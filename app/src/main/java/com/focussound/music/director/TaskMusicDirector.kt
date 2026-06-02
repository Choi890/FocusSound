package com.focussound.music.director

import com.focussound.instrument.InstrumentRole
import com.focussound.music.model.Density
import com.focussound.music.model.LiveCompositionRequest
import com.focussound.music.model.MusicStyle
import com.focussound.music.model.MusicTask
import kotlin.random.Random

class TaskMusicDirector {
    fun profileFor(task: MusicTask): TaskMusicProfile = when (task) {
        MusicTask.STUDY -> TaskMusicProfile(
            task = task,
            tempoRange = 60..78,
            preferredStyles = listOf(MusicStyle.RELAXING_PIANO, MusicStyle.CLASSICAL_MINIMAL, MusicStyle.AMBIENT),
            preferredInstruments = listOf(InstrumentRole.MELODY, InstrumentRole.PAD, InstrumentRole.BASS),
            melodyDensity = Density.LOW,
            rhythmDensity = Density.VERY_LOW,
            harmonicMotion = Density.LOW,
            sectionLengthBars = 16
        )
        MusicTask.SLEEP -> TaskMusicProfile(
            task = task,
            tempoRange = 50..70,
            preferredStyles = listOf(MusicStyle.SLEEP_DRONE, MusicStyle.RELAXING_PIANO, MusicStyle.AMBIENT),
            preferredInstruments = listOf(InstrumentRole.MELODY, InstrumentRole.PAD),
            melodyDensity = Density.VERY_LOW,
            rhythmDensity = Density.NONE,
            harmonicMotion = Density.VERY_LOW,
            sectionLengthBars = 32,
            avoidSyncopation = true
        )
        MusicTask.CODING -> TaskMusicProfile(
            task = task,
            tempoRange = 70..95,
            preferredStyles = listOf(MusicStyle.LOFI, MusicStyle.AMBIENT, MusicStyle.MINIMAL_ELECTRONIC),
            preferredInstruments = listOf(InstrumentRole.MELODY, InstrumentRole.PAD, InstrumentRole.BASS, InstrumentRole.RHYTHM),
            melodyDensity = Density.LOW,
            rhythmDensity = Density.LOW,
            harmonicMotion = Density.LOW,
            sectionLengthBars = 8
        )
        MusicTask.READING -> TaskMusicProfile(
            task = task,
            tempoRange = 55..72,
            preferredStyles = listOf(MusicStyle.ORCHESTRAL_PAD, MusicStyle.AMBIENT, MusicStyle.SLEEP_DRONE),
            preferredInstruments = listOf(InstrumentRole.PAD, InstrumentRole.BASS),
            melodyDensity = Density.VERY_LOW,
            rhythmDensity = Density.NONE,
            harmonicMotion = Density.VERY_LOW,
            sectionLengthBars = 24,
            avoidSyncopation = true
        )
        MusicTask.RELAX -> TaskMusicProfile(
            task = task,
            tempoRange = 60..80,
            preferredStyles = listOf(MusicStyle.RELAXING_PIANO, MusicStyle.ORCHESTRAL_PAD, MusicStyle.CLASSICAL_MINIMAL),
            preferredInstruments = listOf(InstrumentRole.MELODY, InstrumentRole.PAD, InstrumentRole.BASS),
            melodyDensity = Density.LOW,
            rhythmDensity = Density.VERY_LOW,
            harmonicMotion = Density.MEDIUM,
            sectionLengthBars = 16
        )
        MusicTask.WORKOUT -> TaskMusicProfile(
            task = task,
            tempoRange = 120..150,
            preferredStyles = listOf(MusicStyle.MINIMAL_ELECTRONIC, MusicStyle.LOFI),
            preferredInstruments = listOf(InstrumentRole.RHYTHM, InstrumentRole.BASS, InstrumentRole.MELODY),
            melodyDensity = Density.MEDIUM,
            rhythmDensity = Density.HIGH,
            harmonicMotion = Density.LOW,
            sectionLengthBars = 8,
            avoidSuddenChanges = false
        )
    }

    fun chooseTempo(request: LiveCompositionRequest, random: Random): Int {
        val profile = profileFor(request.task)
        val min = profile.tempoRange.first
        val max = profile.tempoRange.last
        val center = ((min + max) / 2f).toInt()
        val spread = ((max - min) * request.diversity.coerceIn(0.1f, 1f)).toInt().coerceAtLeast(2)
        return random.nextInt(center - spread / 2, center + spread / 2 + 1).coerceIn(min, max)
    }
}
