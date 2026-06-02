package com.focussound.audio.sampler

import com.focussound.audio.StereoFrame

data class SampleData(
    val frames: FloatArray,
    val sampleRate: Int,
    val channels: Int
) {
    val frameCount: Int
        get() = frames.size / channels.coerceAtLeast(1)

    fun monoAt(frameIndex: Int): Float {
        if (frameIndex !in 0 until frameCount) return 0f
        val base = frameIndex * channels
        return if (channels == 1) {
            frames[base]
        } else {
            ((frames[base] + frames[base + 1]) * 0.5f).coerceIn(-1f, 1f)
        }
    }

    fun channelAt(frameIndex: Int, channel: Int): Float {
        if (frameIndex !in 0 until frameCount) return 0f
        val safeChannel = channel.coerceIn(0, channels - 1)
        return frames[frameIndex * channels + safeChannel].coerceIn(-1f, 1f)
    }

    fun stereoAt(frameIndex: Int): StereoFrame {
        if (frameIndex !in 0 until frameCount) return StereoFrame(0f, 0f)
        return if (channels == 1) {
            val mono = frames[frameIndex].coerceIn(-1f, 1f)
            StereoFrame(mono, mono)
        } else {
            StereoFrame(channelAt(frameIndex, 0), channelAt(frameIndex, 1))
        }
    }
}
