package com.focussound.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.focussound.instrument.SampleZone

@Entity(
    tableName = "sample_zones",
    indices = [Index(value = ["presetId"])]
)
data class SampleZoneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val presetId: String,
    val samplePath: String,
    val rootMidiNote: Int,
    val minMidiNote: Int,
    val maxMidiNote: Int,
    val minVelocity: Int,
    val maxVelocity: Int,
    val loopStartFrame: Int?,
    val loopEndFrame: Int?
) {
    fun toDomain(): SampleZone = SampleZone(
        samplePath = samplePath,
        rootMidiNote = rootMidiNote,
        minMidiNote = minMidiNote,
        maxMidiNote = maxMidiNote,
        minVelocity = minVelocity,
        maxVelocity = maxVelocity,
        loopStartFrame = loopStartFrame,
        loopEndFrame = loopEndFrame
    )
}

fun SampleZone.toEntity(presetId: String): SampleZoneEntity = SampleZoneEntity(
    presetId = presetId,
    samplePath = samplePath,
    rootMidiNote = rootMidiNote,
    minMidiNote = minMidiNote,
    maxMidiNote = maxMidiNote,
    minVelocity = minVelocity,
    maxVelocity = maxVelocity,
    loopStartFrame = loopStartFrame,
    loopEndFrame = loopEndFrame
)
