package com.focussound.audio.synth

import com.focussound.composition.NoteLane

class BassSynth(sampleRate: Int) : PolySynth(
    sampleRate = sampleRate,
    waveform = Waveform.SOFT_SQUARE,
    attackMillis = 35,
    releaseMillis = 180,
    filterCutoff = 0.045f,
    lane = NoteLane.BASS
)
