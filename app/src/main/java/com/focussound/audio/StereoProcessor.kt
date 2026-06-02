package com.focussound.audio

data class StereoFrame(
    val left: Float,
    val right: Float
)

class StereoProcessor {
    private var delayIndex = 0
    private val delay = FloatArray(96)

    fun process(mono: Float, width: Float): StereoFrame {
        val clippedWidth = width.coerceIn(0f, 1f)
        val delayed = delay[delayIndex]
        delay[delayIndex] = mono
        delayIndex = (delayIndex + 1) % delay.size

        val side = (mono - delayed) * clippedWidth * 0.55f
        return StereoFrame(
            left = (mono + side).coerceIn(-1f, 1f),
            right = (mono - side).coerceIn(-1f, 1f)
        )
    }
}
