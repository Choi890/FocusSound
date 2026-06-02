package com.focussound.music.director

import com.focussound.instrument.InstrumentRole
import com.focussound.music.model.Density
import com.focussound.music.model.MusicStyle
import com.focussound.music.model.MusicTask

data class TaskMusicProfile(
    val task: MusicTask,
    val tempoRange: IntRange,
    val preferredStyles: List<MusicStyle>,
    val preferredInstruments: List<InstrumentRole>,
    val melodyDensity: Density,
    val rhythmDensity: Density,
    val harmonicMotion: Density,
    val sectionLengthBars: Int,
    val avoidSuddenChanges: Boolean = true,
    val avoidSyncopation: Boolean = false
)
