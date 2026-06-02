package com.focussound.audio.sampler

import com.focussound.audio.StereoFrame
import com.focussound.audio.synth.InternalSynthRenderer
import com.focussound.composition.CompositionPatch
import com.focussound.composition.NoteEvent
import com.focussound.composition.NoteLane
import com.focussound.composition.bpmToSamples
import com.focussound.instrument.InstrumentPreset
import com.focussound.instrument.InstrumentSet
import com.focussound.instrument.InstrumentSourceType

class SampleInstrumentEngine(
    private val patch: CompositionPatch,
    private val instrumentSet: InstrumentSet?,
    private val sampleRate: Int
) {
    private data class ScheduledNote(
        val event: NoteEvent,
        val startSample: Int,
        val durationSamples: Int
    )

    private data class ActiveVoice(
        val voice: SampleVoice,
        val lane: NoteLane
    )

    private data class PanGains(
        val leftFromLeft: Float,
        val leftFromRight: Float,
        val rightFromLeft: Float,
        val rightFromRight: Float
    )

    private val cache = SampleCache()
    private val reverb = StudioReverb(sampleRate)
    private val loopSamples = bpmToSamples(patch.tempoBpm, patch.totalBeats, sampleRate).coerceAtLeast(sampleRate)
    private val sampleNotes = patch.notes.filter { it.lane.preset()?.isSampleBacked == true }
    private val fallbackNotes = patch.notes.filter { it.lane.preset()?.isSampleBacked != true }
    private val fallback = fallbackNotes
        .takeIf { it.isNotEmpty() }
        ?.let { InternalSynthRenderer(patch.copy(notes = it), sampleRate) }
    private val hasSampleBackedPreset = sampleNotes.isNotEmpty()
    private val notes = sampleNotes.map {
        ScheduledNote(
            event = it,
            startSample = (bpmToSamples(patch.tempoBpm, it.startBeat, sampleRate) +
                it.renderStartSpreadSamples()).floorMod(loopSamples),
            durationSamples = bpmToSamples(patch.tempoBpm, it.durationBeats, sampleRate).coerceAtLeast(120)
        )
    }.sortedBy { it.startSample }
    private val activeVoices = mutableListOf<ActiveVoice>()
    private var eventIndex = 0
    private var sampleInLoop = 0

    init {
        cache.preload(
            notes.mapNotNull { scheduled ->
                scheduled.event.lane.preset()
                    ?.takeIf(InstrumentPreset::isSampleBacked)
                    ?.let { preset -> findZone(preset, scheduled.event)?.samplePath }
            }
        )
    }

    fun nextFrame(): StereoFrame {
        if (sampleInLoop == 0) {
            eventIndex = 0
            activeVoices.clear()
        }
        while (eventIndex < notes.size && notes[eventIndex].startSample <= sampleInLoop) {
            val scheduled = notes[eventIndex]
            trimActiveVoicesFor(scheduled.event.lane)
            buildVoice(scheduled)?.let { activeVoices += ActiveVoice(it, scheduled.event.lane) }
            eventIndex += 1
        }

        var left = 0f
        var right = 0f
        val iterator = activeVoices.iterator()
        var activeCount = 0
        while (iterator.hasNext()) {
            val active = iterator.next()
            val voice = active.voice
            val lane = active.lane
            val frame = voice.next()
            val pan = lane.panGains()
            val laneGain = lane.gain()
            left += (frame.left * pan.leftFromLeft + frame.right * pan.leftFromRight) * laneGain
            right += (frame.left * pan.rightFromLeft + frame.right * pan.rightFromRight) * laneGain
            activeCount += 1
            if (voice.isFinished()) iterator.remove()
        }

        sampleInLoop = (sampleInLoop + 1) % loopSamples
        val dryFrame = StereoFrame(left * SAMPLE_MASTER_GAIN, right * SAMPLE_MASTER_GAIN)
        val sampleFrame = reverb.process(dryFrame, patch.genreReverbAmount())
        val dryLevel = (kotlin.math.abs(sampleFrame.left) + kotlin.math.abs(sampleFrame.right)) * 0.5f
        val fallbackFrame = fallback?.nextFrame()
        val fallbackGain = when {
            fallbackFrame == null -> 0f
            !hasSampleBackedPreset -> 1f
            activeCount == 0 || dryLevel < 0.003f -> 0.22f
            dryLevel < 0.025f -> 0.12f
            else -> 0.07f
        }
        return StereoFrame(
            left = sampleFrame.left + (fallbackFrame?.left ?: 0f) * fallbackGain,
            right = sampleFrame.right + (fallbackFrame?.right ?: 0f) * fallbackGain
        )
    }

    private fun buildVoice(scheduled: ScheduledNote): SampleVoice? {
        val preset = scheduled.event.lane.preset() ?: return null
        if (preset.sourceType != InstrumentSourceType.USER_IMPORTED_WAV &&
            preset.sourceType != InstrumentSourceType.BUILT_IN_WAV
        ) {
            return null
        }
        val zone = findZone(preset, scheduled.event) ?: return null
        val sample = cache.get(zone.samplePath) ?: return null
        return SampleVoice(
            sample = sample,
            zone = zone,
            note = scheduled.event,
            lane = scheduled.event.lane,
            category = preset.category,
            adsr = preset.adsr,
            outputSampleRate = sampleRate,
            durationSamples = scheduled.durationSamples,
            volume = preset.defaultVolume,
            detuneCents = scheduled.event.humanDetuneCents()
        )
    }

    private fun NoteLane.preset(): InstrumentPreset? = when (this) {
        NoteLane.MELODY -> instrumentSet?.melody
        NoteLane.PAD -> instrumentSet?.pad
        NoteLane.BASS -> instrumentSet?.bass
        NoteLane.RHYTHM -> instrumentSet?.rhythm
    }

    private fun findZone(preset: InstrumentPreset, event: NoteEvent): com.focussound.instrument.SampleZone? {
        val velocity = (event.velocity.coerceIn(0f, 1f) * 127).toInt().coerceIn(1, 127)
        return preset.sampleZones.firstOrNull { zone ->
            event.midiNote in zone.minMidiNote..zone.maxMidiNote &&
                velocity in zone.minVelocity..zone.maxVelocity
        } ?: preset.sampleZones.firstOrNull { zone ->
            event.midiNote in zone.minMidiNote..zone.maxMidiNote
        } ?: preset.sampleZones.minByOrNull { zone ->
            kotlin.math.abs(zone.rootMidiNote - event.midiNote)
        }
    }

    private fun trimActiveVoicesFor(lane: NoteLane) {
        activeVoices.removeAll { it.voice.isFinished() }
        val laneLimit = lane.maxVoiceCount()
        if (activeVoices.count { it.lane == lane } >= laneLimit) {
            activeVoices.indexOfFirst { it.lane == lane }.takeIf { it >= 0 }?.let(activeVoices::removeAt)
        }
        while (activeVoices.size >= MAX_ACTIVE_VOICES) {
            val removeIndex = activeVoices.indexOfFirst { it.lane == NoteLane.PAD }
                .takeIf { it >= 0 }
                ?: activeVoices.indexOfFirst { it.lane == NoteLane.RHYTHM }.takeIf { it >= 0 }
                ?: 0
            activeVoices.removeAt(removeIndex)
        }
    }

    private fun NoteLane.maxVoiceCount(): Int = when (this) {
        NoteLane.MELODY -> 7
        NoteLane.PAD -> 14
        NoteLane.BASS -> 5
        NoteLane.RHYTHM -> 8
    }

    private fun NoteLane.panGains(): PanGains = when (this) {
        NoteLane.MELODY -> PanGains(
            leftFromLeft = 0.862f,
            leftFromRight = 0.051f,
            rightFromLeft = 0.062f,
            rightFromRight = 0.913f
        )
        NoteLane.PAD,
        NoteLane.BASS -> PanGains(
            leftFromLeft = 0.889f,
            leftFromRight = 0.057f,
            rightFromLeft = 0.057f,
            rightFromRight = 0.889f
        )
        NoteLane.RHYTHM -> PanGains(
            leftFromLeft = 0.905f,
            leftFromRight = 0.060f,
            rightFromLeft = 0.053f,
            rightFromRight = 0.871f
        )
    }

    private fun NoteLane.gain(): Float = when (this) {
        NoteLane.MELODY -> 1.18f
        NoteLane.PAD -> 1.08f
        NoteLane.BASS -> 1.22f
        NoteLane.RHYTHM -> 0.9f
    }

    private fun CompositionPatch.genreReverbAmount(): Float = when (genre) {
        com.focussound.composition.CompositionGenre.SLEEP_DRONE -> 0.4f
        com.focussound.composition.CompositionGenre.ORCHESTRAL_PAD -> 0.34f
        com.focussound.composition.CompositionGenre.AMBIENT_CODING -> 0.26f
        com.focussound.composition.CompositionGenre.CLASSICAL_MINIMAL -> 0.22f
        com.focussound.composition.CompositionGenre.LOFI -> 0.16f
    }

    private fun NoteEvent.humanDetuneCents(): Float {
        val hash = (midiNote * 31 + (startBeat * 100).toInt() * 17 + lane.ordinal * 47)
        return ((hash.floorMod(17) - 8) * 0.45f).coerceIn(-4f, 4f)
    }

    private fun NoteEvent.renderStartSpreadSamples(): Int {
        val hash = (midiNote * 37 + (startBeat * 100).toInt() * 19 + lane.ordinal * 53)
        return when (lane) {
            NoteLane.PAD -> hash.floorMod((sampleRate * 0.004f).toInt().coerceAtLeast(1))
            NoteLane.MELODY -> hash.floorMod((sampleRate * 0.0015f).toInt().coerceAtLeast(1))
            NoteLane.BASS,
            NoteLane.RHYTHM -> 0
        }
    }
}

private fun Int.floorMod(other: Int): Int = Math.floorMod(this, other)

private const val MAX_ACTIVE_VOICES = 28
private const val SAMPLE_MASTER_GAIN = 1.55f
