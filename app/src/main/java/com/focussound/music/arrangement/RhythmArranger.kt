package com.focussound.music.arrangement

import com.focussound.composition.NoteEvent
import com.focussound.composition.NoteLane
import com.focussound.music.form.MusicSection
import com.focussound.music.model.LiveCompositionRequest
import com.focussound.music.model.MusicTask

class RhythmArranger {
    fun generate(request: LiveCompositionRequest, section: MusicSection, startBeat: Float): List<NoteEvent> {
        val rhythmAmount = when (request.task) {
            MusicTask.SLEEP, MusicTask.READING -> 0f
            MusicTask.STUDY, MusicTask.RELAX -> request.rhythmAmount * 0.25f
            MusicTask.CODING -> request.rhythmAmount * 0.8f
            MusicTask.WORKOUT -> request.rhythmAmount.coerceAtLeast(0.7f)
        }
        if (rhythmAmount < 0.08f) return emptyList()

        val notes = mutableListOf<NoteEvent>()
        val bars = section.bars
        repeat(bars) { bar ->
            val beat = startBeat + bar * 4f
            notes += NoteEvent(beat, 0.12f, 36, (0.12f + section.energy * 0.18f).coerceIn(0.1f, 0.38f), NoteLane.RHYTHM)
            if (rhythmAmount > 0.4f) {
                notes += NoteEvent(beat + 2f, 0.1f, 38, (0.08f + section.energy * 0.12f).coerceIn(0.08f, 0.28f), NoteLane.RHYTHM)
            }
            if (rhythmAmount > 0.58f) {
                listOf(1f, 1.5f, 3f, 3.5f).forEach { offset ->
                    notes += NoteEvent(beat + offset, 0.06f, 42, 0.08f + rhythmAmount * 0.12f, NoteLane.RHYTHM)
                }
            }
        }
        return notes
    }
}
