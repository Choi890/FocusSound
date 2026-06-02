package com.focussound.composition

import com.focussound.ai.FatigueTarget
import com.focussound.data.FocusMode
import com.focussound.instrument.InstrumentPolicy
import com.focussound.playback.PlaybackMode

data class CompositionRequest(
    val mode: FocusMode,
    val prompt: String?,
    val playbackMode: PlaybackMode,
    val instrumentPolicy: InstrumentPolicy,
    val loopBars: Int = 16,
    val targetFatigue: FatigueTarget = FatigueTarget.LOW
)
