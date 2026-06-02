package com.focussound.music.model

import com.focussound.data.FocusMode
import com.focussound.data.SoundType
import com.focussound.instrument.InstrumentPreset

enum class MusicTask(val label: String, val description: String) {
    STUDY("공부", "방해가 적은 완성형 피아노/현악 음악"),
    SLEEP("숙면", "타악기 없이 천천히 흐르는 수면 음악"),
    CODING("코딩", "약한 반복감과 작은 추진력이 있는 음악"),
    READING("독서", "언어 처리를 방해하지 않는 낮은 멜로디 음악"),
    RELAX("휴식", "따뜻하고 부드러운 릴렉싱 음악"),
    WORKOUT("운동", "빠른 템포와 리듬 중심의 에너지 음악")
}

enum class MusicStyle(val label: String) {
    RELAXING_PIANO("릴렉싱 피아노"),
    AMBIENT("앰비언트"),
    LOFI("로파이"),
    CLASSICAL_MINIMAL("클래식 미니멀"),
    ORCHESTRAL_PAD("오케스트라 패드"),
    SLEEP_DRONE("수면 드론"),
    MINIMAL_ELECTRONIC("미니멀 일렉트로닉")
}

enum class Density(val label: String, val value: Float) {
    NONE("없음", 0f),
    VERY_LOW("매우 낮음", 0.08f),
    LOW("낮음", 0.18f),
    MEDIUM("보통", 0.36f),
    HIGH("높음", 0.62f)
}

data class LiveCompositionRequest(
    val task: MusicTask,
    val selectedInstruments: List<InstrumentPreset>,
    val style: MusicStyle,
    val soundType: SoundType = SoundType.NONE,
    val diversity: Float = 0.46f,
    val melodyAmount: Float = 0.28f,
    val rhythmAmount: Float = 0.08f,
    val targetDurationMinutes: Int? = null
) {
    val focusMode: FocusMode
        get() = when (task) {
            MusicTask.STUDY -> FocusMode.STUDY
            MusicTask.SLEEP -> FocusMode.SLEEP
            MusicTask.CODING -> FocusMode.CODING
            MusicTask.READING -> FocusMode.READING
            MusicTask.RELAX -> FocusMode.READING
            MusicTask.WORKOUT -> FocusMode.CODING
        }
}
