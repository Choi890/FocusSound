package com.focussound.music.realtime

import com.focussound.composition.Chord
import com.focussound.composition.NoteEvent
import com.focussound.music.arrangement.ArrangementEngine
import com.focussound.music.form.MusicSection
import com.focussound.music.model.LiveCompositionRequest
import kotlin.random.Random

class NextBarGenerator(
    private val arrangementEngine: ArrangementEngine = ArrangementEngine()
) {
    fun generateSection(
        request: LiveCompositionRequest,
        memory: MusicMemory,
        section: MusicSection,
        chords: List<Chord>,
        startBeat: Float,
        random: Random
    ): List<NoteEvent> {
        return arrangementEngine.arrange(
            request = request,
            key = memory.key,
            section = section,
            chords = chords,
            motif = memory.motif,
            startBeat = startBeat,
            random = random
        )
    }
}
