package com.focussound.audio

import com.focussound.data.SoundProfile
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.tanh

class AudioFilter(
    private val sampleRate: Int
) {
    private var lowPass = 0f
    private var sampleIndex = 0L

    fun process(input: Float, profile: SoundProfile): Float {
        val brightness = profile.brightness.coerceIn(0f, 1f)
        val warmth = profile.warmth.coerceIn(0f, 1f)
        val movement = profile.movement.coerceIn(0f, 1f)

        val smoothing = 0.035f + (1f - warmth) * 0.18f
        lowPass += smoothing * (input - lowPass)
        val highPass = input - lowPass

        val warmLayer = lowPass * (0.85f + warmth * 0.35f)
        val brightLayer = highPass * (0.2f + brightness * 0.85f)
        val lfoHz = 0.018f + movement * 0.09f
        val lfo = sin((sampleIndex.toDouble() / sampleRate) * 2.0 * PI * lfoHz).toFloat()
        val movementGain = 1f + lfo * movement * 0.08f

        sampleIndex += 1

        val shaped = (warmLayer + brightLayer) * movementGain * 0.72f
        return tanh(shaped.toDouble()).toFloat().coerceIn(-1f, 1f)
    }

    fun reset() {
        lowPass = 0f
        sampleIndex = 0L
    }
}
