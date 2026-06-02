package com.focussound.audio.sampler

import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SampleDecoder {
    fun decodeWav(file: File): SampleData? = runCatching {
        val bytes = file.readBytes()
        if (bytes.size < 44 || String(bytes, 0, 4, Charsets.US_ASCII) != "RIFF") return null
        var offset = 12
        var channels = 1
        var sampleRate = 44_100
        var bitsPerSample = 16
        var dataOffset = -1
        var dataSize = 0
        while (offset + 8 <= bytes.size) {
            val chunkId = String(bytes, offset, 4, Charsets.US_ASCII)
            val chunkSize = bytes.readIntLe(offset + 4)
            val chunkData = offset + 8
            when (chunkId) {
                "fmt " -> {
                    val audioFormat = bytes.readShortLe(chunkData).toInt()
                    channels = bytes.readShortLe(chunkData + 2).toInt().coerceIn(1, 2)
                    sampleRate = bytes.readIntLe(chunkData + 4)
                    bitsPerSample = bytes.readShortLe(chunkData + 14).toInt()
                    if (audioFormat != 1 || bitsPerSample !in listOf(16, 24, 32)) return null
                }
                "data" -> {
                    dataOffset = chunkData
                    dataSize = chunkSize
                }
            }
            offset = chunkData + chunkSize + (chunkSize % 2)
        }
        if (dataOffset < 0 || dataSize <= 0) return null
        val bytesPerSample = bitsPerSample / 8
        val sampleCount = dataSize / bytesPerSample
        val frames = FloatArray(sampleCount)
        if (bitsPerSample == 16) {
            val buffer = ByteBuffer.wrap(bytes, dataOffset, dataSize).order(ByteOrder.LITTLE_ENDIAN)
            repeat(sampleCount) { index ->
                frames[index] = buffer.short / Short.MAX_VALUE.toFloat()
            }
        } else {
            repeat(sampleCount) { index ->
                val sampleOffset = dataOffset + index * bytesPerSample
                frames[index] = when (bitsPerSample) {
                    24 -> bytes.readSigned24Le(sampleOffset) / 8_388_608f
                    else -> bytes.readIntLe(sampleOffset) / 2_147_483_648f
                }.coerceIn(-1f, 1f)
            }
        }
        SampleData(frames, sampleRate, channels)
    }.getOrNull()
}

private fun ByteArray.readIntLe(offset: Int): Int {
    return (this[offset].toInt() and 0xff) or
        ((this[offset + 1].toInt() and 0xff) shl 8) or
        ((this[offset + 2].toInt() and 0xff) shl 16) or
        ((this[offset + 3].toInt() and 0xff) shl 24)
}

private fun ByteArray.readShortLe(offset: Int): Short {
    return ((this[offset].toInt() and 0xff) or ((this[offset + 1].toInt() and 0xff) shl 8)).toShort()
}

private fun ByteArray.readSigned24Le(offset: Int): Int {
    val raw = (this[offset].toInt() and 0xff) or
        ((this[offset + 1].toInt() and 0xff) shl 8) or
        ((this[offset + 2].toInt() and 0xff) shl 16)
    return if ((raw and 0x800000) != 0) raw or -0x1000000 else raw
}
