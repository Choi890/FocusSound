package com.focussound.music.generation

import com.focussound.composition.Chord
import com.focussound.composition.NoteEvent
import com.focussound.composition.NoteLane
import com.focussound.music.knowledge.MusicAvoidRule
import com.focussound.music.knowledge.TaskMusicProfile
import com.focussound.music.model.MusicTask
import kotlin.random.Random

class VariationEngine {
    fun varyNotes(
        notes: List<NoteEvent>,
        profile: TaskMusicProfile,
        temperature: GenerationTemperature,
        sectionIndex: Int,
        random: Random
    ): List<NoteEvent> {
        return notes.mapNotNull { note ->
            if (MusicAvoidRule.PERCUSSION in profile.avoidRules && note.lane == NoteLane.RHYTHM) return@mapNotNull null
            if (MusicAvoidRule.BUSY_MELODY in profile.avoidRules && note.lane == NoteLane.MELODY && random.nextFloat() < 0.18f) return@mapNotNull null
            val octaveShift = when {
                MusicAvoidRule.BRIGHT_HIGH_REGISTER in profile.avoidRules && note.midiNote > 72 -> -12
                profile.task == MusicTask.WORKOUT && note.lane == NoteLane.BASS && sectionIndex % 2 == 1 -> 0
                random.nextFloat() < temperature.arrangementTemperature * 0.08f && note.lane == NoteLane.MELODY -> listOf(-12, 0, 12).random(random)
                else -> 0
            }
            val delayed = if (
                profile.task !in listOf(MusicTask.SLEEP, MusicTask.READING) &&
                random.nextFloat() < temperature.transitionTemperature * 0.06f
            ) {
                random.nextFloat() * 0.08f
            } else {
                0f
            }
            note.copy(
                startBeat = note.startBeat + delayed,
                midiNote = (note.midiNote + octaveShift).coerceIn(28, 88),
                velocity = (note.velocity * velocityScale(profile.task, note.lane)).coerceIn(0.08f, 0.8f)
            )
        }
    }

    fun varyChordOrder(chords: List<Chord>, temperature: GenerationTemperature, random: Random): List<Chord> {
        if (chords.size < 8 || random.nextFloat() > temperature.harmonyTemperature * 0.22f) return chords
        val first = chords.take(4)
        val rest = chords.drop(4)
        return if (random.nextBoolean()) rest + first else first + rest.chunked(4).flatMap { it.reversed() }
    }

    private fun velocityScale(task: MusicTask, lane: NoteLane): Float = when (task) {
        MusicTask.SLEEP -> if (lane == NoteLane.MELODY) 0.62f else 0.76f
        MusicTask.READING -> if (lane == NoteLane.MELODY) 0.58f else 0.78f
        MusicTask.STUDY -> if (lane == NoteLane.RHYTHM) 0.5f else 0.9f
        MusicTask.RELAX -> 0.86f
        MusicTask.CODING -> if (lane == NoteLane.RHYTHM) 0.62f else 0.92f
        MusicTask.WORKOUT -> if (lane == NoteLane.RHYTHM || lane == NoteLane.BASS) 1.08f else 0.96f
    }
}
