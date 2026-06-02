package com.focussound.audio

class SpectralTiltFilter {
    private var lowPass = 0f

    fun process(input: Float, highCut: Float, lowAmount: Float): Float {
        val clippedHighCut = highCut.coerceIn(0f, 1f)
        val clippedLow = lowAmount.coerceIn(0f, 1f)
        val smoothing = 0.04f + (1f - clippedHighCut) * 0.22f
        lowPass += smoothing * (input - lowPass)
        val high = input - lowPass
        return (lowPass * (0.75f + clippedLow * 0.55f) + high * (1f - clippedHighCut * 0.82f))
            .coerceIn(-1f, 1f)
    }

    fun reset() {
        lowPass = 0f
    }
}
