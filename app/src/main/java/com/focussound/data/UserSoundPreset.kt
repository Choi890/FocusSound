package com.focussound.data

data class UserSoundPreset(
    val id: Long = 0L,
    val name: String,
    val profile: SoundProfile,
    val createdAtMillis: Long,
    val lastUsedAtMillis: Long
)

data class RecommendedSound(
    val profile: SoundProfile,
    val title: String,
    val reason: String,
    val fatigueScore: Int
)

data class WeeklyReport(
    val totalFocusMinutes: Int = 0,
    val bestSoundLabel: String = "기록 없음",
    val averageFatigue: Float = 0f,
    val averageFocus: Float = 0f,
    val sessionCount: Int = 0
)
