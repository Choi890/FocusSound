package com.focussound.music.generation

import com.focussound.music.model.MusicTask

data class GenerationTemperature(
    val formTemperature: Float,
    val harmonyTemperature: Float,
    val motifTemperature: Float,
    val arrangementTemperature: Float,
    val transitionTemperature: Float
) {
    fun boosted(attempt: Int): GenerationTemperature {
        val boost = (attempt * 0.045f).coerceAtMost(0.28f)
        return copy(
            formTemperature = (formTemperature + boost).coerceIn(0f, 1f),
            harmonyTemperature = (harmonyTemperature + boost).coerceIn(0f, 1f),
            motifTemperature = (motifTemperature + boost).coerceIn(0f, 1f),
            arrangementTemperature = (arrangementTemperature + boost).coerceIn(0f, 1f),
            transitionTemperature = (transitionTemperature + boost).coerceIn(0f, 1f)
        )
    }

    companion object {
        fun fromDiversity(task: MusicTask, diversity: Float): GenerationTemperature {
            val base = diversity.coerceIn(0f, 1f)
            val taskCap = when (task) {
                MusicTask.SLEEP -> 0.42f
                MusicTask.READING -> 0.5f
                MusicTask.STUDY -> 0.62f
                MusicTask.RELAX -> 0.66f
                MusicTask.CODING -> 0.74f
                MusicTask.WORKOUT -> 0.86f
            }
            val value = base.coerceAtMost(taskCap)
            return GenerationTemperature(
                formTemperature = value,
                harmonyTemperature = value * 0.92f,
                motifTemperature = value,
                arrangementTemperature = value * 0.86f,
                transitionTemperature = value * 0.72f
            )
        }
    }
}
