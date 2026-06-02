package com.focussound.music.realtime

import com.focussound.composition.Chord
import com.focussound.composition.MusicalKey
import com.focussound.music.generation.MusicFingerprint
import com.focussound.music.melody.Motif
import com.focussound.music.model.GeneratedPiece

data class MusicMemory(
    val key: MusicalKey,
    val motif: Motif,
    val recentProgressions: List<List<Chord>> = emptyList(),
    val sectionEnergy: Float = 0.3f,
    val recentFingerprints: List<MusicFingerprint> = emptyList(),
    val recentSectionEnergy: List<Float> = emptyList(),
    val instrumentUse: Map<String, Int> = emptyMap()
) {
    fun remember(chords: List<Chord>, energy: Float): MusicMemory {
        return copy(
            recentProgressions = (recentProgressions + listOf(chords.take(4))).takeLast(6),
            sectionEnergy = energy,
            recentSectionEnergy = (recentSectionEnergy + energy).takeLast(12)
        )
    }

    fun remember(piece: GeneratedPiece): MusicMemory {
        val fingerprint = MusicFingerprint.from(piece)
        val nextUse = instrumentUse.toMutableMap()
        piece.instrumentNames.forEach { name ->
            nextUse[name] = (nextUse[name] ?: 0) + 1
        }
        return copy(
            key = piece.key,
            recentProgressions = (recentProgressions + piece.sections.map { it.chords.take(4) }).takeLast(8),
            sectionEnergy = piece.sections.lastOrNull()?.section?.energy ?: sectionEnergy,
            recentFingerprints = (recentFingerprints + fingerprint).takeLast(16),
            recentSectionEnergy = (recentSectionEnergy + piece.sections.map { it.section.energy }).takeLast(16),
            instrumentUse = nextUse
        )
    }
}
