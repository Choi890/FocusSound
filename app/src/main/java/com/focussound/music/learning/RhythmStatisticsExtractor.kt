package com.focussound.music.learning

import com.focussound.music.knowledge.TaskMusicProfile
import com.focussound.music.model.MusicTask

class RhythmStatisticsExtractor {
    fun extract(profile: TaskMusicProfile, corpus: List<SymbolicMusicDocument>): RhythmModel {
        val values = corpus.flatMap { it.rhythmValues }.takeIf { it.isNotEmpty() }
        return RhythmModel(
            allowedRhythms = values ?: when (profile.task) {
                MusicTask.SLEEP -> listOf(4f, 6f, 8f)
                MusicTask.READING -> listOf(4f, 6f, 8f)
                MusicTask.STUDY -> listOf(2f, 4f, 6f)
                MusicTask.CODING -> listOf(1f, 2f, 4f)
                MusicTask.RELAX -> listOf(2f, 3f, 4f, 6f)
                MusicTask.WORKOUT -> listOf(0.5f, 1f, 2f)
            },
            syncopation = when (profile.task) {
                MusicTask.SLEEP, MusicTask.READING -> 0f
                MusicTask.STUDY -> 0.08f
                MusicTask.CODING -> 0.22f
                MusicTask.RELAX -> 0.12f
                MusicTask.WORKOUT -> 0.55f
            }
        )
    }
}
