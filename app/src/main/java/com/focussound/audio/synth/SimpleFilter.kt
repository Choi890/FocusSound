package com.focussound.audio.synth

class SimpleFilter {
    private var state = 0f

    fun lowPass(input: Float, cutoff: Float): Float {
        val alpha = cutoff.coerceIn(0.015f, 0.95f)
        state += (input - state) * alpha
        return state
    }
}
