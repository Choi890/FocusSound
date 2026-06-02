package com.focussound.audio

import kotlin.math.PI
import kotlin.math.sin

class AmbientPadGenerator(
    private val sampleRate: Int
) : NoiseGenerator {
    private var index = 0L

    override fun nextSample(): Float {
        val t = index.toDouble() / sampleRate
        index += 1
        val a = sin(2.0 * PI * 55.0 * t) * 0.35
        val b = sin(2.0 * PI * 82.5 * t) * 0.22
        val c = sin(2.0 * PI * 110.0 * t) * 0.12
        return ((a + b + c) * 0.42).toFloat().coerceIn(-1f, 1f)
    }

    override fun reset() {
        index = 0L
    }
}
