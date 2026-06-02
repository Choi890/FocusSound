package com.focussound.composition

import kotlin.math.roundToInt

enum class PitchClass(val semitone: Int, val label: String) {
    C(0, "도"),
    CS(1, "도#"),
    D(2, "레"),
    DS(3, "레#"),
    E(4, "미"),
    F(5, "파"),
    FS(6, "파#"),
    G(7, "솔"),
    GS(8, "솔#"),
    A(9, "라"),
    AS(10, "라#"),
    B(11, "시");

    fun transpose(semitones: Int): PitchClass {
        val target = (semitone + semitones).floorMod(12)
        return entries.first { it.semitone == target }
    }
}

enum class ScaleType(val label: String) {
    MAJOR("장조"),
    MINOR("단조"),
    DORIAN("도리안"),
    PENTATONIC("펜타토닉"),
    AMBIENT("앰비언트")
}

data class MusicalKey(
    val root: PitchClass,
    val scaleType: ScaleType
) {
    val label: String
        get() = "${root.label} ${scaleType.label}"

    fun scaleSemitones(): List<Int> = when (scaleType) {
        ScaleType.MAJOR -> listOf(0, 2, 4, 5, 7, 9, 11)
        ScaleType.MINOR -> listOf(0, 2, 3, 5, 7, 8, 10)
        ScaleType.DORIAN -> listOf(0, 2, 3, 5, 7, 9, 10)
        ScaleType.PENTATONIC -> listOf(0, 2, 4, 7, 9)
        ScaleType.AMBIENT -> listOf(0, 2, 3, 7, 10)
    }

    fun midiNote(degree: Int, octave: Int): Int {
        val scale = scaleSemitones()
        val normalizedDegree = degree.floorMod(scale.size)
        val octaveOffset = degree.floorDiv(scale.size)
        return 12 * (octave + 1 + octaveOffset) + root.semitone + scale[normalizedDegree]
    }
}

enum class ChordQuality {
    MAJOR,
    MINOR,
    SUS2,
    SUS4,
    DIMINISHED
}

enum class ChordExtension {
    NONE,
    SIXTH,
    SEVENTH,
    NINTH
}

data class Chord(
    val root: PitchClass,
    val quality: ChordQuality,
    val extension: ChordExtension = ChordExtension.NONE,
    val degree: Int = 0
) {
    val label: String
        get() = root.label + when (quality) {
            ChordQuality.MAJOR -> " 장"
            ChordQuality.MINOR -> " 단"
            ChordQuality.SUS2 -> " 서스2"
            ChordQuality.SUS4 -> " 서스4"
            ChordQuality.DIMINISHED -> " 감"
        } + when (extension) {
            ChordExtension.NONE -> ""
            ChordExtension.SIXTH -> " 6"
            ChordExtension.SEVENTH -> " 7"
            ChordExtension.NINTH -> " 9"
        }

    fun midiNotes(key: MusicalKey, octave: Int): List<Int> {
        val third = when (quality) {
            ChordQuality.MAJOR -> 4
            ChordQuality.MINOR -> 3
            ChordQuality.SUS2 -> 2
            ChordQuality.SUS4 -> 5
            ChordQuality.DIMINISHED -> 3
        }
        val fifth = if (quality == ChordQuality.DIMINISHED) 6 else 7
        val rootMidi = 12 * (octave + 1) + root.semitone
        val notes = mutableListOf(rootMidi, rootMidi + third, rootMidi + fifth)
        when (extension) {
            ChordExtension.SIXTH -> notes += rootMidi + 9
            ChordExtension.SEVENTH -> notes += rootMidi + 10
            ChordExtension.NINTH -> notes += rootMidi + 14
            ChordExtension.NONE -> Unit
        }
        return notes.map { key.closestInScale(it) }
    }
}

enum class NoteLane {
    PAD,
    MELODY,
    BASS,
    RHYTHM
}

data class NoteEvent(
    val startBeat: Float,
    val durationBeats: Float,
    val midiNote: Int,
    val velocity: Float,
    val lane: NoteLane
)

enum class CompositionGenre(val label: String) {
    LOFI("로파이"),
    CLASSICAL_MINIMAL("클래식 미니멀"),
    ORCHESTRAL_PAD("오케스트라 패드"),
    SLEEP_DRONE("수면 드론"),
    AMBIENT_CODING("앰비언트 코딩")
}

private fun Int.floorMod(other: Int): Int = Math.floorMod(this, other)

private fun Int.floorDiv(other: Int): Int = Math.floorDiv(this, other)

private fun MusicalKey.closestInScale(midiNote: Int): Int {
    val pitch = midiNote.floorMod(12)
    val allowed = scaleSemitones().map { (root.semitone + it).floorMod(12) }
    if (pitch in allowed) return midiNote
    val best = (-2..2).minBy { offset ->
        val candidatePitch = (pitch + offset).floorMod(12)
        if (candidatePitch in allowed) kotlin.math.abs(offset) else 99
    }
    return midiNote + best
}

fun bpmToSamples(tempoBpm: Int, beats: Float, sampleRate: Int): Int {
    return ((60f / tempoBpm.coerceAtLeast(1)) * beats * sampleRate).roundToInt()
}
