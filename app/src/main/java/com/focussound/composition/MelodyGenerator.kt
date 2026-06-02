package com.focussound.composition

import com.focussound.data.FocusMode
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

class MelodyGenerator {
    fun generate(key: MusicalKey, intent: CompositionIntent): List<NoteEvent> {
        return generate(key, emptyList(), intent, Random.Default)
    }

    fun generate(key: MusicalKey, chords: List<Chord>, intent: CompositionIntent): List<NoteEvent> {
        return generate(key, chords, intent, Random.Default)
    }

    fun generate(
        key: MusicalKey,
        chords: List<Chord>,
        intent: CompositionIntent,
        random: Random
    ): List<NoteEvent> {
        val density = intent.melodyDensityHint ?: 0.22f
        if (density < 0.08f || intent.mode == FocusMode.SLEEP || intent.genre == CompositionGenre.SLEEP_DRONE) {
            return emptyList()
        }

        val motifPool = motifsFor(intent.genre)
        val targetCount = (3 + density * 30f + random.nextInt(0, 4)).roundToInt().coerceIn(4, 18)
        val octave = when (intent.mode) {
            FocusMode.READING -> 4
            FocusMode.STUDY -> if (random.nextBoolean()) 4 else 5
            else -> 5
        }
        val phraseStarts = when {
            density > 0.32f -> listOf(0f, 0.5f, 1f, 2f)
            else -> listOf(1f, 2f, 3f)
        }
        val restChance = (0.36f - density * 0.45f).coerceIn(0.06f, 0.32f)
        val velocityBase = (0.2f + density * 0.32f).coerceIn(0.2f, 0.5f)

        val notes = mutableListOf<NoteEvent>()
        var beat = phraseStarts.random(random)
        var previousNote = key.midiNote(random.nextInt(1, 5), octave)
        var motifIndex = random.nextInt(motifPool.size)

        while (beat < 16f && notes.size < targetCount) {
            val motif = motifPool[motifIndex % motifPool.size]
            val transpose = random.nextInt(-2, 3)
            val rhythmStretch = random.nextFloatIn(0.86f, 1.22f)

            motif.degrees.forEachIndexed { index, degree ->
                if (beat >= 16f || notes.size >= targetCount) return@forEachIndexed

                val stepBeats = (motif.rhythm[index % motif.rhythm.size] * rhythmStretch)
                    .coerceIn(0.5f, 4f)
                val shouldRest = random.nextFloat() < restChance && notes.isNotEmpty()
                if (!shouldRest) {
                    val chord = chordAt(chords, beat)
                    val candidate = if (isStrongBeat(beat) && chord != null && random.nextFloat() < 0.74f) {
                        chordToneNear(chord, key, octave, previousNote, random)
                    } else {
                        key.midiNote(degree + transpose, octave)
                    }
                    val limited = limitLeap(previousNote, candidate, intent)
                    val noteDuration = (stepBeats * random.nextFloatIn(0.56f, 0.9f)).coerceAtLeast(0.35f)
                    notes += NoteEvent(
                        startBeat = beat,
                        durationBeats = noteDuration,
                        midiNote = limited,
                        velocity = (velocityBase * random.nextFloatIn(0.82f, 1.08f)).coerceIn(0.18f, 0.52f),
                        lane = NoteLane.MELODY
                    )
                    previousNote = limited
                }
                beat += stepBeats
            }

            motifIndex += random.nextInt(1, 4)
            if (random.nextFloat() < 0.28f) {
                beat += listOf(0.5f, 1f, 2f).random(random)
            }
        }

        return notes
    }

