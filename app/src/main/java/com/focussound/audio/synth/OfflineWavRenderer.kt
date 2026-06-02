package com.focussound.audio.synth

import com.focussound.composition.CompositionPatch
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

class OfflineWavRenderer(
    private val sampleRate: Int = 44_100
) {
    fun renderToFile(
        patch: CompositionPatch,
        outputFile: File,
        durationSeconds: Int = patch.durationMinutes.coerceAtLeast(1) * 60
    ): File {
        outputFile.parentFile?.mkdirs()
        val totalFrames = durationSeconds.coerceAtLeast(1) * sampleRate
        val dataBytes = totalFrames * CHANNELS * BYTES_PER_SAMPLE
        val renderer = InternalSynthRenderer(patch, sampleRate)
        val limiter = SoftLimiter()

        BufferedOutputStream(FileOutputStream(outputFile)).use { out ->
            out.writeWavHeader(dataBytes)
            repeat(totalFrames) {
                val frame = renderer.nextFrame()
                val left = (limiter.process(frame.left) * Short.MAX_VALUE)
                    .roundToInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                    .toShort()
                val right = (limiter.process(frame.right) * Short.MAX_VALUE)
                    .roundToInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                    .toShort()
                out.writeShortLe(left)
                out.writeShortLe(right)
            }
        }
        return outputFile
    }

    private fun BufferedOutputStream.writeWavHeader(dataBytes: Int) {
        writeAscii("RIFF")
        writeIntLe(36 + dataBytes)
        writeAscii("WAVE")
        writeAscii("fmt ")
        writeIntLe(16)
        writeShortLe(1)
        writeShortLe(CHANNELS)
        writeIntLe(sampleRate)
        writeIntLe(sampleRate * CHANNELS * BYTES_PER_SAMPLE)
        writeShortLe(CHANNELS * BYTES_PER_SAMPLE)
        writeShortLe(16)
        writeAscii("data")
        writeIntLe(dataBytes)
    }

    private fun BufferedOutputStream.writeAscii(value: String) {
        write(value.toByteArray(Charsets.US_ASCII))
    }

    private fun BufferedOutputStream.writeIntLe(value: Int) {
        write(value and 0xff)
        write((value shr 8) and 0xff)
        write((value shr 16) and 0xff)
        write((value shr 24) and 0xff)
    }

    private fun BufferedOutputStream.writeShortLe(value: Int) {
        write(value and 0xff)
        write((value shr 8) and 0xff)
    }

    private fun BufferedOutputStream.writeShortLe(value: Short) {
        writeShortLe(value.toInt())
    }

    private companion object {
        const val CHANNELS = 2
        const val BYTES_PER_SAMPLE = 2
    }
}
