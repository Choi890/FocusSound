package com.focussound.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONArray

private val Context.focusSoundDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "focus_sound_state"
)

class FocusSoundRepository(context: Context) {
    private val appContext = context.applicationContext

    val preferenceFlow: Flow<UserPreference> = appContext.focusSoundDataStore.data
        .safePreferences()
        .map { preferences ->
            UserPreference(
                mode = preferences[Keys.MODE].toEnum(FocusMode.STUDY),
                soundType = preferences[Keys.SOUND_TYPE].toEnum(SoundType.PINK),
                brightness = preferences[Keys.BRIGHTNESS] ?: 0.35f,
                warmth = preferences[Keys.WARMTH] ?: 0.6f,
                movement = preferences[Keys.MOVEMENT] ?: 0.2f,
                timerMinutes = preferences[Keys.TIMER_MINUTES] ?: 25,
                customTimerMinutes = preferences[Keys.CUSTOM_TIMER_MINUTES] ?: 35
            )
        }

    val sessionsFlow: Flow<List<FocusSession>> = appContext.focusSoundDataStore.data
        .safePreferences()
        .map { preferences -> parseSessions(preferences[Keys.SESSIONS_JSON].orEmpty()) }

    suspend fun savePreference(preference: UserPreference) {
        appContext.focusSoundDataStore.edit { preferences ->
            preferences[Keys.MODE] = preference.mode.name
            preferences[Keys.SOUND_TYPE] = preference.soundType.name
            preferences[Keys.BRIGHTNESS] = preference.brightness.coerceIn(0f, 1f)
            preferences[Keys.WARMTH] = preference.warmth.coerceIn(0f, 1f)
            preferences[Keys.MOVEMENT] = preference.movement.coerceIn(0f, 1f)
            preferences[Keys.TIMER_MINUTES] = preference.timerMinutes.coerceIn(1, 240)
            preferences[Keys.CUSTOM_TIMER_MINUTES] = preference.customTimerMinutes.coerceIn(1, 240)
        }
    }

    suspend fun addSession(session: FocusSession) {
        appContext.focusSoundDataStore.edit { preferences ->
            val current = parseSessions(preferences[Keys.SESSIONS_JSON].orEmpty())
            val capped = (current + session).takeLast(MAX_SESSIONS)
            preferences[Keys.SESSIONS_JSON] = sessionsToJson(capped)
        }
    }

    private object Keys {
        val MODE = stringPreferencesKey("mode")
        val SOUND_TYPE = stringPreferencesKey("sound_type")
        val BRIGHTNESS = floatPreferencesKey("brightness")
        val WARMTH = floatPreferencesKey("warmth")
        val MOVEMENT = floatPreferencesKey("movement")
        val TIMER_MINUTES = intPreferencesKey("timer_minutes")
        val CUSTOM_TIMER_MINUTES = intPreferencesKey("custom_timer_minutes")
        val SESSIONS_JSON = stringPreferencesKey("sessions_json")
    }

    private companion object {
        const val MAX_SESSIONS = 200
    }
}

private fun Flow<Preferences>.safePreferences(): Flow<Preferences> = catch { throwable ->
    if (throwable is IOException) {
        emit(emptyPreferences())
    } else {
        throw throwable
    }
}

private inline fun <reified T : Enum<T>> String?.toEnum(fallback: T): T {
    return enumValues<T>().firstOrNull { it.name == this } ?: fallback
}

private fun parseSessions(raw: String): List<FocusSession> {
    if (raw.isBlank()) return emptyList()
    return runCatching {
        val array = JSONArray(raw)
        buildList {
            for (index in 0 until array.length()) {
                array.optJSONObject(index)?.let(FocusSession::fromJsonObject)?.let(::add)
            }
        }
    }.getOrDefault(emptyList())
}

private fun sessionsToJson(sessions: List<FocusSession>): String {
    val array = JSONArray()
    sessions.forEach { array.put(it.toJsonObject()) }
    return array.toString()
}
