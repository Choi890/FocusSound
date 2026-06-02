package com.focussound.audio.synth

import com.focussound.composition.NoteLane

class SoftPianoSynth(sampleRate: Int) : PolySynth(
    sampleRate = sampleRate,
    waveform = Waveform.TRIANGLE,
    attackMillis = 55,
    releaseMillis = 360,
    filterCutoff = 0.075f,
    lane = NoteLane.MELODY
)
