package com.focussound.repository

import com.focussound.data.SoundProfile
import com.focussound.data.UserSoundPreset
import com.focussound.database.PresetDao
import com.focussound.database.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PresetRepository(
    private val presetDao: PresetDao
) {
    val recentPresets: Flow<List<UserSoundPreset>> = presetDao
        .observeRecent()
        .map { presets -> presets.map { it.toDomain() } }

    suspend fun savePreset(name: String, profile: SoundProfile): Long {
        val now = System.currentTimeMillis()
        return presetDao.insert(
            UserSoundPreset(
                name = name.ifBlank { defaultPresetName(profile) },
                profile = profile,
                createdAtMillis = now,
                lastUsedAtMillis = now
            ).toEntity()
        )
    }

    suspend fun markUsed(id: Long) {
        presetDao.markUsed(id, System.currentTimeMillis())
    }

    suspend fun delete(id: Long) {
        presetDao.deleteById(id)
    }

    private fun defaultPresetName(profile: SoundProfile): String {
        return "${profile.mode.label} ${profile.soundType.label}"
    }
}
