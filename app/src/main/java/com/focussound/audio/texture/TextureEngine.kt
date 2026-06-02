package com.focussound.audio.texture

import com.focussound.audio.BrownNoiseGenerator
import com.focussound.audio.PinkNoiseGenerator
import com.focussound.audio.RainTextureGenerator
import com.focussound.audio.WhiteNoiseGenerator
import com.focussound.data.SoundType

class TextureEngine {
    private val white = WhiteNoiseGenerator()
    private val pink = PinkNoiseGenerator()
    private val brown = BrownNoiseGenerator()
    private val rain = RainTextureGenerator()

    fun nextSample(type: SoundType): Float = when (type) {
        SoundType.NONE -> 0f
        SoundType.WHITE -> white.nextSample()
        SoundType.PINK -> pink.nextSample()
        SoundType.BROWN -> brown.nextSample()
        SoundType.RAIN_TEXTURE -> rain.nextSample() * 0.42f
        SoundType.TAPE_TEXTURE -> pink.nextSample() * 0.12f
    }
}
