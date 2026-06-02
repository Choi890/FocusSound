package com.focussound.audio.sampler

import com.focussound.audio.StereoFrame
import kotlin.math.cos
import kotlin.math.sin

class SampleMixer {
    fun pan(sample: Float, pan: Float): StereoFrame {
        val clipped = pan.coerceIn(0f, 1f) * HALF_PI
        return StereoFrame(
            left = sample * cos(clipped).toFloat(),
            right = sample * sin(clipped).toFloat()
        )
    }

    fun pan(frame: StereoFrame, pan: Float): StereoFrame {
        val clipped = pan.coerceIn(0f, 1f) * HALF_PI
        val leftGain = cos(clipped).toFloat()
        val rightGain = sin(clipped).toFloat()
        return StereoFrame(
            left = (frame.left * (0.62f + leftGain * 0.38f) + frame.right * 0.08f * leftGain).coerceIn(-1f, 1f),
            right = (frame.right * (0.62f + rightGain * 0.38f) + frame.left * 0.08f * rightGain).coerceIn(-1f, 1f)
        )
    }

    private companion object {
        const val HALF_PI = 1.5707964f
    }
}
