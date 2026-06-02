package com.focussound.audio.synth

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

enum class Waveform {
    SINE,
    TRIANGLE,
    SOFT_SQUARE,
    NOISE
}

class Oscillator(
    private val waveform: Waveform,
    seed: Int = 0
) {
    private var phase = 0.0
    private val random = Random(seed)

    fun next(frequencyHz: Float, sampleRate: Int): Float {
        val value = when (waveform) {
            Waveform.SINE -> sin(phase * TWO_PI).toFloat()
            Waveform.TRIANGLE -> (4f * abs(phase.toFloat() - 0.5f) - 1f)
            Waveform.SOFT_SQUARE -> sin(phase * TWO_PI).toFloat().coerceIn(-0.72f, 0.72f)
            Waveform.NOISE -> random.nextFloat() * 2f - 1f
        }
        phase += frequencyHz / sampleRate
        if (phase >= 1.0) phase -= phase.toInt()
        return value
    }

    private companion object {
        const val TWO_PI = PI * 2.0
    }
}
