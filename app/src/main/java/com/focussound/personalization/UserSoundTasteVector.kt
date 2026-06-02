package com.focussound.personalization

import com.focussound.data.FocusMode
import com.focussound.composition.CompositionGenre
import com.focussound.sounddesign.NoiseType

data class UserSoundTasteVector(
    val mode: FocusMode,
    val preferredBrightness: Float = 0.35f,
    val preferredWarmth: Float = 0.6f,
    val preferredMovement: Float = 0.2f,
    val preferredHighCut: Float = 0.55f,
    val preferredStereoWidth: Float = 0.35f,
    val preferredNoiseTypes: Map<NoiseType, Float> = mapOf(
        NoiseType.WHITE to 0.2f,
        NoiseType.PINK to 0.45f,
        NoiseType.BROWN to 0.35f
    ),
    val preferredSessionMinutes: Int = 25,
    val confidence: Float = 0f,
    val preferredGenre: CompositionGenre = CompositionGenre.AMBIENT_CODING,
    val preferredTempoRange: IntRange = 60..82,
    val preferredMelodyDensity: Float = 0.22f,
    val preferredRhythmDensity: Float = 0.12f,
    val preferredHarmonicComplexity: Float = 0.28f
)
