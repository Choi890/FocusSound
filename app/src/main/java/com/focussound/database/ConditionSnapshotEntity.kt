package com.focussound.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.focussound.condition.FatigueLevel
import com.focussound.condition.MoodLevel
import com.focussound.condition.TimeOfDay
import com.focussound.condition.UserCondition

@Entity(tableName = "condition_snapshots")
data class ConditionSnapshotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val sleepMinutes: Int?,
    val sleepDebt: Boolean,
    val stepsToday: Int?,
    val restingHeartRate: Int?,
    val selfReportedFatigue: String,
    val selfReportedMood: String,
    val timeOfDay: String,
    val source: String,
    val createdAtMillis: Long
) {
    fun toDomain(): UserCondition = UserCondition(
        sleepMinutes = sleepMinutes,
        sleepDebt = sleepDebt,
        stepsToday = stepsToday,
        restingHeartRate = restingHeartRate,
        selfReportedFatigue = enumValueOrDefault(selfReportedFatigue, FatigueLevel.MEDIUM),
        selfReportedMood = enumValueOrDefault(selfReportedMood, MoodLevel.NORMAL),
        timeOfDay = enumValueOrDefault(timeOfDay, TimeOfDay.AFTERNOON)
    )

    companion object {
        fun fromDomain(condition: UserCondition, source: String): ConditionSnapshotEntity {
            return ConditionSnapshotEntity(
                sleepMinutes = condition.sleepMinutes,
                sleepDebt = condition.sleepDebt,
                stepsToday = condition.stepsToday,
                restingHeartRate = condition.restingHeartRate,
                selfReportedFatigue = condition.selfReportedFatigue.name,
                selfReportedMood = condition.selfReportedMood.name,
                timeOfDay = condition.timeOfDay.name,
                source = source,
                createdAtMillis = System.currentTimeMillis()
            )
        }
    }
}
