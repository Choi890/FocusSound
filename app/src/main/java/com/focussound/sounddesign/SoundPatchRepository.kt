package com.focussound.sounddesign

import com.focussound.database.SoundPatchDao
import com.focussound.database.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SoundPatchRepository(
    private val soundPatchDao: SoundPatchDao
) {
    val recentPatches: Flow<List<SoundPatch>> = soundPatchDao
        .observeRecent()
        .map { patches -> patches.map { it.toDomain() } }

    suspend fun save(patch: SoundPatch) {
        soundPatchDao.upsert(patch.toEntity())
    }

    suspend fun markUsed(id: String) {
        soundPatchDao.markUsed(id, System.currentTimeMillis())
    }
}
