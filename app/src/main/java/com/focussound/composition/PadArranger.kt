package com.focussound.composition

class PadArranger {
    fun arrange(key: MusicalKey, chords: List<Chord>, intent: CompositionIntent): List<NoteEvent> {
        val chordBeats = 4f
        val padAmount = intent.padAmountHint ?: if (intent.padFocus) 0.72f else 0.5f
        val velocity = ((if (intent.genre == CompositionGenre.SLEEP_DRONE) 0.26f else 0.34f) * (0.72f + padAmount * 0.5f))
            .coerceIn(0.18f, 0.48f)
        return chords.flatMapIndexed { index, chord ->
            chord.midiNotes(key, octave = if (intent.mode.name == "SLEEP") 3 else 4)
                .map { midi ->
                    NoteEvent(
                        startBeat = index * chordBeats,
                        durationBeats = chordBeats * (1.35f + padAmount.coerceIn(0f, 1f) * 0.65f),
                        midiNote = midi,
                        velocity = velocity,
                        lane = NoteLane.PAD
                    )
                }
        }
    }
}
