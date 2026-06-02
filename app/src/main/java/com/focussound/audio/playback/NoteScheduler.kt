package com.focussound.audio.playback

import com.focussound.composition.NoteEvent

class NoteScheduler(
    private val notes: List<NoteEvent>
) {
    fun notesStartingBetween(startBeat: Float, endBeat: Float): List<NoteEvent> {
        return notes.filter { it.startBeat >= startBeat && it.startBeat < endBeat }
    }
}
