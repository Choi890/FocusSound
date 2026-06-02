package com.focussound.data

import com.focussound.recommendation.FatigueEstimator

enum class FocusMode(val label: String, val description: String) {
    STUDY("공부", "차분하게 유지되는 학습용 질감"),
    CODING("코딩", "흐름을 방해하지 않는 낮은 움직임"),
    READING("독서", "부드럽고 따뜻한 배경음"),
    SLEEP("수면", "밝기를 낮춘 안정적인 소리")
}

enum class SoundType(val label: String, val description: String) {
    NONE("선택하지 않음", "악기 샘플만 재생"),
    BROWN("브라운 노이즈", "낮고 둥근 저역 중심"),
    PINK("핑크 노이즈", "균형 잡힌 자연스러운 감쇠"),
    WHITE("화이트 노이즈", "밝고 균일한 고역 포함"),
    RAIN_TEXTURE("비 오는 질감", "부드러운 랜덤 텍스처"),
    TAPE_TEXTURE("테이프 질감", "아주 약한 흔들림과 따뜻한 배경")
}

data class SoundProfile(
    val mode: FocusMode = FocusMode.STUDY,
    val soundType: SoundType = SoundType.NONE,
    val brightness: Float = 0.35f,
    val warmth: Float = 0.6f,
    val movement: Float = 0.2f
) {
    val displayName: String
        get() = "${mode.label} · ${soundType.label}"
}

fun estimateFatigueScore(profile: SoundProfile, durationMinutes: Int): Int {
    return FatigueEstimator().estimate(profile, durationMinutes)
}
