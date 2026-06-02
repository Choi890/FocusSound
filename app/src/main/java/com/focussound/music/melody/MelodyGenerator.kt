package com.focussound.music.melody

import com.focussound.composition.Chord
import com.focussound.composition.MusicalKey
import com.focussound.composition.NoteEvent
import com.focussound.composition.NoteLane
import com.focussound.music.form.MusicSection
import com.focussound.music.model.LiveCompositionRequest
import com.focussound.music.model.MusicTask
import kotlin.math.abs
import kotlin.random.Random

class MelodyGenerator(
    private val variationEngine: PhraseVariationEngine = PhraseVariationEngine()
) {
    fun generate(
        request: LiveCompositionRequest,
        key: MusicalKey,
        section: MusicSection,
        chords: List<Chord>,
        motif: Motif,
        startBeat: Float,
        random: Random
    ): List<NoteEvent> {
        val amount = when (request.task) {
            MusicTask.SLEEP -> request.melodyAmount * 0.25f
            MusicTask.READING -> request.melodyAmount * 0.35f
            MusicTask.WORKOUT -> request.melodyAmount * 1.45f
            else -> request.melodyAmount
        }.coerceIn(0f, 1f)
        if (amount < 0.08f) return emptyList()

        val varied = variationEngine.vary(motif, request.task, section.type, random)
        val notes = mutableListOf<NoteEvent>()
        var beat = startBeat + if (request.task == MusicTask.WORKOUT) 0f else 2f
        val endBeat = startBeat + section.bars * 4f
        var previous = key.midiNote(2, if (request.task == MusicTask.READING) 4 else 5)
        var motifRun = 0

        while (beat < endBeat) {
            if (random.nextFloat() > amount && request.task != MusicTask.WORKOUT) {
                beat += 4f
                continue
            }
            varied.degrees.forEachIndexed { index, degree ->
                if (beat >= endBeat) return@forEachIndexed
                val rhythm = varied.rhythms[index % varied.rhythms.size].coerceAtLeast(0.5f)
                val chord = chords[((beat - startBeat) / 4f).toInt().coerceIn(0, chords.lastIndex)]
                val candidate = if ((beat - startBeat).toInt() % 4 == 0) {
                    chordToneNear(chord, key, previous, random)
                } else {
                    key.midiNote(degree + motifRun % 2, if (request.task == MusicTask.READING) 4 else 5)
                }
                val limited = limitLeap(previous, candidate)
                notes += NoteEvent(
                    startBeat = beat,
                    durationBeats = (rhythm * 0.72f).coerceIn(0.35f, 4f),
                    midiNote = limited,
                    velocity = (0.24f + section.energy * 0.24f + amount * 0.14f).coerceIn(0.2f, 0.58f),
                    lane = NoteLane.MELODY
                )
                previous = limited
                beat += rhythm
            }
            motifRun += 1
            if (request.task != MusicTask.WORKOUT) beat += listOf(1f, 2f, 4f).random(random)
        }
        return notes
    }

    private fun chordToneNear(chord: Chord, key: MusicalKey, previous: Int, random: Random): Int {
        val candidates = chord.midiNotes(key, 5).flatMap { listOf(it - 12, it, it + 12) }.filter { it in 55..79 }
        return candidates.sortedBy { abs(it - previous) }.take(3).random(random)
    }

    private fun limitLeap(previous: Int, candidate: Int): Int {
        var adjusted = candidate
        while (adjusted - previous > 7) adjusted -= 12
        while (previous - adjusted > 7) adjusted += 12
        return adjusted.coerceIn(52, 81)
    }
}
