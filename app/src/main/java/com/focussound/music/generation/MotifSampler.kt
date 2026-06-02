package com.focussound.music.generation

import com.focussound.music.learning.MotifModel
import com.focussound.music.melody.Motif
import com.focussound.music.model.MusicTask
import kotlin.random.Random

class MotifSampler {
    fun sample(
        task: MusicTask,
        model: MotifModel,
        temperature: GenerationTemperature,
        random: Random
    ): Motif {
        val template = model.contourTemplates.weightedRandom(random) {
            it.probability + random.nextFloat() * temperature.motifTemperature
        }
        var degree = 0
        val degrees = template.intervals.map { interval ->
            degree = (degree + interval).coerceIn(-3, 8)
            degree
        }
        val stretchedRhythms = when (task) {
            MusicTask.SLEEP -> template.rhythms.map { it * 1.35f }
            MusicTask.READING -> template.rhythms.map { it * 1.2f }
            MusicTask.WORKOUT -> template.rhythms.map { (it * 0.75f).coerceAtLeast(0.5f) }
            else -> template.rhythms
        }
        return Motif(
            degrees = if (random.nextFloat() < temperature.motifTemperature * 0.35f) degrees.reversed() else degrees,
            rhythms = stretchedRhythms
        )
    }
}
