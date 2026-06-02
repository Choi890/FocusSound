package com.focussound.audio.tone

class OnePoleLowPass(
    private var alpha: Float = 0.2f
) {
    private var previous = 0f

    fun setAlpha(value: Float) {
        alpha = value.coerceIn(0.01f, 0.98f)
    }

    fun process(sample: Float): Float {
        previous += alpha * (sample - previous)
        return previous
    }
}
