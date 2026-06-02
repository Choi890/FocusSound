package com.focussound.playback

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Process
import com.focussound.audio.sampler.SampleInstrumentEngine
import com.focussound.audio.synth.InternalSynthRenderer
import com.focussound.audio.synth.SoftLimiter
import com.focussound.composition.CompositionPatch
import com.focussound.instrument.InstrumentSet
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max

class CompositionPlaybackEngine(
    private val sampleRate: Int = 44_100
) {
    private val running = AtomicBoolean(false)
    private var audioTrack: AudioTrack? = null
    private var playbackThread: Thread? = null

    @Synchronized
    fun start(patch: CompositionPatch, instrumentSet: InstrumentSet? = null) {
        stop()
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
        val renderer = if (instrumentSet == null) {
            InternalSynthRenderer(patch, sampleRate)
        } else {
            null
        }
        val sampleRenderer = instrumentSet?.let { SampleInstrumentEngine(patch, it, sampleRate) }
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

        audioTrack = track
        running.set(true)
        primeWithSilence(track, framesPerBuffer)
        track.play()
        playbackThread = Thread(
            {
                Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)
                renderLoop(track, renderer, sampleRenderer, framesPerBuffer)
            },
            "FocusSound-CompositionPlayback"
        ).also { it.start() }
    }

    @Synchronized
    fun stop() {
        if (!running.get() && audioTrack == null) return
        running.set(false)
        playbackThread?.takeIf { it != Thread.currentThread() }?.join(800)
        runCatching {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.stop()
        }
        runCatching { audioTrack?.release() }
        audioTrack = null
        playbackThread = null
    }

    fun release() = stop()

    private fun renderLoop(
        track: AudioTrack,
        renderer: InternalSynthRenderer?,
        sampleRenderer: SampleInstrumentEngine?,
        framesPerBuffer: Int
    ) {
        val limiter = SoftLimiter()
        val buffer = ShortArray(framesPerBuffer * 2)
        while (running.get()) {
            var index = 0
            repeat(framesPerBuffer) {
                val frame = sampleRenderer?.nextFrame() ?: renderer!!.nextFrame()
                buffer[index++] = (limiter.process(frame.left) * Short.MAX_VALUE).toInt().toShort()
                buffer[index++] = (limiter.process(frame.right) * Short.MAX_VALUE).toInt().toShort()
            }
            if (track.write(buffer, 0, buffer.size, AudioTrack.WRITE_BLOCKING) < 0) {
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

    private companion object {
        const val CHANNEL_COUNT = 2
        const val BYTES_PER_STEREO_FRAME = 4
        const val RENDER_FRAMES_PER_BUFFER = 4_096
        const val AUDIO_BUFFER_MULTIPLIER = 8
        const val START_SILENCE_BUFFER_COUNT = 10
    }
}
