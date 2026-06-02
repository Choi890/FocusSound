package com.focussound.music.model

import com.focussound.composition.Chord

data class ChordEvent(
    val startBeat: Float,
    val durationBeats: Float,
    val chord: Chord
)
