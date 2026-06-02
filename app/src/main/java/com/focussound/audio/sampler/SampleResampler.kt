package com.focussound.audio.sampler

import com.focussound.audio.StereoFrame

class SampleResampler {
    fun sample(sample: SampleData, position: Float): Float {
        val index = position.toInt()
        val fraction = position - index
        return cubic(
            sample.monoAt(index - 1),
            sample.monoAt(index),
            sample.monoAt(index + 1),
            sample.monoAt(index + 2),
            fraction
        )
    }

    fun sampleFrame(sample: SampleData, position: Float): StereoFrame {
        val index = position.toInt()
        val fraction = position - index
        return StereoFrame(
            left = cubic(
                sample.channelAt(index - 1, 0),
                sample.channelAt(index, 0),
                sample.channelAt(index + 1, 0),
                sample.channelAt(index + 2, 0),
                fraction
            ),
            right = cubic(
                sample.channelAt(index - 1, if (sample.channels > 1) 1 else 0),
                sample.channelAt(index, if (sample.channels > 1) 1 else 0),
                sample.channelAt(index + 1, if (sample.channels > 1) 1 else 0),
                sample.channelAt(index + 2, if (sample.channels > 1) 1 else 0),
                fraction
            )
        )
    }

    private fun cubic(y0: Float, y1: Float, y2: Float, y3: Float, fraction: Float): Float {
        val a0 = y3 - y2 - y0 + y1
        val a1 = y0 - y1 - a0
        val a2 = y2 - y0
        val a3 = y1
        return (a0 * fraction * fraction * fraction +
            a1 * fraction * fraction +
            a2 * fraction +
            a3).coerceIn(-1f, 1f)
    }
}
