package com.focussound.audio

import com.focussound.data.FocusMode
import com.focussound.data.SoundProfile
import kotlin.math.PI
import kotlin.math.sin

class AudioModulator(
    private val sampleRate: Int
) {
    private var sampleIndex = 0L
    private var sleepFadeSamples = 0L
    private var movementDepth = 0f
    private var movementRateHz = 0.02f

    fun configure(profile: SoundProfile, sleepFadeOutMillis: Long) {
        configure(
            profile = profile,
            sleepFadeOutMillis = sleepFadeOutMillis,
            modulationDepth = profile.movement * 0.25f,
            modulationRateHz = 0.02f + profile.movement * 0.08f
        )
    }

    fun configure(
        profile: SoundProfile,
        sleepFadeOutMillis: Long,
        modulationDepth: Float,
        modulationRateHz: Float
    ) {
        sampleIndex = 0L
        sleepFadeSamples = if (profile.mode == FocusMode.SLEEP && sleepFadeOutMillis > 0L) {
            (sampleRate * sleepFadeOutMillis / 1000L).coerceAtLeast(1L)
        } else {
            0L
        }
        movementDepth = modulationDepth.coerceIn(0f, 1f)
        movementRateHz = modulationRateHz.coerceIn(0.005f, 0.25f)
    }

    fun nextGain(): Float {
        val movement = 1f + sin((sampleIndex.toDouble() / sampleRate) * 2.0 * PI * movementRateHz).toFloat() * movementDepth * 0.2f
        val sleepGain = if (sleepFadeSamples > 0L) {
            (1f - (sampleIndex.toFloat() / sleepFadeSamples.toFloat())).coerceIn(0f, 1f)
        } else {
            1f
        }
        sampleIndex += 1
        return (movement * sleepGain).coerceIn(0f, 1.05f)
    }
}
