package com.focussound.music.form

import com.focussound.composition.NoteEvent
import com.focussound.composition.NoteLane

class SectionTransitionPlanner {
    fun transitionNotes(startBeat: Float, energy: Float): List<NoteEvent> {
        if (energy < 0.22f) return emptyList()
        return listOf(
            NoteEvent(
                startBeat = startBeat - 0.5f,
                durationBeats = 0.45f,
                midiNote = 72,
                velocity = (0.08f + energy * 0.08f).coerceIn(0.08f, 0.18f),
                lane = NoteLane.PAD
            )
        )
    }
}