    private fun motifsFor(genre: CompositionGenre): List<MelodyMotif> = when (genre) {
        CompositionGenre.CLASSICAL_MINIMAL -> listOf(
            MelodyMotif(listOf(0, 2, 4, 2), listOf(1f, 1f, 2f, 2f)),
            MelodyMotif(listOf(4, 3, 1, 2), listOf(2f, 1f, 1f, 2f)),
            MelodyMotif(listOf(0, 1, 3, 5), listOf(1.5f, 0.5f, 2f, 2f)),
            MelodyMotif(listOf(5, 4, 2, 0), listOf(2f, 2f, 1f, 1f)),
            MelodyMotif(listOf(2, 4, 5, 3, 1), listOf(1f, 1f, 1f, 1f, 2f)),
            MelodyMotif(listOf(0, 3, 2, 4), listOf(2f, 1f, 1f, 2f))
        )
        CompositionGenre.LOFI -> listOf(
            MelodyMotif(listOf(0, 2, 3, 2), listOf(0.75f, 0.75f, 1.5f, 1f)),
            MelodyMotif(listOf(4, 2, 0, 1), listOf(1f, 0.5f, 1.5f, 1f)),
            MelodyMotif(listOf(2, 4, 6, 4), listOf(0.5f, 1f, 1.5f, 1f)),
            MelodyMotif(listOf(5, 4, 2, 3, 1), listOf(1f, 0.5f, 0.5f, 1f, 2f)),
            MelodyMotif(listOf(0, 1, 4, 2), listOf(1.5f, 0.5f, 1f, 1f))
        )
        CompositionGenre.ORCHESTRAL_PAD -> listOf(
            MelodyMotif(listOf(0, 4, 5), listOf(2f, 2f, 4f)),
            MelodyMotif(listOf(5, 3, 2, 0), listOf(2f, 1.5f, 0.5f, 4f)),
            MelodyMotif(listOf(2, 4, 6, 5), listOf(2f, 2f, 2f, 2f)),
            MelodyMotif(listOf(4, 2, 0), listOf(3f, 1f, 4f))
        )
        CompositionGenre.AMBIENT_CODING -> listOf(
            MelodyMotif(listOf(0, 2, 4, 6), listOf(2f, 1f, 1f, 2f)),
            MelodyMotif(listOf(4, 5, 3, 2), listOf(1f, 1f, 2f, 2f)),
            MelodyMotif(listOf(2, 0, 3, 4), listOf(2f, 2f, 1f, 1f)),
            MelodyMotif(listOf(5, 4, 2, 1), listOf(1.5f, 0.5f, 2f, 2f)),
            MelodyMotif(listOf(0, 3, 5), listOf(2f, 2f, 4f))
        )
        CompositionGenre.SLEEP_DRONE -> emptyList()
    }

    private fun chordAt(chords: List<Chord>, beat: Float): Chord? {
        if (chords.isEmpty()) return null
        return chords[(beat / 4f).toInt().coerceIn(0, chords.lastIndex)]
    }

    private fun chordToneNear(
        chord: Chord,
        key: MusicalKey,
        octave: Int,
        previousNote: Int,
        random: Random
    ): Int {
        val candidates = chord.midiNotes(key, octave)
            .flatMap { note -> listOf(note - 12, note, note + 12) }
            .filter { it in 55..79 }
        val close = candidates.sortedBy { abs(it - previousNote) }.take(3)
        return close.random(random)
    }

    private fun isStrongBeat(beat: Float): Boolean {
        val beatInBar = beat % 4f
        return abs(beatInBar) < 0.05f || abs(beatInBar - 2f) < 0.05f
    }

    private fun limitLeap(previous: Int, candidate: Int, intent: CompositionIntent): Int {
        var adjusted = candidate
        val maxLeap = if (intent.genre == CompositionGenre.LOFI) 8 else 7
        while (adjusted - previous > maxLeap) adjusted -= 12
        while (previous - adjusted > maxLeap) adjusted += 12
        return if (abs(adjusted - previous) > maxLeap) previous else adjusted.coerceIn(55, 79)
    }
}

private data class MelodyMotif(
    val degrees: List<Int>,
    val rhythm: List<Float>
)

private fun Random.nextFloatIn(min: Float, max: Float): Float {
    return min + (max - min) * nextFloat()
}
