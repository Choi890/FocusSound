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

class StringsArranger(
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
        val sustain = when (request.task) {
            MusicTask.SLEEP -> 10.5f
            MusicTask.READING -> 10f
            MusicTask.WORKOUT -> 2.8f
            else -> 7.2f
        }
        chords.forEachIndexed { bar, chord ->
            if (bar % 2 != 0 && request.task in listOf(MusicTask.SLEEP, MusicTask.READING)) return@forEachIndexed
            val voicing = voicingGenerator.padVoicing(key, chord, request.task, random)
            voicing.take(if (request.task == MusicTask.WORKOUT) 2 else 4).forEachIndexed { index, midi ->
                notes += NoteEvent(
                    startBeat = startBeat + bar * 4f + if (index % 2 == 0) 0f else 0.08f,
                    durationBeats = sustain,
                    midiNote = midi.coerceIn(40, 79),
                    velocity = (0.18f + section.energy * 0.22f - index * 0.012f).coerceIn(0.16f, 0.44f),
                    lane = NoteLane.PAD
                )
            }
        }
        return notes
    }
}
