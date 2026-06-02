package com.focussound.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserTasteVectorDao {
    @Query("SELECT * FROM user_taste_vectors")
    fun observeAll(): Flow<List<UserTasteVectorEntity>>

    @Query("SELECT * FROM user_taste_vectors WHERE mode = :mode LIMIT 1")
    suspend fun getByMode(mode: String): UserTasteVectorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vector: UserTasteVectorEntity)
}
