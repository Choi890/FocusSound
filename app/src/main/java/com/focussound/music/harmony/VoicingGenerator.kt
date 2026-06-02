package com.focussound.music.harmony

import com.focussound.composition.Chord
import com.focussound.composition.MusicalKey
import com.focussound.music.model.MusicTask
import kotlin.random.Random

class VoicingGenerator {
    fun padVoicing(key: MusicalKey, chord: Chord, task: MusicTask, random: Random): List<Int> {
        val octave = when (task) {
            MusicTask.SLEEP, MusicTask.READING -> 3
            MusicTask.WORKOUT -> 4
            else -> if (random.nextBoolean()) 3 else 4
        }
        return chord.midiNotes(key, octave)
            .mapIndexed { index, note -> note + if (index == 0 && task != MusicTask.SLEEP) -12 else 0 }
            .distinct()
            .sorted()
    }

    fun pianoVoicing(key: MusicalKey, chord: Chord, random: Random): List<Int> {
        val base = chord.midiNotes(key, octave = 4)
        return when (random.nextInt(3)) {
            0 -> base
            1 -> listOf(base[0] - 12, base[1], base[2])
            else -> base + listOf(base.first() + 12)
        }.distinct().sorted()
    }
}
