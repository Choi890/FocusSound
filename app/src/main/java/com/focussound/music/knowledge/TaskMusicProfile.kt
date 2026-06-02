package com.focussound.music.knowledge

import com.focussound.instrument.InstrumentRole
import com.focussound.music.model.Density
import com.focussound.music.model.MusicStyle
import com.focussound.music.model.MusicTask

data class TaskMusicProfile(
    val task: MusicTask,
    val meaning: String,
    val tempoRange: IntRange,
    val defaultStyles: List<MusicStyle>,
    val preferredInstruments: List<InstrumentRole>,
    val melodyDensity: Density,
    val rhythmDensity: Density,
    val harmonyComplexity: Density,
    val sectionLengthBars: IntRange,
    val allowedFormTypes: List<FormType>,
    val avoidRules: List<MusicAvoidRule>
)
