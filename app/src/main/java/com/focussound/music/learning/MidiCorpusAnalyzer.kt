package com.focussound.music.learning

import java.io.File

data class SymbolicMusicDocument(
    val name: String,
    val sectionLabels: List<String> = emptyList(),
    val chordDegrees: List<Int> = emptyList(),
    val melodyIntervals: List<Int> = emptyList(),
    val rhythmValues: List<Float> = emptyList()
)

class MidiCorpusAnalyzer {
    fun analyze(files: List<File>): List<SymbolicMusicDocument> {
        return files
            .filter { it.exists() && it.isFile }
            .map { file ->
                SymbolicMusicDocument(
                    name = file.nameWithoutExtension,
                    sectionLabels = inferSections(file.nameWithoutExtension)
                )
            }
    }

    private fun inferSections(name: String): List<String> {
        return when {
            name.contains("sleep", ignoreCase = true) -> listOf("INTRO", "A", "A_VARIATION", "OUTRO")
            name.contains("coding", ignoreCase = true) -> listOf("INTRO", "A", "B", "BREAK", "A_VARIATION")
            else -> listOf("INTRO", "A", "A_VARIATION", "B", "A_VARIATION")
        }
    }
}
