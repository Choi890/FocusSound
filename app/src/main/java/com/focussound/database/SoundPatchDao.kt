package com.focussound.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundPatchDao {
    @Query("SELECT * FROM sound_patches ORDER BY lastUsedAtMillis DESC LIMIT :limit")
    fun observeRecent(limit: Int = 12): Flow<List<SoundPatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(patch: SoundPatchEntity)

    @Query("UPDATE sound_patches SET lastUsedAtMillis = :usedAtMillis WHERE id = :id")
    suspend fun markUsed(id: String, usedAtMillis: Long)
}
