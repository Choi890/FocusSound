package com.focussound.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Process
import com.focussound.audio.sampler.SampleInstrumentEngine
import com.focussound.audio.synth.Mixer
import com.focussound.audio.tone.RealtimeToneProcessor
import com.focussound.audio.tone.ToneControlState
import com.focussound.composition.CompositionPatch
import com.focussound.data.FocusMode
import com.focussound.data.SoundProfile
import com.focussound.instrument.InstrumentSet
import com.focussound.playback.PlaybackMode
import com.focussound.sounddesign.NoiseType
import com.focussound.sounddesign.SoundPatch
import com.focussound.sounddesign.toSoundPatch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max

class FocusSoundEngine(
    private val sampleRate: Int = 44_100
) {
    private val running = AtomicBoolean(false)
    private val envelope = AudioEnvelope(sampleRate)
    private val modulator = AudioModulator(sampleRate)
    private val toneProcessor = RealtimeToneProcessor()

    @Volatile
    private var stopAfterFade = false

    @Volatile
    private var paused = false

    @Volatile
    private var audioTrack: AudioTrack? = null

    private var playbackThread: Thread? = null

    @Synchronized
    fun start(
        profile: SoundProfile,
        fadeInMillis: Long = DEFAULT_FADE_IN_MS,
        sleepFadeOutMillis: Long = 0L
    ) {
        start(
            patch = profile.toSoundPatch(durationMinutes = 25),
            fadeInMillis = fadeInMillis,
            sleepFadeOutMillis = sleepFadeOutMillis
        )
    }

    @Synchronized
    fun start(
        patch: SoundPatch,
        compositionPatch: CompositionPatch? = null,
        instrumentSet: InstrumentSet? = null,
        playbackMode: PlaybackMode = PlaybackMode.SOUND_DESIGN,
        fadeInMillis: Long = DEFAULT_FADE_IN_MS,
        sleepFadeOutMillis: Long = 0L
    ) {
        stopInternal(fadeOutMillis = 0L)

        val minBufferBytes = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        ).takeIf { it > 0 } ?: (sampleRate / 10 * BYTES_PER_STEREO_FRAME)
        val framesPerBuffer = max(RENDER_FRAMES_PER_BUFFER, minBufferBytes / BYTES_PER_STEREO_FRAME)
        val bufferSizeBytes = max(
            minBufferBytes * AUDIO_BUFFER_MULTIPLIER,
            sampleRate * 3 / 4 * BYTES_PER_STEREO_FRAME
        )

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setTransferMode(AudioTrack.MODE_STREAM)
            .setBufferSizeInBytes(bufferSizeBytes)
            .build()

        stopAfterFade = false
        paused = false
        envelope.setImmediate(0f)
        envelope.fadeTo(1f, fadeInMillis)
        modulator.configure(
            profile = patch.toSoundProfile(),
            sleepFadeOutMillis = sleepFadeOutMillis,
            modulationDepth = patch.modulationDepth,
            modulationRateHz = patch.modulationRateHz
        )
        toneProcessor.updateTone(
            ToneControlState(
                brightness = patch.brightness,
                warmth = patch.warmth,
                coldness = (1f - patch.warmth) * 0.22f
            )
        )
        val synth = compositionPatch?.let { SampleInstrumentEngine(it, instrumentSet, sampleRate) }

        audioTrack = track
        running.set(true)
        primeWithSilence(track, framesPerBuffer)
        track.play()
        playbackThread = Thread(
            {
                Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)
                renderLoop(track, patch, synth, playbackMode, framesPerBuffer)
            },
            "FocusSound-AudioTrack"
        ).also { it.start() }
    }

    fun pause(fadeOutMillis: Long = PAUSE_FADE_MS) {
        if (!running.get()) return
        paused = true
        envelope.fadeTo(0f, fadeOutMillis)
    }

    fun resume(fadeInMillis: Long = RESUME_FADE_MS) {
        if (!running.get()) return
        paused = false
        envelope.fadeTo(1f, fadeInMillis)
    }

    @Synchronized
    fun stop(fadeOutMillis: Long = DEFAULT_FADE_OUT_MS) {
        stopInternal(fadeOutMillis)
    }

    fun release() {
        stopInternal(fadeOutMillis = 0L)
    }

    fun updateTone(state: ToneControlState) {
        toneProcessor.updateTone(state)
    }

    private fun stopInternal(fadeOutMillis: Long) {
        val track = audioTrack
        if (!running.get() && track == null) return

        stopAfterFade = true
        paused = false
        envelope.fadeTo(0f, fadeOutMillis)

        val joinTimeout = fadeOutMillis.coerceAtMost(MAX_STOP_WAIT_MS) + STOP_JOIN_GRACE_MS
        playbackThread
            ?.takeIf { it != Thread.currentThread() }
            ?.join(joinTimeout)

        running.set(false)

        runCatching {
            track?.pause()
            track?.flush()
            track?.stop()
        }
        runCatching { track?.release() }

        playbackThread = null
        audioTrack = null
        stopAfterFade = false
        paused = false
    }

    private fun renderLoop(
        track: AudioTrack,
        patch: SoundPatch,
        synth: SampleInstrumentEngine?,
        playbackMode: PlaybackMode,
        framesPerBuffer: Int
    ) {
        val generator = when (patch.baseNoiseType) {
            NoiseType.WHITE -> WhiteNoiseGenerator()
            NoiseType.PINK -> PinkNoiseGenerator()
            NoiseType.BROWN -> BrownNoiseGenerator()
        }
        val rain = RainTextureGenerator()
        val pad = AmbientPadGenerator(sampleRate)
        val filter = AudioFilter(sampleRate)
        val tiltLeft = SpectralTiltFilter()
        val tiltRight = SpectralTiltFilter()
        val stereo = StereoProcessor()
        val limiter = SoftLimiter()
        val pcmBuffer = ShortArray(framesPerBuffer * CHANNEL_COUNT)
        val waveformBuffer = FloatArray(AudioSampleBus.WAVEFORM_SIZE)
        var waveformIndex = 0
        var publishCounter = 0

        while (running.get()) {
            var pcmIndex = 0
            for (frame in 0 until framesPerBuffer) {
                val envelopeGain = envelope.nextGain()
                val effectiveGain = if (paused && envelope.isSilentAtTarget()) 0f else envelopeGain

                val textureGain = when (playbackMode) {
                    PlaybackMode.SOUND_DESIGN -> 1f
                    PlaybackMode.AI_COMPOSITION_WITH_TEXTURE -> 0.24f
                    PlaybackMode.AI_COMPOSITION_ONLY -> 0f
                }
                val base = filter.process(generator.nextSample(), patch.toSoundProfile()) *
                    textureGain *
                    patch.noiseLayerAmount.coerceIn(0f, 1f)
                val rainLayer = rain.nextSample() * patch.rainLayerAmount.coerceIn(0f, 1f) * textureGain
                val padLayer = pad.nextSample() * patch.padLayerAmount.coerceIn(0f, 1f) * textureGain
                val mixed = (base + rainLayer * 0.42f + padLayer * 0.35f).coerceIn(-1f, 1f)
                val stereoFrame = stereo.process(mixed, patch.stereoWidth)
                val synthFrame = synth?.nextFrame() ?: StereoFrame(0f, 0f)
                val compositionGain = if (playbackMode == PlaybackMode.SOUND_DESIGN) {
                    COMPOSITION_LAYER_GAIN
                } else {
                    1f
                }
                val composedFrame = Mixer.add(stereoFrame, synthFrame, compositionGain)
                val processed = toneProcessor.process(
                    StereoFrame(
                        left = tiltLeft.process(composedFrame.left, patch.highCut, patch.lowAmount),
                        right = tiltRight.process(composedFrame.right, patch.highCut, patch.lowAmount)
                    )
                )
                val left = limiter.process(
                    processed.left *
                        effectiveGain *
                        modulator.nextGain() *
                        patch.mode.outputGain() *
                        OUTPUT_GAIN
                )
                val right = limiter.process(
                    processed.right *
                        effectiveGain *
                        patch.mode.outputGain() *
                        OUTPUT_GAIN
                )

                pcmBuffer[pcmIndex++] = (left.coerceIn(-1f, 1f) * Short.MAX_VALUE).toInt().toShort()
                pcmBuffer[pcmIndex++] = (right.coerceIn(-1f, 1f) * Short.MAX_VALUE).toInt().toShort()

                if (frame % WAVEFORM_DOWNSAMPLE == 0) {
                    waveformBuffer[waveformIndex] = ((left + right) * 0.5f).coerceIn(-1f, 1f)
                    waveformIndex = (waveformIndex + 1) % waveformBuffer.size
                }
            }

            val written = track.write(pcmBuffer, 0, pcmBuffer.size, AudioTrack.WRITE_BLOCKING)
            if (written < 0) {
                running.set(false)
            }

            publishCounter += 1
            if (publishCounter >= WAVEFORM_PUBLISH_EVERY_BUFFERS) {
                publishCounter = 0
                AudioSampleBus.publish(waveformBuffer)
            }

            if (stopAfterFade && envelope.isSilentAtTarget()) {
                running.set(false)
            }
        }
    }

    private fun primeWithSilence(track: AudioTrack, framesPerBuffer: Int) {
        val silence = ShortArray(framesPerBuffer * CHANNEL_COUNT * START_SILENCE_BUFFER_COUNT)
        var offset = 0
        while (offset < silence.size) {
            val written = track.write(
                silence,
                offset,
                silence.size - offset,
                AudioTrack.WRITE_NON_BLOCKING
            )
            if (written <= 0) break
            offset += written
        }
    }

    private fun FocusMode.outputGain(): Float = when (this) {
        FocusMode.SLEEP -> 0.72f
        FocusMode.READING -> 0.8f
        FocusMode.STUDY -> 0.88f
        FocusMode.CODING -> 0.9f
    }

    private companion object {
        const val CHANNEL_COUNT = 2
        const val BYTES_PER_STEREO_FRAME = 4
        const val RENDER_FRAMES_PER_BUFFER = 4_096
        const val AUDIO_BUFFER_MULTIPLIER = 8
        const val START_SILENCE_BUFFER_COUNT = 10
        const val OUTPUT_GAIN = 0.72f
        const val COMPOSITION_LAYER_GAIN = 0.7f
        const val DEFAULT_FADE_IN_MS = 1800L
        const val DEFAULT_FADE_OUT_MS = 1100L
        const val PAUSE_FADE_MS = 450L
        const val RESUME_FADE_MS = 700L
        const val MAX_STOP_WAIT_MS = 2_500L
        const val STOP_JOIN_GRACE_MS = 450L
        const val WAVEFORM_DOWNSAMPLE = 8
        const val WAVEFORM_PUBLISH_EVERY_BUFFERS = 2
    }
}
