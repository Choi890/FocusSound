package com.focussound.audio

import kotlin.math.max

class AudioEnvelope(
    private val sampleRate: Int
) {
    private var currentGain = 0f
    private var targetGain = 0f
    private var step = 0f
    private var remainingSamples = 0

    fun setImmediate(gain: Float) {
        val clipped = gain.coerceIn(0f, 1f)
        currentGain = clipped
        targetGain = clipped
        step = 0f
        remainingSamples = 0
    }

    fun fadeTo(gain: Float, durationMillis: Long) {
        targetGain = gain.coerceIn(0f, 1f)
        val samples = ((sampleRate * max(durationMillis, 0L)) / 1000L)
            .toInt()
            .coerceAtLeast(1)
        remainingSamples = samples
        step = (targetGain - currentGain) / samples
    }

    fun nextGain(): Float {
        if (remainingSamples > 0) {
            currentGain += step
            remainingSamples -= 1
            if (remainingSamples == 0) {
                currentGain = targetGain
            }
        }
        return currentGain.coerceIn(0f, 1f)
    }

    fun isSilentAtTarget(): Boolean {
        return remainingSamples == 0 && targetGain == 0f && currentGain <= 0.0001f
    }
}
