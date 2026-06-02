package com.focussound.audio.synth

import com.focussound.composition.NoteLane

class WarmPadSynth(sampleRate: Int) : PolySynth(
    sampleRate = sampleRate,
    waveform = Waveform.SINE,
    attackMillis = 1100,
    releaseMillis = 1900,
    filterCutoff = 0.032f,
    lane = NoteLane.PAD
)
