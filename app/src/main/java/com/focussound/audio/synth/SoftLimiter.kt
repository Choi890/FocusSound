package com.focussound.audio.synth

import kotlin.math.tanh

class SoftLimiter(
    private val drive: Float = 1.4f
) {
    fun process(input: Float): Float {
        return tanh((input * drive).toDouble()).toFloat().coerceIn(-1f, 1f)
    }
}
