package com.focussound.music.learning

import com.focussound.music.knowledge.TaskMusicProfile
import com.focussound.music.model.MusicTask

class MotifStatisticsExtractor {
    fun extract(profile: TaskMusicProfile, corpus: List<SymbolicMusicDocument>): MotifModel {
        val corpusTemplates = corpus
            .mapNotNull { document ->
                val intervals = document.melodyIntervals.takeIf { it.size >= 3 } ?: return@mapNotNull null
                val rhythms = document.rhythmValues.takeIf { it.size >= 3 } ?: listOf(1f, 1f, 2f, 4f)
                WeightedMotifTemplate(intervals, rhythms, 0.25f)
            }
        return MotifModel(corpusTemplates + builtInMotifs(profile.task))
    }

    private fun builtInMotifs(task: MusicTask): List<WeightedMotifTemplate> = when (task) {
        MusicTask.SLEEP -> listOf(
            WeightedMotifTemplate(listOf(0, 2, -1), listOf(4f, 4f, 8f), 0.35f),
            WeightedMotifTemplate(listOf(0, -2, 1), listOf(6f, 4f, 6f), 0.25f)
        )
        MusicTask.READING -> listOf(
            WeightedMotifTemplate(listOf(0, 2, -2), listOf(6f, 4f, 6f), 0.35f),
            WeightedMotifTemplate(listOf(0, -1, 2), listOf(8f, 4f, 4f), 0.25f)
        )
        MusicTask.WORKOUT -> listOf(
            WeightedMotifTemplate(listOf(0, 0, 2, -1), listOf(1f, 1f, 1f, 1f), 0.34f),
            WeightedMotifTemplate(listOf(0, 2, 2, -2), listOf(0.5f, 0.5f, 1f, 2f), 0.28f)
        )
        MusicTask.CODING -> listOf(
            WeightedMotifTemplate(listOf(0, 2, -1, 1), listOf(1f, 1f, 2f, 4f), 0.28f),
            WeightedMotifTemplate(listOf(0, -2, 3, -1), listOf(2f, 1f, 1f, 4f), 0.24f),
            WeightedMotifTemplate(listOf(0, 1, 2, -3), listOf(1.5f, 0.5f, 2f, 4f), 0.2f)
        )
        else -> listOf(
            WeightedMotifTemplate(listOf(0, 2, 2, -2), listOf(1f, 1f, 2f, 4f), 0.28f),
            WeightedMotifTemplate(listOf(0, -1, -2, 3), listOf(2f, 1f, 1f, 4f), 0.24f),
            WeightedMotifTemplate(listOf(0, 1, 2, 2), listOf(1.5f, 0.5f, 2f, 4f), 0.2f)
        )
    }
}
