package com.focussound.audio

interface NoiseGenerator {
    fun nextSample(): Float

    fun reset() = Unit
}
