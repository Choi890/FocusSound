package com.focussound.music.melody

import com.focussound.composition.Chord
import com.focussound.composition.MusicalKey
import com.focussound.composition.NoteEvent
import com.focussound.composition.NoteLane
import com.focussound.music.form.MusicSection
import com.focussound.music.form.SectionType
import com.focussound.music.model.LiveCompositionRequest
import com.focussound.music.model.MusicTask

class CounterMelodyGenerator {
    fun generate(
        request: LiveCompositionRequest,
        key: MusicalKey,
        section: MusicSection,
        chords: List<Chord>,
        startBeat: Float
    ): List<NoteEvent> {
        if (request.task in listOf(MusicTask.SLEEP, MusicTask.READING, MusicTask.WORKOUT)) return emptyList()
        if (section.type != SectionType.B && section.type != SectionType.A_VARIATION) return emptyList()

        return chords.filterIndexed { index, _ -> index % 2 == 1 }.mapIndexed { index, chord ->
            val beat = startBeat + (index * 8f) + 4f
            val note = chord.midiNotes(key, octave = 4).lastOrNull() ?: 67
            NoteEvent(
                startBeat = beat,
                durationBeats = 3.25f,
                midiNote = note.coerceIn(55, 72),
                velocity = (0.12f + section.energy * 0.12f).coerceIn(0.12f, 0.28f),
                lane = NoteLane.PAD
            )
        }
    }
}
