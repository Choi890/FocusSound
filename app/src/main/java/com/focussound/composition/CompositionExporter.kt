package com.focussound.composition

import com.focussound.audio.synth.OfflineWavRenderer
import java.io.File

class CompositionExporter(
    private val wavRenderer: OfflineWavRenderer = OfflineWavRenderer(),
    private val midiExporter: MidiExporter = MidiExporter()
) {
    fun exportWav(patch: CompositionPatch, directory: File): File {
        val file = File(directory, "${patch.safeFileName()}.wav")
        return wavRenderer.renderToFile(patch, file)
    }

    fun exportMidi(patch: CompositionPatch, directory: File): File {
        val file = File(directory, "${patch.safeFileName()}.mid")
        return midiExporter.exportToFile(patch, file)
    }

    private fun CompositionPatch.safeFileName(): String {
        return name.lowercase()
            .replace(Regex("[^a-z0-9가-힣_-]+"), "_")
            .trim('_')
            .ifBlank { "focus_composition" } + "_${id.take(8)}"
    }
}
