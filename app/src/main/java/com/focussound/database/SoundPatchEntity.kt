package com.focussound.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.focussound.data.FocusMode
import com.focussound.sounddesign.NoiseType
import com.focussound.sounddesign.SoundPatch

@Entity(tableName = "sound_patches")
data class SoundPatchEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val mode: String,
    val baseNoiseType: String,
    val brightness: Float,
    val warmth: Float,
    val movement: Float,
    val highCut: Float,
    val lowAmount: Float,
    val stereoWidth: Float,
    val noiseLayerAmount: Float,
    val rainLayerAmount: Float,
    val padLayerAmount: Float,
    val modulationDepth: Float,
    val modulationRateHz: Float,
    val targetFatigueScore: Int,
    val durationMinutes: Int,
    val createdAtMillis: Long,
    val lastUsedAtMillis: Long
) {
    fun toDomain(): SoundPatch = SoundPatch(
        id = id,
        name = name,
        mode = enumValueOrDefault(mode, FocusMode.STUDY),
        baseNoiseType = enumValueOrDefault(baseNoiseType, NoiseType.PINK),
        brightness = brightness,
        warmth = warmth,
        movement = movement,
        highCut = highCut,
        lowAmount = lowAmount,
        stereoWidth = stereoWidth,
        noiseLayerAmount = noiseLayerAmount,
        rainLayerAmount = rainLayerAmount,
        padLayerAmount = padLayerAmount,
        modulationDepth = modulationDepth,
        modulationRateHz = modulationRateHz,
        targetFatigueScore = targetFatigueScore,
        durationMinutes = durationMinutes
    )
}

fun SoundPatch.toEntity(nowMillis: Long = System.currentTimeMillis()): SoundPatchEntity = SoundPatchEntity(
    id = id,
    name = name,
    mode = mode.name,
    baseNoiseType = baseNoiseType.name,
    brightness = brightness,
    warmth = warmth,
    movement = movement,
    highCut = highCut,
    lowAmount = lowAmount,
    stereoWidth = stereoWidth,
    noiseLayerAmount = noiseLayerAmount,
    rainLayerAmount = rainLayerAmount,
    padLayerAmount = padLayerAmount,
    modulationDepth = modulationDepth,
    modulationRateHz = modulationRateHz,
    targetFatigueScore = targetFatigueScore,
    durationMinutes = durationMinutes,
    createdAtMillis = nowMillis,
    lastUsedAtMillis = nowMillis
)
