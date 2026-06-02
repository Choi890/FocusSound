package com.focussound.audio.playback

class PlaybackClock(
    private val sampleRate: Int
) {
    fun beatAtSample(sampleIndex: Long, tempoBpm: Int): Float {
        val seconds = sampleIndex / sampleRate.toFloat()
        return seconds * tempoBpm / 60f
    }
}
