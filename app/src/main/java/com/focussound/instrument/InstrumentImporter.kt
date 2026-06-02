package com.focussound.instrument

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.util.UUID

class InstrumentImporter {
    fun importWav(
        context: Context,
        uri: Uri,
        role: InstrumentRole? = null
    ): InstrumentPreset {
        val displayName = context.displayName(uri).ifBlank { "가져온 WAV" }
        val resolvedRole = role ?: inferRole(displayName)
        val target = copyToInstrumentStore(context, uri, extension = "wav")
        return InstrumentPreset(
            name = displayName.removeSuffix(".wav"),
            role = resolvedRole,
            sourceType = InstrumentSourceType.USER_IMPORTED_WAV,
            sourcePath = target.absolutePath,
            sampleZones = listOf(
                SampleZone(
                    samplePath = target.absolutePath,
                    rootMidiNote = when (resolvedRole) {
                        InstrumentRole.BASS -> 36
                        InstrumentRole.PAD -> 60
                        InstrumentRole.RHYTHM -> 42
                        InstrumentRole.MELODY -> 60
                    },
                    minMidiNote = when (resolvedRole) {
                        InstrumentRole.BASS -> 24
                        InstrumentRole.PAD -> 48
                        InstrumentRole.RHYTHM -> 35
                        InstrumentRole.MELODY -> 48
                    },
                    maxMidiNote = when (resolvedRole) {
                        InstrumentRole.BASS -> 52
                        InstrumentRole.PAD -> 76
                        InstrumentRole.RHYTHM -> 46
                        InstrumentRole.MELODY -> 84
                    }
                )
            ),
            adsr = when (resolvedRole) {
                InstrumentRole.MELODY -> AdsrParams(35, 120, 0.68f, 320)
                InstrumentRole.PAD -> AdsrParams(900, 360, 0.78f, 1600)
                InstrumentRole.BASS -> AdsrParams(20, 80, 0.72f, 160)
                InstrumentRole.RHYTHM -> AdsrParams(3, 20, 0.35f, 40)
            },
            defaultVolume = when (resolvedRole) {
                InstrumentRole.MELODY -> 0.46f
                InstrumentRole.PAD -> 0.52f
                InstrumentRole.BASS -> 0.4f
                InstrumentRole.RHYTHM -> 0.22f
            },
            brightness = 0.42f,
            warmth = 0.7f
        )
    }

    fun importSf2(context: Context, uri: Uri): InstrumentPreset {
        val displayName = context.displayName(uri).ifBlank { "가져온 SF2" }
        val target = copyToInstrumentStore(context, uri, extension = "sf2")
        return InstrumentPreset(
            name = displayName.removeSuffix(".sf2"),
            role = InstrumentRole.MELODY,
            sourceType = InstrumentSourceType.USER_IMPORTED_SF2,
            sourcePath = target.absolutePath,
            sampleZones = emptyList(),
            adsr = AdsrParams(80, 160, 0.64f, 480),
            defaultVolume = 0.45f,
            brightness = 0.45f,
            warmth = 0.68f
        )
    }

    private fun copyToInstrumentStore(context: Context, uri: Uri, extension: String): File {
        val directory = File(context.filesDir, "instruments").apply { mkdirs() }
        val target = File(directory, "${UUID.randomUUID()}.$extension")
        context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Cannot open selected file." }
            target.outputStream().use { output -> input.copyTo(output) }
        }
        return target
    }

    private fun inferRole(displayName: String): InstrumentRole {
        val text = displayName.lowercase()
        return when {
            text.contains("bass") || text.contains("sub") || text.contains("low") -> InstrumentRole.BASS
            text.contains("pad") || text.contains("string") || text.contains("drone") -> InstrumentRole.PAD
            text.contains("drum") || text.contains("click") || text.contains("perc") || text.contains("kick") -> InstrumentRole.RHYTHM
            else -> InstrumentRole.MELODY
        }
    }

    private fun Context.displayName(uri: Uri): String {
        return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else ""
        }.orEmpty()
    }
}
