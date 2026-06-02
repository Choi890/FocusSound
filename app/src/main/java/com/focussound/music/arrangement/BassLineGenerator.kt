package com.focussound.music.arrangement

import com.focussound.composition.Chord
import com.focussound.composition.NoteEvent
import com.focussound.composition.NoteLane
import com.focussound.music.form.MusicSection
import com.focussound.music.model.LiveCompositionRequest
import com.focussound.music.model.MusicTask

class BassLineGenerator {
    fun generate(
        request: LiveCompositionRequest,
        section: MusicSection,
        chords: List<Chord>,
        startBeat: Float
    ): List<NoteEvent> {
        if (request.task == MusicTask.SLEEP || request.task == MusicTask.READING) return emptyList()
        return chords.flatMapIndexed { bar, chord ->
            val root = (24 + chord.root.semitone).coerceIn(24, 48)
            val barStart = startBeat + bar * 4f
            if (request.task == MusicTask.WORKOUT || request.rhythmAmount > 0.45f) {
                listOf(0f, 1.5f, 2f, 3f).map { offset ->
                    NoteEvent(barStart + offset, 0.95f, root, (0.28f + section.energy * 0.28f).coerceIn(0.24f, 0.62f), NoteLane.BASS)
                }
            } else {
                listOf(NoteEvent(barStart, 3.8f, root, (0.24f + section.energy * 0.2f).coerceIn(0.2f, 0.44f), NoteLane.BASS))
            }
        }
    }
}
