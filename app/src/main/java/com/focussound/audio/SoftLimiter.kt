package com.focussound.audio

import kotlin.math.tanh

class SoftLimiter {
    fun process(sample: Float): Float {
        return tanh((sample * DRIVE).toDouble()).toFloat().coerceIn(-1f, 1f)
    }

    private companion object {
        const val DRIVE = 1.12f
    }
}
