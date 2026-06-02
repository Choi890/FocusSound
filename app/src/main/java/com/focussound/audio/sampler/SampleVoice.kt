package com.focussound.audio.sampler

import com.focussound.audio.StereoFrame
import com.focussound.composition.NoteEvent
import com.focussound.composition.NoteLane
import com.focussound.instrument.AdsrParams
import com.focussound.instrument.InstrumentCategory
import com.focussound.instrument.SampleZone
import kotlin.math.pow

class SampleVoice(
    private val sample: SampleData,
    private val zone: SampleZone,
    private val note: NoteEvent,
    private val lane: NoteLane,
    private val category: InstrumentCategory,
    private val adsr: AdsrParams,
    private val outputSampleRate: Int,
    private val durationSamples: Int,
    private val volume: Float,
    detuneCents: Float
) {
    private val resampler = SampleResampler()
    private val pitchRatio = 2.0.pow((note.midiNote - zone.rootMidiNote + detuneCents / 100.0) / 12.0).toFloat()
    private val sourceStep = pitchRatio * sample.sampleRate / outputSampleRate
    private val loopEnabled = category in LOOPING_CATEGORIES && lane != NoteLane.RHYTHM
    private val autoLoopStart = zone.loopStartFrame ?: (sample.frameCount * 0.28f).toInt()
    private val autoLoopEnd = zone.loopEndFrame ?: (sample.frameCount * 0.86f).toInt()
    private val loopStart = autoLoopStart.coerceIn(0, (sample.frameCount - 4).coerceAtLeast(0))
    private val loopEnd = autoLoopEnd.coerceIn(loopStart + 16, (sample.frameCount - 3).coerceAtLeast(loopStart + 16))
    private val loopCrossfadeFrames = ((sample.sampleRate * 0.045f).toInt()).coerceIn(64, (loopEnd - loopStart) / 3)
    private val attackSamples = adsr.attackMillis.toSamples(outputSampleRate).coerceAtLeast(1)
    private val decaySamples = adsr.decayMillis.toSamples(outputSampleRate).coerceAtLeast(1)
    private val releaseSamples = adsr.releaseMillis.toSamples(outputSampleRate).coerceAtLeast(1)
    private val sustainLevel = adsr.sustainLevel.coerceIn(0f, 1f)
    private var sourcePosition = 0f
    private var outputPosition = 0

    fun next(): StereoFrame {
        val gain = envelopeGain() * note.velocity * volume
        val sampleFrame = sampleAtLoopAwarePosition()
        sourcePosition = nextSourcePosition()
        outputPosition += 1
        return StereoFrame(
            left = sampleFrame.left * gain,
            right = sampleFrame.right * gain
        )
    }

    fun isFinished(): Boolean {
        val reachedNaturalEnd = !loopEnabled && sourcePosition >= sample.frameCount - 3
        return outputPosition > durationSamples + releaseSamples || reachedNaturalEnd
    }

    private fun sampleAtLoopAwarePosition(): StereoFrame {
        if (!loopEnabled || outputPosition >= durationSamples) {
            return resampler.sampleFrame(sample, sourcePosition)
        }
        val distanceToEnd = loopEnd - sourcePosition
        if (distanceToEnd > loopCrossfadeFrames || sourcePosition < loopStart) {
            return resampler.sampleFrame(sample, sourcePosition)
        }

        val blend = (1f - distanceToEnd / loopCrossfadeFrames).coerceIn(0f, 1f)
        val wrappedPosition = loopStart + (loopCrossfadeFrames - distanceToEnd)
        val current = resampler.sampleFrame(sample, sourcePosition)
        val wrapped = resampler.sampleFrame(sample, wrappedPosition)
        return StereoFrame(
            left = current.left * (1f - blend) + wrapped.left * blend,
            right = current.right * (1f - blend) + wrapped.right * blend
        )
    }

    private fun nextSourcePosition(): Float {
        val next = sourcePosition + sourceStep
        return if (loopEnabled && outputPosition < durationSamples && next >= loopEnd) {
            loopStart + (next - loopEnd)
        } else {
            next
        }
    }

    private fun envelopeGain(): Float {
        return when {
            outputPosition < attackSamples -> {
                val progress = outputPosition / attackSamples.toFloat()
                progress * progress
            }
            outputPosition < attackSamples + decaySamples -> {
                val progress = (outputPosition - attackSamples) / decaySamples.toFloat()
                1f - (1f - sustainLevel) * progress
            }
            outputPosition < durationSamples -> sustainLevel
            outputPosition < durationSamples + releaseSamples -> {
                val progress = (outputPosition - durationSamples) / releaseSamples.toFloat()
                sustainLevel * (1f - progress) * (1f - progress * 0.35f)
            }
            else -> 0f
        }.coerceIn(0f, 1f)
    }

    private companion object {
        val LOOPING_CATEGORIES = setOf(
            InstrumentCategory.STRINGS,
            InstrumentCategory.PAD,
            InstrumentCategory.WOODWIND,
            InstrumentCategory.BASS,
            InstrumentCategory.BRASS
        )
    }
}

private fun Int.toSamples(sampleRate: Int): Int = (this * sampleRate / 1000f).toInt()
