package com.focussound.music.arrangement

import com.focussound.composition.Chord
import com.focussound.composition.MusicalKey
import com.focussound.composition.NoteEvent
import com.focussound.composition.NoteLane
import com.focussound.music.form.MusicSection
import com.focussound.music.harmony.VoicingGenerator
import com.focussound.music.model.LiveCompositionRequest
import com.focussound.music.model.MusicTask
import kotlin.random.Random

class PianoPatternGenerator(
    private val voicingGenerator: VoicingGenerator = VoicingGenerator()
) {
    fun generate(
        request: LiveCompositionRequest,
        key: MusicalKey,
        section: MusicSection,
        chords: List<Chord>,
        startBeat: Float,
        random: Random
    ): List<NoteEvent> {
        val notes = mutableListOf<NoteEvent>()
        val pattern = patternFor(request.task, random)
        chords.forEachIndexed { bar, chord ->
            val barStart = startBeat + bar * 4f
            val voicing = voicingGenerator.pianoVoicing(key, chord, random)
            pattern.forEachIndexed { index, offset ->
                val midi = voicing[index % voicing.size]
                notes += NoteEvent(
                    startBeat = barStart + offset,
                    durationBeats = if (request.task == MusicTask.SLEEP) 3.8f else 2.25f,
                    midiNote = midi.coerceIn(43, 78),
                    velocity = (0.24f + section.energy * 0.28f).coerceIn(0.22f, 0.58f),
                    lane = NoteLane.MELODY
                )
            }
            if (request.task != MusicTask.SLEEP && bar % 2 == 0) {
                notes += NoteEvent(
                    startBeat = barStart,
                    durationBeats = 3.6f,
                    midiNote = (voicing.first() - 12).coerceIn(36, 55),
                    velocity = (0.2f + section.energy * 0.18f).coerceIn(0.18f, 0.38f),
                    lane = NoteLane.BASS
                )
            }
        }
        return notes
    }

    private fun patternFor(task: MusicTask, random: Random): List<Float> = when (task) {
        MusicTask.SLEEP -> listOf(0f, 2f)
        MusicTask.READING -> listOf(0f)
        MusicTask.WORKOUT -> listOf(0f, 1f, 2f, 3f)
        MusicTask.CODING -> listOf(0f, 1.5f, 2.5f)
        else -> listOf(
            listOf(0f, 1f, 2.5f),
            listOf(0f, 2f, 3f),
            listOf(0f, 1.5f)
        ).random(random)
    }
}
