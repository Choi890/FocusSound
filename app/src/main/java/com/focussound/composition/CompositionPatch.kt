package com.focussound.composition

import com.focussound.data.FocusMode
import java.util.UUID

data class CompositionPatch(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val mode: FocusMode,
    val genre: CompositionGenre,
    val tempoBpm: Int,
    val key: MusicalKey,
    val chordProgression: List<Chord>,
    val notes: List<NoteEvent>,
    val melodyDensity: Float,
    val rhythmDensity: Float,
    val harmonicComplexity: Float,
    val padAmount: Float = 0.58f,
    val moodKeywords: List<String> = emptyList(),
    val instrumentNames: List<String> = emptyList(),
    val fatigueScore: Int,
    val durationMinutes: Int,
    val createdAtMillis: Long = System.currentTimeMillis()
) {
    val totalBeats: Float
        get() = maxOf(
            16f,
            chordProgression.size * 4f,
            notes.maxOfOrNull { it.startBeat + it.durationBeats } ?: 16f
        )

    val chordProgressionLabel: String
        get() = chordProgression.joinToString(" - ") { it.label }
}
