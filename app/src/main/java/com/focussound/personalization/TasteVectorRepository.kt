package com.focussound.personalization

import com.focussound.data.FocusMode
import com.focussound.database.UserTasteVectorDao
import com.focussound.database.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TasteVectorRepository(
    private val userTasteVectorDao: UserTasteVectorDao
) {
    val tasteVectors: Flow<List<UserSoundTasteVector>> = userTasteVectorDao
        .observeAll()
        .map { vectors -> vectors.map { it.toDomain() } }

    suspend fun getByMode(mode: FocusMode): UserSoundTasteVector {
        return userTasteVectorDao.getByMode(mode.name)?.toDomain()
            ?: UserSoundTasteVector(mode = mode)
    }

    suspend fun save(vector: UserSoundTasteVector) {
        userTasteVectorDao.upsert(vector.toEntity())
    }
}
