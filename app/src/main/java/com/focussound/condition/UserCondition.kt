package com.focussound.condition

enum class FatigueLevel {
    LOW,
    MEDIUM,
    HIGH
}

enum class MoodLevel {
    CALM,
    NORMAL,
    STRESSED
}

enum class TimeOfDay {
    MORNING,
    AFTERNOON,
    EVENING,
    NIGHT
}

data class UserCondition(
    val sleepMinutes: Int? = null,
    val sleepDebt: Boolean = false,
    val stepsToday: Int? = null,
    val restingHeartRate: Int? = null,
    val selfReportedFatigue: FatigueLevel = FatigueLevel.MEDIUM,
    val selfReportedMood: MoodLevel = MoodLevel.NORMAL,
    val timeOfDay: TimeOfDay = TimeOfDay.AFTERNOON
)
