package com.focussound.audio

import kotlin.random.Random

class RainTextureGenerator(
    private val random: Random = Random.Default
) : NoiseGenerator {
    private var dripHold = 0f
    private var dripDecay = 0.96f

    override fun nextSample(): Float {
        val hiss = (random.nextFloat() * 2f - 1f) * 0.32f
        if (random.nextFloat() > 0.9968f) {
            dripHold = random.nextFloat() * 1.5f - 0.75f
            dripDecay = 0.90f + random.nextFloat() * 0.08f
        }
        dripHold *= dripDecay
        return (hiss + dripHold * 0.38f).coerceIn(-1f, 1f)
    }

    override fun reset() {
        dripHold = 0f
    }
}
