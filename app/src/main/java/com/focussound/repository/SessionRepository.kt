package com.focussound.repository

import com.focussound.data.FocusSession
import com.focussound.data.WeeklyReport
import com.focussound.database.FocusSessionDao
import com.focussound.database.toEntity
import java.util.Calendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionRepository(
    private val focusSessionDao: FocusSessionDao
) {
    val sessions: Flow<List<FocusSession>> = focusSessionDao
        .observeAll()
        .map { entities -> entities.map { it.toDomain() } }

    val weeklyReport: Flow<WeeklyReport> = focusSessionDao
        .observeSince(currentWeekStartMillis())
        .map { entities -> entities.map { it.toDomain() }.toWeeklyReport() }

    suspend fun saveSession(session: FocusSession) {
        focusSessionDao.insert(session.toEntity())
    }

    private fun List<FocusSession>.toWeeklyReport(): WeeklyReport {
        if (isEmpty()) return WeeklyReport()

        val totalMinutes = sumOf { it.elapsedSeconds } / 60
        val averageFatigue = map { it.fatigueRating }.average().toFloat()
        val averageFocus = map { it.focusRating }.average().toFloat()
        val bestSound = groupBy { it.soundType }
            .maxByOrNull { (_, grouped) ->
                grouped.map { session ->
                    session.focusRating * 1.4f - session.fatigueRating * 0.9f - session.fatigueScore * 0.02f
                }.average()
            }
            ?.key
            ?.label ?: "기록 없음"

        return WeeklyReport(
            totalFocusMinutes = totalMinutes,
            bestSoundLabel = bestSound,
            averageFatigue = averageFatigue,
            averageFocus = averageFocus,
            sessionCount = size
        )
    }

    private fun currentWeekStartMillis(): Long {
        return Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
