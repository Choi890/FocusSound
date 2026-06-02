package com.focussound.music.model

import com.focussound.composition.Chord
import com.focussound.composition.CompositionGenre
import com.focussound.composition.CompositionPatch
import com.focussound.composition.MusicalKey
import com.focussound.composition.NoteEvent
import com.focussound.data.FocusMode
import com.focussound.music.form.MusicForm

data class GeneratedPiece(
    val task: MusicTask,
    val style: MusicStyle,
    val form: MusicForm,
    val key: MusicalKey,
    val tempoBpm: Int,
    val chords: List<Chord>,
    val notes: List<NoteEvent>,
    val sections: List<GeneratedSection>,
    val fatigueScore: Int,
    val durationMinutes: Int,
    val instrumentNames: List<String>
) {
    fun toCompositionPatch(name: String): CompositionPatch {
        return CompositionPatch(
            name = name,
            mode = task.toFocusMode(),
            genre = style.toCompositionGenre(task),
            tempoBpm = tempoBpm,
            key = key,
            chordProgression = chords,
            notes = notes.sortedWith(compareBy<NoteEvent> { it.startBeat }.thenBy { it.lane.ordinal }),
            melodyDensity = taskAdjustedMelodyDensity(),
            rhythmDensity = taskAdjustedRhythmDensity(),
            harmonicComplexity = when (task) {
                MusicTask.SLEEP, MusicTask.READING -> 0.18f
                MusicTask.WORKOUT -> 0.28f
                else -> 0.42f
            },
            padAmount = when (task) {
                MusicTask.SLEEP, MusicTask.READING -> 0.9f
                MusicTask.WORKOUT -> 0.2f
                else -> 0.62f
            },
            moodKeywords = listOf(task.label, style.label, "작업 맞춤 문법 생성"),
            instrumentNames = instrumentNames,
            fatigueScore = fatigueScore,
            durationMinutes = durationMinutes
        )
    }

    private fun taskAdjustedMelodyDensity(): Float = when (task) {
        MusicTask.SLEEP -> 0.06f
        MusicTask.READING -> 0.08f
        MusicTask.STUDY -> 0.22f
        MusicTask.RELAX -> 0.24f
        MusicTask.CODING -> 0.28f
        MusicTask.WORKOUT -> 0.48f
    }

    private fun taskAdjustedRhythmDensity(): Float = when (task) {
        MusicTask.SLEEP, MusicTask.READING -> 0f
        MusicTask.STUDY -> 0.06f
        MusicTask.RELAX -> 0.08f
        MusicTask.CODING -> 0.22f
        MusicTask.WORKOUT -> 0.72f
    }
}

private fun MusicTask.toFocusMode(): FocusMode = when (this) {
    MusicTask.STUDY -> FocusMode.STUDY
    MusicTask.SLEEP -> FocusMode.SLEEP
    MusicTask.CODING -> FocusMode.CODING
    MusicTask.READING -> FocusMode.READING
    MusicTask.RELAX -> FocusMode.READING
    MusicTask.WORKOUT -> FocusMode.CODING
}

fun MusicStyle.toCompositionGenre(task: MusicTask): CompositionGenre = when {
    task == MusicTask.SLEEP -> CompositionGenre.SLEEP_DRONE
    this == MusicStyle.LOFI -> CompositionGenre.LOFI
    this == MusicStyle.CLASSICAL_MINIMAL || this == MusicStyle.RELAXING_PIANO -> CompositionGenre.CLASSICAL_MINIMAL
    this == MusicStyle.ORCHESTRAL_PAD -> CompositionGenre.ORCHESTRAL_PAD
    this == MusicStyle.SLEEP_DRONE -> CompositionGenre.SLEEP_DRONE
    else -> CompositionGenre.AMBIENT_CODING
}
