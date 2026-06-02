package com.focussound.audio.synth

import com.focussound.composition.NoteLane

class NoiseTextureSynth(sampleRate: Int) : PolySynth(
    sampleRate = sampleRate,
    waveform = Waveform.NOISE,
    attackMillis = 3,
    releaseMillis = 45,
    filterCutoff = 0.16f,
    lane = NoteLane.RHYTHM
)
