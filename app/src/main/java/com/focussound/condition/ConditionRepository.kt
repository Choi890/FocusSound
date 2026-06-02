package com.focussound.condition

import android.content.Context
import com.focussound.database.ConditionSnapshotDao
import com.focussound.database.ConditionSnapshotEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConditionRepository(
    private val context: Context,
    private val conditionSnapshotDao: ConditionSnapshotDao
) {
    private val manualSource = ManualConditionSource()
    private val healthConnectSource = HealthConnectConditionSource(context)
    private val _condition = MutableStateFlow(UserCondition())
    val condition: Flow<UserCondition> = _condition.asStateFlow()

    suspend fun updateManualCondition(
        sleepMinutes: Int?,
        fatigue: FatigueLevel,
        mood: MoodLevel,
        timeOfDay: TimeOfDay
    ): Long {
        val condition = manualSource.buildCondition(sleepMinutes, fatigue, mood, timeOfDay)
        _condition.value = condition
        return saveSnapshot(condition, source = "manual")
    }

    suspend fun refreshFromHealthConnectIfAvailable(): UserCondition? {
        if (!healthConnectSource.isAvailable()) return null
        val condition = healthConnectSource.readConditionOrNull() ?: return null
        _condition.value = condition
        saveSnapshot(condition, source = "health_connect")
        return condition
    }

    suspend fun saveSnapshot(condition: UserCondition, source: String = "manual"): Long {
        return conditionSnapshotDao.insert(ConditionSnapshotEntity.fromDomain(condition, source))
    }
}
