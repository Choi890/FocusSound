package com.focussound.audio.synth

import com.focussound.composition.NoteEvent
import com.focussound.composition.NoteLane

open class PolySynth(
    private val sampleRate: Int,
    private val waveform: Waveform,
    private val attackMillis: Int,
    private val releaseMillis: Int,
    private val filterCutoff: Float,
    private val lane: NoteLane
) {
    private val voices = mutableListOf<Voice>()

    fun trigger(note: NoteEvent, durationSamples: Int, seed: Int) {
        voices += Voice(
            lane = lane,
            note = note.copy(lane = lane),
            sampleRate = sampleRate,
            durationSamples = durationSamples,
            waveform = waveform,
            attackMillis = attackMillis,
            releaseMillis = releaseMillis,
            filterCutoff = filterCutoff,
            seed = seed
        )
    }

    fun next(): Float {
        var sample = 0f
        val iterator = voices.iterator()
        while (iterator.hasNext()) {
            val voice = iterator.next()
            sample += voice.next()
            if (voice.isFinished()) iterator.remove()
        }
        return sample.coerceIn(-1f, 1f)
    }

    fun clear() {
        voices.clear()
    }
}
