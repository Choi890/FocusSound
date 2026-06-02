package com.focussound.music.realtime

import com.focussound.composition.CompositionPatch
import com.focussound.music.form.MusicForm
import com.focussound.music.generation.TaskAwareComposer
import com.focussound.music.model.GeneratedPiece
import com.focussound.music.model.GeneratedSection
import com.focussound.music.model.LiveCompositionRequest

class NextSectionGenerator(
    private val taskAwareComposer: TaskAwareComposer = TaskAwareComposer()
) {
    fun generate(
        request: LiveCompositionRequest,
        memory: MusicMemory,
        startBar: Int,
        minBars: Int = DEFAULT_BARS
    ): QueuedMusicSegment {
        val targetBars = minBars.coerceIn(MIN_BARS, MAX_BARS)
        val nextRequest = request.copy(
            diversity = (request.diversity + memory.recentFingerprints.size * 0.015f).coerceIn(0.18f, 0.95f),
            targetDurationMinutes = null
        )
        val piece = clipToBars(taskAwareComposer.generatePiece(nextRequest), targetBars)
        val patch = piece
            .toCompositionPatch("${request.task.label} 다음 구간")
            .shifted(startBar)
        return QueuedMusicSegment(
            startBar = startBar,
            endBar = startBar + piece.form.totalBars,
            piece = piece,
            patch = patch
        )
    }

    private fun clipToBars(piece: GeneratedPiece, targetBars: Int): GeneratedPiece {
        var usedBars = 0
        val sections = mutableListOf<GeneratedSection>()
        val chords = mutableListOf<com.focussound.composition.Chord>()
        val notes = mutableListOf<com.focussound.composition.NoteEvent>()

        for (section in piece.sections) {
            if (usedBars >= targetBars) break
            val keepBars = minOf(section.section.bars, targetBars - usedBars)
            if (keepBars <= 0) break
            val sectionEndBeat = (section.startBar + keepBars) * BEATS_PER_BAR
            val clippedChords = section.chords.take(keepBars.coerceAtLeast(1))
            val clippedNotes = section.notes.filter { it.startBeat < sectionEndBeat }
            val clippedSection = section.copy(
                section = section.section.copy(bars = keepBars),
                chords = clippedChords,
                notes = clippedNotes
            )
            sections += clippedSection
            chords += clippedChords
            notes += clippedNotes
            usedBars += keepBars
        }

        if (sections.isEmpty()) return piece
        return piece.copy(
            form = MusicForm(sections.map { it.section }),
            chords = chords,
            notes = notes,
            sections = sections
        )
    }

    private fun CompositionPatch.shifted(startBar: Int): CompositionPatch {
        if (startBar <= 0) return this
        val offsetBeats = startBar * BEATS_PER_BAR
        return copy(notes = notes.map { it.copy(startBeat = it.startBeat + offsetBeats) })
    }

    private companion object {
        const val BEATS_PER_BAR = 4f
        const val DEFAULT_BARS = 12
        const val MIN_BARS = 8
        const val MAX_BARS = 16
    }
}
