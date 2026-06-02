package com.focussound.audio

import kotlin.random.Random

class WhiteNoiseGenerator(
    private val random: Random = Random.Default
) : NoiseGenerator {
    override fun nextSample(): Float = random.nextFloat() * 2f - 1f
}
