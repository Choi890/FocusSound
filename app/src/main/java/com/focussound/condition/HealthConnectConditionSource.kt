package com.focussound.condition

import android.content.Context

class HealthConnectConditionSource(
    private val context: Context
) {
    fun isAvailable(): Boolean = false

    suspend fun readConditionOrNull(): UserCondition? {
        return null
    }
}
