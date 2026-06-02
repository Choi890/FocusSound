package com.focussound.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface InstrumentDao {
    @Query("SELECT * FROM instrument_presets ORDER BY importedAtMillis DESC")
    fun observePresets(): Flow<List<InstrumentPresetEntity>>

    @Query("SELECT * FROM sample_zones WHERE presetId = :presetId ORDER BY minMidiNote ASC")
    suspend fun getZones(presetId: String): List<SampleZoneEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPreset(preset: InstrumentPresetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZones(zones: List<SampleZoneEntity>)

    @Query("DELETE FROM sample_zones WHERE presetId = :presetId")
    suspend fun deleteZones(presetId: String)

    @Transaction
    suspend fun upsertInstrument(
        preset: InstrumentPresetEntity,
        zones: List<SampleZoneEntity>
    ) {
        upsertPreset(preset)
        deleteZones(preset.id)
        insertZones(zones)
    }
}
