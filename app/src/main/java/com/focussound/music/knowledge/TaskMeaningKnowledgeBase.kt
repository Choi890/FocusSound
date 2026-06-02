package com.focussound.music.knowledge

import com.focussound.music.model.MusicTask

object TaskMeaningKnowledgeBase {
    fun meaningFor(task: MusicTask): String = when (task) {
        MusicTask.STUDY -> "기억, 이해, 문제 해결을 돕는 낮은 방해감의 배경 음악"
        MusicTask.SLEEP -> "각성을 낮추고 수면 진입을 방해하지 않는 매우 부드러운 음악"
        MusicTask.CODING -> "문제 해결과 몰입을 돕는 약한 추진력의 반복 기반 음악"
        MusicTask.READING -> "언어 처리를 방해하지 않도록 멜로디와 리듬을 줄인 음악"
        MusicTask.RELAX -> "긴장을 낮추고 정서적으로 편안하게 흐르는 음악"
        MusicTask.WORKOUT -> "운동 동기와 에너지를 높이는 리듬 중심 음악"
    }
}
