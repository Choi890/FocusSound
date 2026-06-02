package com.focussound.music.generation

import com.focussound.composition.Chord
import com.focussound.composition.MusicalKey
import com.focussound.composition.NoteEvent
import com.focussound.composition.NoteLane
import com.focussound.music.model.GeneratedPiece
import kotlin.math.roundToInt

data class MusicFingerprint(
    val formSignature: String,
    val chordNgrams: List<String>,
    val motifContourHash: String,
    val rhythmPatternHash: String,
    val instrumentRoleHash: String,
    val key: MusicalKey,
    val tempo: Int
) {
    companion object {
        fun from(piece: GeneratedPiece): MusicFingerprint {
            return MusicFingerprint(
                formSignature = piece.form.sections.joinToString("-") { it.type.name },
                chordNgrams = chordNgrams(piece.chords),
                motifContourHash = contourHash(piece.notes),
                rhythmPatternHash = rhythmHash(piece.notes),
                instrumentRoleHash = piece.notes.map { it.lane.name }.distinct().sorted().joinToString("-"),
                key = piece.key,
                tempo = piece.tempoBpm
            )
        }

        private fun chordNgrams(chords: List<Chord>): List<String> {
            val tokens = chords.map { "${it.degree}:${it.quality}:${it.extension}" }
            if (tokens.size < 3) return tokens
            return tokens.windowed(3).map { it.joinToString(">") }
        }

        private fun contourHash(notes: List<NoteEvent>): String {
            val melody = notes.filter { it.lane == NoteLane.MELODY }.sortedBy { it.startBeat }.take(24)
            if (melody.size < 2) return "no-melody"
            return melody.zipWithNext()
                .joinToString("") { (a, b) ->
                    when {
                        b.midiNote - a.midiNote > 2 -> "U"
                        a.midiNote - b.midiNote > 2 -> "D"
                        else -> "S"
                    }
                }
        }

        private fun rhythmHash(notes: List<NoteEvent>): String {
            return notes
                .filter { it.lane == NoteLane.MELODY || it.lane == NoteLane.RHYTHM }
                .sortedBy { it.startBeat }
                .take(32)
                .map { (it.durationBeats * 4f).roundToInt() }
                .joinToString("-")
                .ifBlank { "no-rhythm" }
        }
    }
}
