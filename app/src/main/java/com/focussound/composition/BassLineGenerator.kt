package com.focussound.composition

import com.focussound.data.FocusMode

class BassLineGenerator {
    fun generate(key: MusicalKey, chords: List<Chord>, intent: CompositionIntent): List<NoteEvent> {
        if (intent.genre == CompositionGenre.SLEEP_DRONE || intent.mode == FocusMode.SLEEP) return emptyList()
        return chords.mapIndexed { index, chord ->
            NoteEvent(
                startBeat = index * 4f,
                durationBeats = if (intent.genre == CompositionGenre.LOFI) 2.75f else 3.5f,
                midiNote = (12 * 3 + chord.root.semitone).coerceIn(36, 52),
                velocity = if (intent.genre == CompositionGenre.LOFI) 0.32f else 0.26f,
                lane = NoteLane.BASS
            )
        }
    }
}
