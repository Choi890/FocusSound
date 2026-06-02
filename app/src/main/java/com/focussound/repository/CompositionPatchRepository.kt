package com.focussound.repository

import com.focussound.composition.CompositionPatch
import com.focussound.database.CompositionDao
import com.focussound.database.toEntity

class CompositionPatchRepository(
    private val compositionDao: CompositionDao
) {
    suspend fun save(patch: CompositionPatch) {
        compositionDao.upsertComposition(
            patch = patch.toEntity(),
            notes = patch.notes.map { it.toEntity(patch.id) }
        )
    }

    suspend fun markUsed(id: String) {
        compositionDao.markUsed(id, System.currentTimeMillis())
    }
}
