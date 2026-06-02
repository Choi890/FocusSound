package com.focussound.condition

class ManualConditionSource {
    fun buildCondition(
        sleepMinutes: Int?,
        fatigue: FatigueLevel,
        mood: MoodLevel,
        timeOfDay: TimeOfDay
    ): UserCondition {
        return UserCondition(
            sleepMinutes = sleepMinutes,
            sleepDebt = sleepMinutes != null && sleepMinutes < 360,
            selfReportedFatigue = fatigue,
            selfReportedMood = mood,
            timeOfDay = timeOfDay
        )
    }
}
