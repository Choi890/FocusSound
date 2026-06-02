package com.focussound.music.generation

import com.focussound.composition.NoteEvent
import com.focussound.music.form.SectionTransitionPlanner
import com.focussound.music.learning.SectionTransitionModel
import kotlin.random.Random

class SectionTransitionSampler(
    private val planner: SectionTransitionPlanner = SectionTransitionPlanner()
) {
    fun sample(
        startBeat: Float,
        energy: Float,
        model: SectionTransitionModel,
        temperature: GenerationTemperature,
        random: Random
    ): List<NoteEvent> {
        val chance = model.transitionNoteProbability + temperature.transitionTemperature * 0.18f
        return if (random.nextFloat() < chance.coerceIn(0f, 0.75f)) {
            planner.transitionNotes(startBeat, energy)
        } else {
            emptyList()
        }
    }
}
