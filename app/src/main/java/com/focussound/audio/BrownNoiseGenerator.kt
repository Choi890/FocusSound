package com.focussound.audio

class BrownNoiseGenerator(
    private val whiteNoiseGenerator: WhiteNoiseGenerator = WhiteNoiseGenerator()
) : NoiseGenerator {
    private var lastOut = 0f

    override fun nextSample(): Float {
        val white = whiteNoiseGenerator.nextSample()
        lastOut = ((lastOut + 0.02f * white) / 1.02f).coerceIn(-1f, 1f)
        return (lastOut * 3.5f).coerceIn(-1f, 1f)
    }

    override fun reset() {
        lastOut = 0f
    }
}
