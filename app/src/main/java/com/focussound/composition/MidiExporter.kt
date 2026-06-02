package com.focussound.composition

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

class MidiExporter(
    private val ticksPerQuarter: Int = 480
) {
    private data class MidiEvent(
        val tick: Int,
        val status: Int,
        val note: Int,
        val velocity: Int
    )

    fun exportToFile(patch: CompositionPatch, outputFile: File): File {
        outputFile.parentFile?.mkdirs()
        val track = ByteArrayOutputStream()
        track.writeVarLen(0)
        track.write(byteArrayOf(0xFF.toByte(), 0x51.toByte(), 0x03, ((microsecondsPerQuarter(patch) shr 16) and 0xff).toByte(), ((microsecondsPerQuarter(patch) shr 8) and 0xff).toByte(), (microsecondsPerQuarter(patch) and 0xff).toByte()))

        val events = patch.notes.flatMap { note ->
            val start = (note.startBeat * ticksPerQuarter).roundToInt()
            val end = ((note.startBeat + note.durationBeats) * ticksPerQuarter).roundToInt()
            val velocity = (note.velocity.coerceIn(0f, 1f) * 100).roundToInt().coerceIn(1, 110)
            listOf(
                MidiEvent(start, 0x90, note.midiNote.coerceIn(0, 127), velocity),
                MidiEvent(end, 0x80, note.midiNote.coerceIn(0, 127), 0)
            )
        }.sortedWith(compareBy<MidiEvent> { it.tick }.thenBy { it.status })

        var previousTick = 0
        events.forEach { event ->
            track.writeVarLen((event.tick - previousTick).coerceAtLeast(0))
            track.write(event.status)
            track.write(event.note)
            track.write(event.velocity)
            previousTick = event.tick
        }
        track.writeVarLen(0)
        track.write(byteArrayOf(0xFF.toByte(), 0x2F, 0x00))

        FileOutputStream(outputFile).use { out ->
            out.writeAscii("MThd")
            out.writeIntBe(6)
            out.writeShortBe(0)
            out.writeShortBe(1)
            out.writeShortBe(ticksPerQuarter)
            val trackBytes = track.toByteArray()
            out.writeAscii("MTrk")
            out.writeIntBe(trackBytes.size)
            out.write(trackBytes)
        }
        return outputFile
    }

    private fun microsecondsPerQuarter(patch: CompositionPatch): Int {
        return (60_000_000 / patch.tempoBpm.coerceAtLeast(1))
    }
}

private fun ByteArrayOutputStream.writeVarLen(value: Int) {
    var buffer = value and 0x7F
    var remaining = value shr 7
    while (remaining > 0) {
        buffer = buffer shl 8
        buffer = buffer or ((remaining and 0x7F) or 0x80)
        remaining = remaining shr 7
    }
    while (true) {
        write(buffer and 0xFF)
        if (buffer and 0x80 != 0) {
            buffer = buffer shr 8
        } else {
            break
        }
    }
}

private fun FileOutputStream.writeAscii(value: String) {
    write(value.toByteArray(Charsets.US_ASCII))
}

private fun FileOutputStream.writeIntBe(value: Int) {
    write((value shr 24) and 0xff)
    write((value shr 16) and 0xff)
    write((value shr 8) and 0xff)
    write(value and 0xff)
}

private fun FileOutputStream.writeShortBe(value: Int) {
    write((value shr 8) and 0xff)
    write(value and 0xff)
}
