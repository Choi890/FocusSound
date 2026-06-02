package com.focussound.recommendation

import com.focussound.data.FocusMode
import com.focussound.data.SoundProfile
import com.focussound.data.SoundType
import kotlin.math.roundToInt

class FatigueEstimator {
    fun estimate(profile: SoundProfile, durationMinutes: Int): Int {
        val typeBase = when (profile.soundType) {
            SoundType.NONE -> 18f
            SoundType.WHITE -> 48f
            SoundType.PINK -> 31f
            SoundType.BROWN -> 25f
            SoundType.RAIN_TEXTURE -> 28f
            SoundType.TAPE_TEXTURE -> 24f
        }
        val modeAdjustment = when (profile.mode) {
            FocusMode.STUDY -> 1f
            FocusMode.CODING -> 3f
            FocusMode.READING -> -2f
            FocusMode.SLEEP -> -8f
        }
        val durationImpact = when {
            durationMinutes <= 25 -> 0f
            durationMinutes <= 50 -> 7f
            else -> 7f + ((durationMinutes - 50) * 0.28f).coerceAtMost(20f)
        }

        val brightnessImpact = profile.brightness.coerceIn(0f, 1f) * 30f
        val warmthRelief = profile.warmth.coerceIn(0f, 1f) * 13f
        val movementImpact = profile.movement.coerceIn(0f, 1f) * 17f
        val brownBassImpact = if (profile.soundType == SoundType.BROWN && profile.warmth > 0.78f) 4f else 0f

        return (typeBase + modeAdjustment + durationImpact + brightnessImpact - warmthRelief + movementImpact + brownBassImpact)
            .roundToInt()
            .coerceIn(0, 100)
    }
}
