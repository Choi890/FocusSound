package com.focussound.music.knowledge

import com.focussound.instrument.InstrumentRole
import com.focussound.music.model.Density
import com.focussound.music.model.MusicStyle
import com.focussound.music.model.MusicTask

class TaskMusicProfileRepository {
    fun get(task: MusicTask): TaskMusicProfile = when (task) {
        MusicTask.STUDY -> TaskMusicProfile(
            task = task,
            meaning = TaskMeaningKnowledgeBase.meaningFor(task),
            tempoRange = 60..78,
            defaultStyles = listOf(MusicStyle.RELAXING_PIANO, MusicStyle.CLASSICAL_MINIMAL, MusicStyle.AMBIENT),
            preferredInstruments = listOf(InstrumentRole.MELODY, InstrumentRole.PAD, InstrumentRole.BASS),
            melodyDensity = Density.LOW,
            rhythmDensity = Density.VERY_LOW,
            harmonyComplexity = Density.MEDIUM,
            sectionLengthBars = 16..32,
            allowedFormTypes = listOf(FormType.ABA_VARIATION, FormType.CONTINUOUS_VARIATION),
            avoidRules = listOf(
                MusicAvoidRule.LYRICS,
                MusicAvoidRule.BUSY_MELODY,
                MusicAvoidRule.SUDDEN_TRANSITION,
                MusicAvoidRule.AGGRESSIVE_PERCUSSION
            )
        )
        MusicTask.SLEEP -> TaskMusicProfile(
            task = task,
            meaning = TaskMeaningKnowledgeBase.meaningFor(task),
            tempoRange = 50..70,
            defaultStyles = listOf(MusicStyle.SLEEP_DRONE, MusicStyle.RELAXING_PIANO, MusicStyle.ORCHESTRAL_PAD),
            preferredInstruments = listOf(InstrumentRole.MELODY, InstrumentRole.PAD, InstrumentRole.BASS),
            melodyDensity = Density.VERY_LOW,
            rhythmDensity = Density.NONE,
            harmonyComplexity = Density.LOW,
            sectionLengthBars = 32..64,
            allowedFormTypes = listOf(FormType.SLOW_EVOLUTION, FormType.LONG_FADE),
            avoidRules = listOf(
                MusicAvoidRule.LYRICS,
                MusicAvoidRule.PERCUSSION,
                MusicAvoidRule.SYNCOPATION,
                MusicAvoidRule.BRIGHT_HIGH_REGISTER,
                MusicAvoidRule.SUDDEN_TRANSITION
            )
        )
        MusicTask.CODING -> TaskMusicProfile(
            task = task,
            meaning = TaskMeaningKnowledgeBase.meaningFor(task),
            tempoRange = 70..95,
            defaultStyles = listOf(MusicStyle.LOFI, MusicStyle.MINIMAL_ELECTRONIC, MusicStyle.AMBIENT),
            preferredInstruments = listOf(InstrumentRole.MELODY, InstrumentRole.PAD, InstrumentRole.BASS, InstrumentRole.RHYTHM),
            melodyDensity = Density.LOW,
            rhythmDensity = Density.LOW,
            harmonyComplexity = Density.MEDIUM,
            sectionLengthBars = 8..16,
            allowedFormTypes = listOf(FormType.LOOP_WITH_VARIATION, FormType.GROOVE_FORM),
            avoidRules = listOf(
                MusicAvoidRule.LYRICS,
                MusicAvoidRule.BUSY_MELODY,
                MusicAvoidRule.AGGRESSIVE_PERCUSSION
            )
        )
        MusicTask.READING -> TaskMusicProfile(
            task = task,
            meaning = TaskMeaningKnowledgeBase.meaningFor(task),
            tempoRange = 55..72,
            defaultStyles = listOf(MusicStyle.AMBIENT, MusicStyle.ORCHESTRAL_PAD, MusicStyle.CLASSICAL_MINIMAL),
            preferredInstruments = listOf(InstrumentRole.PAD, InstrumentRole.BASS),
            melodyDensity = Density.VERY_LOW,
            rhythmDensity = Density.NONE,
            harmonyComplexity = Density.LOW,
            sectionLengthBars = 24..48,
            allowedFormTypes = listOf(FormType.SLOW_EVOLUTION, FormType.CONTINUOUS_VARIATION),
            avoidRules = listOf(
                MusicAvoidRule.LYRICS,
                MusicAvoidRule.BUSY_MELODY,
                MusicAvoidRule.PERCUSSION,
                MusicAvoidRule.FAST_HARMONIC_RHYTHM
            )
        )
        MusicTask.RELAX -> TaskMusicProfile(
            task = task,
            meaning = TaskMeaningKnowledgeBase.meaningFor(task),
            tempoRange = 60..80,
            defaultStyles = listOf(MusicStyle.RELAXING_PIANO, MusicStyle.ORCHESTRAL_PAD, MusicStyle.AMBIENT),
            preferredInstruments = listOf(InstrumentRole.MELODY, InstrumentRole.PAD, InstrumentRole.BASS),
            melodyDensity = Density.LOW,
            rhythmDensity = Density.VERY_LOW,
            harmonyComplexity = Density.MEDIUM,
            sectionLengthBars = 16..32,
            allowedFormTypes = listOf(FormType.ABA_VARIATION, FormType.ARC_FORM),
            avoidRules = listOf(
                MusicAvoidRule.AGGRESSIVE_PERCUSSION,
                MusicAvoidRule.SUDDEN_TRANSITION
            )
        )
        MusicTask.WORKOUT -> TaskMusicProfile(
            task = task,
            meaning = TaskMeaningKnowledgeBase.meaningFor(task),
            tempoRange = 120..150,
            defaultStyles = listOf(MusicStyle.MINIMAL_ELECTRONIC, MusicStyle.LOFI),
            preferredInstruments = listOf(InstrumentRole.RHYTHM, InstrumentRole.BASS, InstrumentRole.MELODY),
            melodyDensity = Density.MEDIUM,
            rhythmDensity = Density.HIGH,
            harmonyComplexity = Density.LOW,
            sectionLengthBars = 8..16,
            allowedFormTypes = listOf(FormType.BUILD_DROP, FormType.GROOVE_FORM),
            avoidRules = listOf(
                MusicAvoidRule.LONG_STATIC_PAD
            )
        )
    }
}
