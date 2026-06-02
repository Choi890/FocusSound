package com.focussound.sounddesign

import com.focussound.data.FocusMode
import com.focussound.data.SoundProfile
import com.focussound.data.SoundType
import java.util.UUID

enum class NoiseType {
    WHITE,
    PINK,
    BROWN
}

data class SoundPatch(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val mode: FocusMode,
    val baseNoiseType: NoiseType,
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
    val durationMinutes: Int
) {
    val displayName: String
        get() = "$name · ${mode.label}"

    fun toSoundProfile(): SoundProfile = SoundProfile(
        mode = mode,
        soundType = when {
            noiseLayerAmount <= 0.001f && rainLayerAmount <= 0.001f -> SoundType.NONE
            rainLayerAmount > 0.001f && noiseLayerAmount <= 0.08f -> SoundType.RAIN_TEXTURE
            noiseLayerAmount in 0.001f..0.12f -> SoundType.TAPE_TEXTURE
            else -> baseNoiseType.toSoundType()
        },
        brightness = brightness,
        warmth = warmth,
        movement = movement
    )
}

fun NoiseType.toSoundType(): SoundType = when (this) {
    NoiseType.WHITE -> SoundType.WHITE
    NoiseType.PINK -> SoundType.PINK
    NoiseType.BROWN -> SoundType.BROWN
}

fun SoundType.toNoiseType(): NoiseType = when (this) {
    SoundType.NONE -> NoiseType.PINK
    SoundType.WHITE -> NoiseType.WHITE
    SoundType.PINK -> NoiseType.PINK
    SoundType.BROWN -> NoiseType.BROWN
    SoundType.RAIN_TEXTURE -> NoiseType.PINK
    SoundType.TAPE_TEXTURE -> NoiseType.PINK
}

fun SoundProfile.toSoundPatch(durationMinutes: Int): SoundPatch = SoundPatch(
    name = "${mode.label} ${soundType.label}",
    mode = mode,
    baseNoiseType = soundType.toNoiseType(),
    brightness = brightness,
    warmth = warmth,
    movement = movement,
    highCut = (1f - brightness + 0.12f).coerceIn(0f, 1f),
    lowAmount = (0.35f + warmth * 0.45f).coerceIn(0f, 1f),
    stereoWidth = when (mode) {
        FocusMode.SLEEP -> 0.24f
        FocusMode.READING -> 0.32f
        FocusMode.STUDY -> 0.38f
        FocusMode.CODING -> 0.46f
    },
    noiseLayerAmount = when (soundType) {
        SoundType.NONE -> 0f
        SoundType.RAIN_TEXTURE -> 0.04f
        SoundType.TAPE_TEXTURE -> 0.07f
        else -> 1f
    },
    rainLayerAmount = if (soundType == SoundType.RAIN_TEXTURE) 0.42f else 0f,
    padLayerAmount = if (mode == FocusMode.SLEEP) 0.18f else 0f,
    modulationDepth = movement * 0.25f,
    modulationRateHz = 0.02f + movement * 0.08f,
    targetFatigueScore = 30,
    durationMinutes = durationMinutes
)
