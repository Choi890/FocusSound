package com.focussound.audio.synth

import com.focussound.audio.StereoFrame
import com.focussound.composition.CompositionPatch
import com.focussound.composition.NoteEvent
import com.focussound.composition.NoteLane
import com.focussound.composition.bpmToSamples
import kotlin.math.sin

class InternalSynthRenderer(
    private val patch: CompositionPatch,
    private val sampleRate: Int
) {
    private data class ScheduledNote(
        val event: NoteEvent,
        val startSample: Int,
        val durationSamples: Int
    )

    private val loopSamples = bpmToSamples(patch.tempoBpm, patch.totalBeats, sampleRate).coerceAtLeast(sampleRate)
    private val scheduledNotes = patch.notes
        .map { note ->
            ScheduledNote(
                event = note,
                startSample = bpmToSamples(patch.tempoBpm, note.startBeat, sampleRate).floorMod(loopSamples),
                durationSamples = bpmToSamples(patch.tempoBpm, note.durationBeats, sampleRate).coerceAtLeast(120)
            )
        }
        .sortedBy { it.startSample }
    private val activeVoices = mutableListOf<Voice>()
    private var eventIndex = 0
    private var sampleInLoop = 0
    private var globalSample = 0

    fun nextFrame(): StereoFrame {
        if (sampleInLoop == 0) {
            eventIndex = 0
            activeVoices.clear()
        }
        while (eventIndex < scheduledNotes.size && scheduledNotes[eventIndex].startSample <= sampleInLoop) {
            val note = scheduledNotes[eventIndex]
            activeVoices += note.toVoice(globalSample + eventIndex)
            eventIndex += 1
        }

        var left = 0f
        var right = 0f
        val iterator = activeVoices.iterator()
        while (iterator.hasNext()) {
            val voice = iterator.next()
            val sample = voice.next()
            val pan = voicePan(voice.lane)
            left += sample * (1f - pan)
            right += sample * pan
            if (voice.isFinished()) iterator.remove()
        }

        globalSample += 1
        sampleInLoop += 1
        if (sampleInLoop >= loopSamples) sampleInLoop = 0

        val gain = when (patch.genre) {
            com.focussound.composition.CompositionGenre.SLEEP_DRONE -> 0.34f
            com.focussound.composition.CompositionGenre.ORCHESTRAL_PAD -> 0.42f
            else -> 0.38f
        }
        return StereoFrame(
            left = (left * gain).coerceIn(-1f, 1f),
            right = (right * gain).coerceIn(-1f, 1f)
        )
    }

    private fun ScheduledNote.toVoice(seed: Int): Voice {
        val lane = event.lane
        return Voice(
            lane = lane,
            note = event,
            sampleRate = sampleRate,
            durationSamples = durationSamples,
            waveform = when (lane) {
                NoteLane.PAD -> Waveform.SINE
                NoteLane.MELODY -> Waveform.TRIANGLE
                NoteLane.BASS -> Waveform.SOFT_SQUARE
                NoteLane.RHYTHM -> Waveform.NOISE
            },
            attackMillis = when (lane) {
                NoteLane.PAD -> 900
                NoteLane.MELODY -> 80
                NoteLane.BASS -> 45
                NoteLane.RHYTHM -> 4
            },
            releaseMillis = when (lane) {
                NoteLane.PAD -> 1600
                NoteLane.MELODY -> 280
                NoteLane.BASS -> 160
                NoteLane.RHYTHM -> 35
            },
            filterCutoff = when (lane) {
                NoteLane.PAD -> 0.035f
                NoteLane.MELODY -> 0.085f
                NoteLane.BASS -> 0.052f
                NoteLane.RHYTHM -> 0.18f
            },
            seed = seed
        )
    }

    private fun voicePan(lane: NoteLane): Float {
        return when (lane) {
            NoteLane.PAD -> 0.5f + sin(globalSample / sampleRate.toFloat() * 0.18f).toFloat() * 0.12f
            NoteLane.MELODY -> 0.58f
            NoteLane.BASS -> 0.5f
            NoteLane.RHYTHM -> 0.46f
        }.coerceIn(0.12f, 0.88f)
    }
}

private fun Int.floorMod(other: Int): Int = Math.floorMod(this, other)
