package com.focussound.composition

import com.focussound.data.FocusMode

class RhythmGenerator {
    fun generate(intent: CompositionIntent): List<NoteEvent> {
        val density = intent.rhythmDensityHint ?: 0.12f
        if (
            density < 0.08f ||
            intent.genre == CompositionGenre.SLEEP_DRONE ||
            intent.mode == FocusMode.SLEEP ||
            intent.mode == FocusMode.READING ||
            (intent.mode == FocusMode.STUDY && intent.genre != CompositionGenre.LOFI)
        ) {
            return emptyList()
        }
        val events = mutableListOf<NoteEvent>()
        val kickVelocity = (0.12f + density * 0.18f).coerceIn(0.1f, 0.28f)
        for (bar in 0 until 4) {
            val offset = bar * 4f
            events += NoteEvent(offset, 0.12f, 36, kickVelocity, NoteLane.RHYTHM)
            if (density > 0.25f) {
                events += NoteEvent(offset + 2f, 0.1f, 38, kickVelocity * 0.65f, NoteLane.RHYTHM)
            }
            if (density > 0.32f) {
                events += NoteEvent(offset + 1.5f, 0.08f, 42, kickVelocity * 0.42f, NoteLane.RHYTHM)
                events += NoteEvent(offset + 3.5f, 0.08f, 42, kickVelocity * 0.38f, NoteLane.RHYTHM)
            }
        }
        return events
    }
}
