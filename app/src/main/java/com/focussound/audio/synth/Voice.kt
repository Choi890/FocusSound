package com.focussound.audio.synth

import com.focussound.composition.NoteEvent
import com.focussound.composition.NoteLane
import kotlin.math.pow

class Voice(
    val lane: NoteLane,
    private val note: NoteEvent,
    private val sampleRate: Int,
    private val durationSamples: Int,
    waveform: Waveform,
    attackMillis: Int,
    releaseMillis: Int,
    private val filterCutoff: Float,
    seed: Int
) {
    private val oscillator = Oscillator(waveform, seed)
    private val envelope = ADSREnvelope(
        attackSamples = millisToSamples(attackMillis),
        decaySamples = millisToSamples(160),
        sustainLevel = when (lane) {
            NoteLane.PAD -> 0.78f
            NoteLane.MELODY -> 0.58f
            NoteLane.BASS -> 0.7f
            NoteLane.RHYTHM -> 0.42f
        },
        releaseSamples = millisToSamples(releaseMillis)
    )
    private val filter = SimpleFilter()
    private var positionSamples = 0

    fun next(): Float {
        val frequency = if (lane == NoteLane.RHYTHM) {
            when (note.midiNote) {
                36 -> 72f
                38 -> 180f
                else -> 420f
            }
        } else {
            note.midiNote.toFrequencyHz()
        }
        val raw = oscillator.next(frequency, sampleRate)
        val shaped = when (lane) {
            NoteLane.RHYTHM -> raw * (1f - positionSamples / durationSamples.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
            else -> raw
        }
        val gain = envelope.gain(positionSamples, durationSamples) * note.velocity
        positionSamples += 1
        return filter.lowPass(shaped * gain, filterCutoff)
    }

    fun isFinished(): Boolean = positionSamples >= durationSamples + envelope.tailSamples

    private fun millisToSamples(millis: Int): Int = (sampleRate * millis / 1000f).toInt().coerceAtLeast(1)
}

private fun Int.toFrequencyHz(): Float {
    return (440.0 * 2.0.pow((this - 69) / 12.0)).toFloat()
}
