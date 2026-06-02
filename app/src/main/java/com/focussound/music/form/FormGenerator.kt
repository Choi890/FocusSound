package com.focussound.music.form

import com.focussound.music.director.MusicEnergyPlanner
import com.focussound.music.director.TaskMusicProfile
import com.focussound.music.model.MusicTask

class FormGenerator(
    private val energyPlanner: MusicEnergyPlanner = MusicEnergyPlanner()
) {
    fun generate(profile: TaskMusicProfile): MusicForm {
        val sectionTypes = when (profile.task) {
            MusicTask.SLEEP -> listOf(
                SectionType.INTRO,
                SectionType.A,
                SectionType.A_VARIATION,
                SectionType.A_VARIATION,
                SectionType.OUTRO
            )
            MusicTask.CODING -> listOf(
                SectionType.INTRO,
                SectionType.A,
                SectionType.A_VARIATION,
                SectionType.B,
                SectionType.BREAK,
                SectionType.A_VARIATION
            )
            MusicTask.WORKOUT -> listOf(
                SectionType.INTRO,
                SectionType.A,
                SectionType.A_VARIATION,
                SectionType.B,
                SectionType.A_VARIATION,
                SectionType.B
            )
            MusicTask.RELAX -> listOf(
                SectionType.INTRO,
                SectionType.A,
                SectionType.B,
                SectionType.A_VARIATION,
                SectionType.OUTRO
            )
            else -> listOf(
                SectionType.INTRO,
                SectionType.A,
                SectionType.A_VARIATION,
                SectionType.B,
                SectionType.A_VARIATION
            )
        }

        val sections = sectionTypes.mapIndexed { index, type ->
            val bars = when (type) {
                SectionType.INTRO -> if (profile.task == MusicTask.SLEEP) 8 else 4
                SectionType.OUTRO -> if (profile.task == MusicTask.SLEEP) 12 else 4
                SectionType.BREAK -> 4
                else -> profile.sectionLengthBars
            }
            MusicSection(
                id = "${type.label} ${index + 1}",
                type = type,
                bars = bars,
                energy = energyPlanner.energyFor(profile.task, type, index),
                variationLevel = when (type) {
                    SectionType.INTRO -> 0.12f
                    SectionType.A -> 0.2f
                    SectionType.A_VARIATION -> 0.48f
                    SectionType.B -> 0.68f
                    SectionType.BREAK -> 0.36f
                    SectionType.OUTRO -> 0.18f
                }
            )
        }
        return MusicForm(sections)
    }
}
