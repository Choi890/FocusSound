package com.focussound.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {
    @Query("SELECT * FROM sound_presets ORDER BY lastUsedAtMillis DESC LIMIT :limit")
    fun observeRecent(limit: Int = 6): Flow<List<PresetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preset: PresetEntity): Long

    @Update
    suspend fun update(preset: PresetEntity)

    @Query("UPDATE sound_presets SET lastUsedAtMillis = :usedAtMillis WHERE id = :id")
    suspend fun markUsed(id: Long, usedAtMillis: Long)

    @Query("DELETE FROM sound_presets WHERE id = :id")
    suspend fun deleteById(id: Long)
}
