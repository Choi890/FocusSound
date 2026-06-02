package com.focussound.audio.sampler

import com.focussound.audio.StereoFrame

class StudioReverb(sampleRate: Int) {
    private val combsLeft = listOf(
        FeedbackDelay((sampleRate * 0.0297f).toInt(), 0.72f, 0.34f),
        FeedbackDelay((sampleRate * 0.0371f).toInt(), 0.68f, 0.32f),
        FeedbackDelay((sampleRate * 0.0411f).toInt(), 0.65f, 0.31f)
    )
    private val combsRight = listOf(
        FeedbackDelay((sampleRate * 0.0313f).toInt(), 0.71f, 0.34f),
        FeedbackDelay((sampleRate * 0.0397f).toInt(), 0.67f, 0.32f),
        FeedbackDelay((sampleRate * 0.0437f).toInt(), 0.64f, 0.31f)
    )
    private val preDelayLeft = SimpleDelay((sampleRate * 0.018f).toInt())
    private val preDelayRight = SimpleDelay((sampleRate * 0.023f).toInt())

    fun process(input: StereoFrame, amount: Float): StereoFrame {
        val mix = amount.coerceIn(0f, 0.45f)
        if (mix <= 0.001f) return input

        val preLeft = preDelayLeft.process(input.left)
        val preRight = preDelayRight.process(input.right)
        var wetLeft = 0f
        for (index in combsLeft.indices) {
            wetLeft += combsLeft[index].process(preLeft)
        }
        wetLeft /= combsLeft.size

        var wetRight = 0f
        for (index in combsRight.indices) {
            wetRight += combsRight[index].process(preRight)
        }
        wetRight /= combsRight.size

        return StereoFrame(
            left = (input.left * (1f - mix) + wetLeft * mix).coerceIn(-1f, 1f),
            right = (input.right * (1f - mix) + wetRight * mix).coerceIn(-1f, 1f)
        )
    }
}

private class SimpleDelay(size: Int) {
    private val buffer = FloatArray(size.coerceAtLeast(1))
    private var index = 0

    fun process(input: Float): Float {
        val delayed = buffer[index]
        buffer[index] = input
        index = (index + 1) % buffer.size
        return delayed
    }
}

private class FeedbackDelay(
    size: Int,
    private val feedback: Float,
    private val damping: Float
) {
    private val buffer = FloatArray(size.coerceAtLeast(1))
    private var index = 0
    private var damped = 0f

    fun process(input: Float): Float {
        val delayed = buffer[index]
        damped += (delayed - damped) * (1f - damping)
        buffer[index] = input + damped * feedback
        index = (index + 1) % buffer.size
        return delayed
    }
}
