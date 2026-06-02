package com.focussound.playback

import com.focussound.composition.CompositionPatch
import com.focussound.composition.NoteEvent
import com.focussound.composition.bpmToSamples

class NoteScheduler(
    patch: CompositionPatch,
    private val sampleRate: Int
) {
    data class ScheduledNote(
        val event: NoteEvent,
        val startSample: Int,
        val durationSamples: Int
    )

    val loopSamples: Int = bpmToSamples(patch.tempoBpm, patch.totalBeats, sampleRate)
        .coerceAtLeast(sampleRate)

    private val notes = patch.notes
        .map { note ->
            ScheduledNote(
                event = note,
                startSample = bpmToSamples(patch.tempoBpm, note.startBeat, sampleRate).floorMod(loopSamples),
                durationSamples = bpmToSamples(patch.tempoBpm, note.durationBeats, sampleRate).coerceAtLeast(120)
            )
        }
        .sortedBy { it.startSample }

    private var sampleInLoop = 0
    private var nextIndex = 0

    fun nextDueNotes(): List<ScheduledNote> {
        if (sampleInLoop == 0) nextIndex = 0
        val due = mutableListOf<ScheduledNote>()
        while (nextIndex < notes.size && notes[nextIndex].startSample <= sampleInLoop) {
            due += notes[nextIndex]
            nextIndex += 1
        }
        sampleInLoop = (sampleInLoop + 1) % loopSamples
        return due
    }
}

private fun Int.floorMod(other: Int): Int = Math.floorMod(this, other)
