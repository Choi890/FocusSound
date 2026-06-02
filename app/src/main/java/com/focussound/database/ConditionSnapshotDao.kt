package com.focussound.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConditionSnapshotDao {
    @Query("SELECT * FROM condition_snapshots ORDER BY createdAtMillis DESC LIMIT 1")
    fun observeLatest(): Flow<ConditionSnapshotEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(snapshot: ConditionSnapshotEntity): Long
}
