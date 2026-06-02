package com.focussound.music.harmony

import com.focussound.composition.Chord
import com.focussound.composition.ChordExtension
import com.focussound.composition.ChordQuality
import com.focussound.composition.MusicalKey
import com.focussound.composition.PitchClass
import com.focussound.composition.ScaleType
import com.focussound.music.model.MusicStyle
import com.focussound.music.model.MusicTask
import kotlin.random.Random

class ChordProgressionLibrary {
    fun chooseKey(task: MusicTask, style: MusicStyle, random: Random): MusicalKey {
        val candidates = when {
            task == MusicTask.SLEEP -> listOf(
                MusicalKey(PitchClass.C, ScaleType.AMBIENT),
                MusicalKey(PitchClass.D, ScaleType.AMBIENT),
                MusicalKey(PitchClass.A, ScaleType.MINOR)
            )
            task == MusicTask.WORKOUT -> listOf(
                MusicalKey(PitchClass.A, ScaleType.MINOR),
                MusicalKey(PitchClass.D, ScaleType.DORIAN),
                MusicalKey(PitchClass.G, ScaleType.MINOR)
            )
            style == MusicStyle.RELAXING_PIANO -> listOf(
                MusicalKey(PitchClass.D, ScaleType.MINOR),
                MusicalKey(PitchClass.C, ScaleType.MAJOR),
                MusicalKey(PitchClass.G, ScaleType.MAJOR)
            )
            style == MusicStyle.ORCHESTRAL_PAD -> listOf(
                MusicalKey(PitchClass.F, ScaleType.MAJOR),
                MusicalKey(PitchClass.D, ScaleType.DORIAN),
                MusicalKey(PitchClass.C, ScaleType.MAJOR)
            )
            else -> listOf(
                MusicalKey(PitchClass.D, ScaleType.DORIAN),
                MusicalKey(PitchClass.A, ScaleType.MINOR),
                MusicalKey(PitchClass.E, ScaleType.MINOR)
            )
        }
        return candidates.random(random)
    }

    fun progression(task: MusicTask, style: MusicStyle, key: MusicalKey, random: Random): List<Chord> {
        val degrees = templates(task, style).random(random)
        return degrees.mapIndexed { index, degree ->
            val root = key.root.transpose(key.scaleSemitones()[Math.floorMod(degree, key.scaleSemitones().size)])
            val quality = qualityFor(key.scaleType, degree)
            val extension = when {
                task == MusicTask.SLEEP -> if (index % 2 == 0) ChordExtension.NINTH else ChordExtension.SEVENTH
                style == MusicStyle.RELAXING_PIANO -> listOf(ChordExtension.SIXTH, ChordExtension.SEVENTH, ChordExtension.NINTH).random(random)
                style == MusicStyle.ORCHESTRAL_PAD -> listOf(ChordExtension.SEVENTH, ChordExtension.NINTH).random(random)
                else -> if (random.nextFloat() > 0.45f) ChordExtension.SEVENTH else ChordExtension.NONE
            }
            Chord(root = root, quality = quality, extension = extension, degree = degree)
        }
    }

    private fun templates(task: MusicTask, style: MusicStyle): List<List<Int>> = when {
        task == MusicTask.SLEEP -> listOf(
            listOf(0, 5, 3, 4),
            listOf(0, 0, 4, 3),
            listOf(0, 2, 4, 0),
            listOf(0, 3, 0, 4)
        )
        task == MusicTask.WORKOUT -> listOf(
            listOf(0, 5, 3, 4),
            listOf(0, 6, 5, 4),
            listOf(0, 2, 3, 4)
        )
        style == MusicStyle.RELAXING_PIANO -> listOf(
            listOf(0, 4, 5, 3),
            listOf(5, 3, 0, 4),
            listOf(0, 2, 4, 5),
            listOf(0, 5, 3, 4)
        )
        style == MusicStyle.LOFI -> listOf(
            listOf(0, 5, 2, 4),
            listOf(0, 3, 5, 4),
            listOf(5, 4, 0, 2)
        )
        else -> listOf(
            listOf(0, 3, 5, 4),
            listOf(0, 4, 2, 5),
            listOf(0, 2, 4, 5),
            listOf(3, 0, 5, 4)
        )
    }

    private fun qualityFor(scaleType: ScaleType, degree: Int): ChordQuality = when (scaleType) {
        ScaleType.MAJOR -> if (degree in listOf(1, 2, 5)) ChordQuality.MINOR else ChordQuality.MAJOR
        ScaleType.MINOR, ScaleType.AMBIENT -> if (degree in listOf(2, 5)) ChordQuality.MAJOR else ChordQuality.MINOR
        ScaleType.DORIAN -> if (degree in listOf(0, 1, 4)) ChordQuality.MINOR else ChordQuality.MAJOR
        ScaleType.PENTATONIC -> ChordQuality.SUS2
    }
}
