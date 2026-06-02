package com.focussound.composition

import com.focussound.data.FocusMode
import kotlin.random.Random

class ChordProgressionGenerator {
    fun generate(intent: CompositionIntent): Pair<MusicalKey, List<Chord>> {
        return generate(intent, Random.Default)
    }

    fun generate(intent: CompositionIntent, random: Random): Pair<MusicalKey, List<Chord>> {
        val key = intent.keyHint ?: defaultKeys(intent).random(random)
        val degrees = progressionTemplates(intent).random(random)
        val chords = degrees.map { degree ->
            val root = key.root.transpose(key.scaleSemitones()[degree.floorMod(key.scaleSemitones().size)])
            val quality = when (key.scaleType) {
                ScaleType.MAJOR -> if (degree in listOf(1, 2, 5)) ChordQuality.MINOR else ChordQuality.MAJOR
                ScaleType.MINOR, ScaleType.AMBIENT -> if (degree in listOf(2, 5)) ChordQuality.MAJOR else ChordQuality.MINOR
                ScaleType.DORIAN -> if (degree in listOf(0, 1, 4)) ChordQuality.MINOR else ChordQuality.MAJOR
                ScaleType.PENTATONIC -> ChordQuality.SUS2
            }
            val extension = when {
                intent.harmonicComplexityHint.orDefault(0.3f) > 0.52f && random.nextFloat() > 0.2f -> {
                    listOf(ChordExtension.SEVENTH, ChordExtension.NINTH).random(random)
                }
                intent.harmonicComplexityHint.orDefault(0.3f) > 0.3f && random.nextFloat() > 0.35f -> {
                    listOf(ChordExtension.SIXTH, ChordExtension.SEVENTH).random(random)
                }
                else -> ChordExtension.NONE
            }
            Chord(root = root, quality = quality, extension = extension, degree = degree)
        }
        return key to chords
    }

    private fun defaultKeys(intent: CompositionIntent): List<MusicalKey> = when {
        intent.mode == FocusMode.SLEEP -> listOf(
            MusicalKey(PitchClass.C, ScaleType.AMBIENT),
            MusicalKey(PitchClass.D, ScaleType.AMBIENT),
            MusicalKey(PitchClass.G, ScaleType.PENTATONIC),
            MusicalKey(PitchClass.A, ScaleType.AMBIENT)
        )
        intent.genre == CompositionGenre.CLASSICAL_MINIMAL -> listOf(
            MusicalKey(PitchClass.D, ScaleType.MINOR),
            MusicalKey(PitchClass.A, ScaleType.MINOR),
            MusicalKey(PitchClass.E, ScaleType.MINOR),
            MusicalKey(PitchClass.G, ScaleType.MAJOR),
            MusicalKey(PitchClass.C, ScaleType.MAJOR)
        )
        intent.genre == CompositionGenre.ORCHESTRAL_PAD -> listOf(
            MusicalKey(PitchClass.F, ScaleType.MAJOR),
            MusicalKey(PitchClass.C, ScaleType.MAJOR),
            MusicalKey(PitchClass.G, ScaleType.MAJOR),
            MusicalKey(PitchClass.D, ScaleType.DORIAN)
        )
        intent.genre == CompositionGenre.LOFI -> listOf(
            MusicalKey(PitchClass.A, ScaleType.MINOR),
            MusicalKey(PitchClass.D, ScaleType.MINOR),
            MusicalKey(PitchClass.E, ScaleType.DORIAN),
            MusicalKey(PitchClass.G, ScaleType.MAJOR)
        )
        else -> listOf(
            MusicalKey(PitchClass.D, ScaleType.DORIAN),
            MusicalKey(PitchClass.A, ScaleType.MINOR),
            MusicalKey(PitchClass.E, ScaleType.MINOR),
            MusicalKey(PitchClass.G, ScaleType.DORIAN)
        )
    }

    private fun progressionTemplates(intent: CompositionIntent): List<List<Int>> = when (intent.genre) {
        CompositionGenre.SLEEP_DRONE -> listOf(
            listOf(0, 0, 4, 3),
            listOf(0, 3, 0, 4),
            listOf(0, 4, 2, 0),
            listOf(0, 0, 1, 4),
            listOf(0, 2, 4, 0)
        )
        CompositionGenre.CLASSICAL_MINIMAL -> listOf(
            listOf(0, 4, 5, 3),
            listOf(0, 2, 4, 0),
            listOf(0, 5, 3, 4),
            listOf(0, 3, 4, 5),
            listOf(0, 4, 0, 3),
            listOf(0, 2, 5, 4),
            listOf(5, 3, 4, 0),
            listOf(0, 6, 4, 5)
        )
        CompositionGenre.ORCHESTRAL_PAD -> listOf(
            listOf(0, 5, 3, 4),
            listOf(0, 3, 5, 4),
            listOf(0, 4, 2, 5),
            listOf(3, 0, 4, 5),
            listOf(0, 2, 3, 5),
            listOf(4, 5, 0, 3)
        )
        CompositionGenre.LOFI -> listOf(
            listOf(0, 5, 2, 4),
            listOf(0, 3, 5, 4),
            listOf(0, 4, 5, 3),
            listOf(5, 4, 0, 2),
            listOf(0, 2, 3, 4),
            listOf(3, 5, 4, 0),
            listOf(0, 6, 5, 4)
        )
        CompositionGenre.AMBIENT_CODING -> listOf(
            listOf(0, 3, 5, 4),
            listOf(0, 4, 2, 5),
            listOf(0, 5, 4, 3),
            listOf(0, 2, 4, 5),
            listOf(3, 0, 5, 4),
            listOf(0, 4, 0, 5)
        )
    }
}

private fun Int.floorMod(other: Int): Int = Math.floorMod(this, other)
private fun Float?.orDefault(default: Float): Float = this ?: default
