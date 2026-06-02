package com.focussound.sounddesign

import com.focussound.ai.FatigueTarget
import com.focussound.ai.SoundIntent
import com.focussound.ai.TextureHint
import com.focussound.condition.FatigueLevel
import com.focussound.condition.UserCondition
import com.focussound.data.FocusMode
import com.focussound.personalization.UserSoundTasteVector

class SoundPatchGenerator(
    private val nameGenerator: SoundPatchNameGenerator = SoundPatchNameGenerator()
) {
    fun generate(
        intent: SoundIntent,
        taste: UserSoundTasteVector?,
        condition: UserCondition?
    ): SoundPatch {
        val baseBrightness = intent.brightnessHint
            ?: taste?.preferredBrightness
            ?: defaultBrightness(intent.mode)
        val baseWarmth = intent.warmthHint
            ?: taste?.preferredWarmth
            ?: defaultWarmth(intent.mode)
        val baseMovement = intent.movementHint
            ?: taste?.preferredMovement
            ?: defaultMovement(intent.mode)

        val fatigueAdjustment = when {
            condition?.sleepDebt == true -> -0.12f
            condition?.selfReportedFatigue == FatigueLevel.HIGH -> -0.15f
            else -> 0f
        }
        val texture = intent.textureHint ?: TextureHint.CLEAN
        val brightness = (baseBrightness + fatigueAdjustment).coerceIn(0.05f, 0.9f)
        val warmth = baseWarmth.coerceIn(0f, 1f)
        val movement = baseMovement.coerceIn(0f, 1f)

        return SoundPatch(
            name = nameGenerator.buildName(intent.mode, texture),
            mode = intent.mode,
            baseNoiseType = chooseNoiseType(intent, taste),
            brightness = brightness,
            warmth = warmth,
            movement = movement,
            highCut = calculateHighCut(brightness, intent.fatigueTarget),
            lowAmount = calculateLowAmount(warmth),
            stereoWidth = calculateStereoWidth(intent.mode, texture, taste),
            noiseLayerAmount = 1f,
            rainLayerAmount = if (texture == TextureHint.RAINY) 0.45f else 0f,
            padLayerAmount = if (texture == TextureHint.WARM_PAD || texture == TextureHint.NIGHT || texture == TextureHint.SLEEP_DARK) 0.25f else 0f,
            modulationDepth = movement * 0.25f,
            modulationRateHz = 0.02f + movement * 0.08f,
            targetFatigueScore = estimateTargetFatigue(intent.fatigueTarget),
            durationMinutes = intent.durationMinutes ?: defaultDuration(intent.mode, condition)
        )
    }

    private fun defaultBrightness(mode: FocusMode): Float = when (mode) {
        FocusMode.STUDY -> 0.42f
        FocusMode.CODING -> 0.36f
        FocusMode.READING -> 0.30f
        FocusMode.SLEEP -> 0.18f
    }

    private fun defaultWarmth(mode: FocusMode): Float = when (mode) {
        FocusMode.STUDY -> 0.50f
        FocusMode.CODING -> 0.64f
        FocusMode.READING -> 0.70f
        FocusMode.SLEEP -> 0.82f
    }

    private fun defaultMovement(mode: FocusMode): Float = when (mode) {
        FocusMode.STUDY -> 0.18f
        FocusMode.CODING -> 0.28f
        FocusMode.READING -> 0.12f
        FocusMode.SLEEP -> 0.08f
    }

    private fun chooseNoiseType(intent: SoundIntent, taste: UserSoundTasteVector?): NoiseType {
        val preferred = taste?.preferredNoiseTypes?.maxByOrNull { it.value }?.key
        if (preferred != null && taste.confidence > 0.45f) return preferred
        return when (intent.mode) {
            FocusMode.CODING -> NoiseType.BROWN
            FocusMode.READING -> NoiseType.BROWN
            FocusMode.SLEEP -> NoiseType.BROWN
            FocusMode.STUDY -> NoiseType.PINK
        }
    }

    private fun calculateHighCut(brightness: Float, target: FatigueTarget): Float {
        val fatigueBoost = when (target) {
            FatigueTarget.VERY_LOW -> 0.25f
            FatigueTarget.LOW -> 0.12f
            FatigueTarget.NORMAL -> 0f
        }
        return (1f - brightness + fatigueBoost).coerceIn(0f, 1f)
    }

    private fun calculateLowAmount(warmth: Float): Float {
        return (0.35f + warmth * 0.45f).coerceIn(0f, 1f)
    }

    private fun calculateStereoWidth(
        mode: FocusMode,
        texture: TextureHint,
        taste: UserSoundTasteVector?
    ): Float {
        return taste?.preferredStereoWidth ?: when {
            mode == FocusMode.SLEEP -> 0.25f
            texture == TextureHint.RAINY -> 0.55f
            mode == FocusMode.CODING -> 0.45f
            else -> 0.35f
        }
    }

    private fun estimateTargetFatigue(target: FatigueTarget): Int = when (target) {
        FatigueTarget.VERY_LOW -> 15
        FatigueTarget.LOW -> 25
        FatigueTarget.NORMAL -> 40
    }

    private fun defaultDuration(mode: FocusMode, condition: UserCondition?): Int {
        if (condition?.sleepDebt == true) return 25
        return when (mode) {
            FocusMode.STUDY -> 50
            FocusMode.CODING -> 50
            FocusMode.READING -> 30
            FocusMode.SLEEP -> 45
        }
    }
}
