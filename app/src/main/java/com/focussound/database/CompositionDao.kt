package com.focussound.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CompositionDao {
    @Query("SELECT * FROM composition_patches ORDER BY lastUsedAtMillis DESC LIMIT :limit")
    fun observeRecentPatches(limit: Int = 8): Flow<List<CompositionPatchEntity>>

    @Query("SELECT * FROM composition_patches WHERE id = :id LIMIT 1")
    suspend fun getPatch(id: String): CompositionPatchEntity?

    @Query("SELECT * FROM composition_notes WHERE patchId = :patchId ORDER BY startBeat ASC, id ASC")
    suspend fun getNotes(patchId: String): List<CompositionNoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPatch(patch: CompositionPatchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<CompositionNoteEntity>)

    @Query("DELETE FROM composition_notes WHERE patchId = :patchId")
    suspend fun deleteNotes(patchId: String)

    @Query("UPDATE composition_patches SET lastUsedAtMillis = :usedAtMillis WHERE id = :id")
    suspend fun markUsed(id: String, usedAtMillis: Long)

    @Transaction
    suspend fun upsertComposition(
        patch: CompositionPatchEntity,
        notes: List<CompositionNoteEntity>
    ) {
        upsertPatch(patch)
        deleteNotes(patch.id)
        insertNotes(notes)
    }
}
