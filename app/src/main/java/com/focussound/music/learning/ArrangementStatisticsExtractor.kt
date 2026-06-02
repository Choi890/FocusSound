package com.focussound.music.learning

import com.focussound.music.knowledge.TaskMusicProfile
import com.focussound.music.model.MusicTask

class ArrangementStatisticsExtractor {
    fun extract(profile: TaskMusicProfile): ArrangementModel = when (profile.task) {
        MusicTask.SLEEP -> ArrangementModel(0.18f, 0.82f, 0.78f, 0.2f, 0f)
        MusicTask.READING -> ArrangementModel(0.12f, 0.86f, 0.82f, 0.24f, 0f)
        MusicTask.STUDY -> ArrangementModel(0.58f, 0.56f, 0.5f, 0.28f, 0.04f)
        MusicTask.CODING -> ArrangementModel(0.46f, 0.46f, 0.3f, 0.42f, 0.22f)
        MusicTask.RELAX -> ArrangementModel(0.5f, 0.68f, 0.62f, 0.28f, 0.06f)
        MusicTask.WORKOUT -> ArrangementModel(0.35f, 0.18f, 0.12f, 0.76f, 0.9f)
    }
}
