package com.focussound.ai

import com.focussound.data.FocusMode

data class SoundIntent(
    val mode: FocusMode,
    val moodKeywords: List<String>,
    val fatigueTarget: FatigueTarget,
    val brightnessHint: Float?,
    val warmthHint: Float?,
    val movementHint: Float?,
    val textureHint: TextureHint?,
    val durationMinutes: Int?
)

enum class FatigueTarget {
    VERY_LOW,
    LOW,
    NORMAL
}

enum class TextureHint {
    CLEAN,
    RAINY,
    NIGHT,
    WARM_PAD,
    DEEP_ROOM,
    LIBRARY,
    CYBER,
    SLEEP_DARK
}
