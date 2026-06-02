package com.focussound.audio

class PinkNoiseGenerator(
    private val whiteNoiseGenerator: WhiteNoiseGenerator = WhiteNoiseGenerator()
) : NoiseGenerator {
    private var b0 = 0f
    private var b1 = 0f
    private var b2 = 0f
    private var b3 = 0f
    private var b4 = 0f
    private var b5 = 0f
    private var b6 = 0f

    override fun nextSample(): Float {
        val white = whiteNoiseGenerator.nextSample()
        b0 = 0.99886f * b0 + white * 0.0555179f
        b1 = 0.99332f * b1 + white * 0.0750759f
        b2 = 0.96900f * b2 + white * 0.1538520f
        b3 = 0.86650f * b3 + white * 0.3104856f
        b4 = 0.55000f * b4 + white * 0.5329522f
        b5 = -0.7616f * b5 - white * 0.0168980f
        val pink = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362f
        b6 = white * 0.115926f
        return (pink * 0.11f).coerceIn(-1f, 1f)
    }

    override fun reset() {
        b0 = 0f
        b1 = 0f
        b2 = 0f
        b3 = 0f
        b4 = 0f
        b5 = 0f
        b6 = 0f
    }
}
