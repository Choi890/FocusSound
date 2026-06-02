package com.focussound.music.learning

import com.focussound.music.knowledge.TaskMusicProfile
import com.focussound.music.model.MusicTask

class SectionTransitionStatisticsExtractor {
    fun extract(profile: TaskMusicProfile): SectionTransitionModel = when (profile.task) {
        MusicTask.SLEEP -> SectionTransitionModel(0.08f, 0.18f)
        MusicTask.READING -> SectionTransitionModel(0.1f, 0.2f)
        MusicTask.STUDY -> SectionTransitionModel(0.18f, 0.28f)
        MusicTask.CODING -> SectionTransitionModel(0.28f, 0.36f)
        MusicTask.RELAX -> SectionTransitionModel(0.2f, 0.3f)
        MusicTask.WORKOUT -> SectionTransitionModel(0.42f, 0.55f)
    }
}
