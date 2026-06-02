package com.focussound.music.director

import com.focussound.music.form.SectionType
import com.focussound.music.model.MusicTask

class MusicEnergyPlanner {
    fun energyFor(task: MusicTask, sectionType: SectionType, index: Int): Float {
        val base = when (task) {
            MusicTask.SLEEP -> 0.18f
            MusicTask.READING -> 0.24f
            MusicTask.STUDY -> 0.34f
            MusicTask.RELAX -> 0.38f
            MusicTask.CODING -> 0.46f
            MusicTask.WORKOUT -> 0.72f
        }
        val sectionOffset = when (sectionType) {
            SectionType.INTRO -> -0.12f
            SectionType.A -> 0f
            SectionType.A_VARIATION -> 0.06f
            SectionType.B -> 0.13f
            SectionType.BREAK -> -0.08f
            SectionType.OUTRO -> -0.16f
        }
        val gradualLift = if (task == MusicTask.WORKOUT) index * 0.035f else index * 0.012f
        return (base + sectionOffset + gradualLift).coerceIn(0.05f, 0.95f)
    }
}
