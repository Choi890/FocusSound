package com.focussound.music.learning

import com.focussound.music.knowledge.TaskMusicProfile
import com.focussound.music.model.MusicStyle
import com.focussound.music.model.MusicTask

class HarmonyStatisticsExtractor {
    fun extract(profile: TaskMusicProfile, style: MusicStyle, corpus: List<SymbolicMusicDocument>): HarmonyModel {
        val corpusTemplates = corpus
            .mapNotNull { it.chordDegrees.takeIf { degrees -> degrees.size >= 4 } }
            .map { WeightedDegreeTemplate(it, 0.25f) }
        return HarmonyModel(
            degreeTemplates = corpusTemplates + builtInDegreeTemplates(profile.task, style),
            cadenceDegrees = when (profile.task) {
                MusicTask.SLEEP, MusicTask.READING -> listOf(0, 3, 4)
                MusicTask.WORKOUT -> listOf(4, 0)
                else -> listOf(4, 5, 0)
            },
            extensionProbability = when (profile.task) {
                MusicTask.SLEEP -> 0.62f
                MusicTask.READING -> 0.5f
                MusicTask.WORKOUT -> 0.18f
                else -> 0.42f
            }
        )
    }

    private fun builtInDegreeTemplates(task: MusicTask, style: MusicStyle): List<WeightedDegreeTemplate> = when {
        task == MusicTask.SLEEP -> listOf(
            WeightedDegreeTemplate(listOf(0, 5, 3, 4), 0.28f),
            WeightedDegreeTemplate(listOf(0, 3, 0, 4), 0.25f),
            WeightedDegreeTemplate(listOf(0, 2, 4, 0), 0.22f),
            WeightedDegreeTemplate(listOf(5, 3, 0, 4), 0.18f)
        )
        task == MusicTask.READING -> listOf(
            WeightedDegreeTemplate(listOf(0, 3, 5, 4), 0.3f),
            WeightedDegreeTemplate(listOf(0, 5, 3, 0), 0.25f),
            WeightedDegreeTemplate(listOf(3, 0, 4, 5), 0.2f)
        )
        task == MusicTask.CODING || style == MusicStyle.LOFI -> listOf(
            WeightedDegreeTemplate(listOf(0, 5, 2, 4), 0.25f),
            WeightedDegreeTemplate(listOf(0, 3, 5, 4), 0.25f),
            WeightedDegreeTemplate(listOf(5, 4, 0, 2), 0.2f),
            WeightedDegreeTemplate(listOf(0, 2, 3, 4), 0.16f)
        )
        task == MusicTask.WORKOUT -> listOf(
            WeightedDegreeTemplate(listOf(0, 6, 5, 4), 0.3f),
            WeightedDegreeTemplate(listOf(0, 3, 4, 4), 0.24f),
            WeightedDegreeTemplate(listOf(0, 5, 3, 4), 0.2f)
        )
        style == MusicStyle.RELAXING_PIANO -> listOf(
            WeightedDegreeTemplate(listOf(0, 4, 5, 3), 0.28f),
            WeightedDegreeTemplate(listOf(5, 3, 0, 4), 0.24f),
            WeightedDegreeTemplate(listOf(0, 2, 4, 5), 0.2f)
        )
        else -> listOf(
            WeightedDegreeTemplate(listOf(0, 3, 5, 4), 0.28f),
            WeightedDegreeTemplate(listOf(3, 0, 5, 4), 0.24f),
            WeightedDegreeTemplate(listOf(0, 4, 2, 5), 0.2f)
        )
    }
}
