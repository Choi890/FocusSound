package com.focussound.playback

import com.focussound.composition.CompositionPatch

enum class CompositionPlaybackStatus {
    STOPPED,
    PLAYING,
    PAUSED
}

data class CompositionPlaybackState(
    val status: CompositionPlaybackStatus = CompositionPlaybackStatus.STOPPED,
    val patch: CompositionPatch? = null,
    val startedAtMillis: Long = 0L
) {
    val isActive: Boolean = status != CompositionPlaybackStatus.STOPPED
}
