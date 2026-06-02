package com.focussound.music.generation

import com.focussound.composition.Chord
import com.focussound.composition.MusicalKey
import com.focussound.composition.NoteEvent
import com.focussound.music.arrangement.ArrangementEngine
import com.focussound.music.form.MusicSection
import com.focussound.music.knowledge.TaskMusicProfile
import com.focussound.music.melody.Motif
import com.focussound.music.model.LiveCompositionRequest
import kotlin.random.Random

class ArrangementSampler(
    private val arrangementEngine: ArrangementEngine = ArrangementEngine(),
    private val variationEngine: VariationEngine = VariationEngine()
) {
    fun sample(
        request: LiveCompositionRequest,
        profile: TaskMusicProfile,
        key: MusicalKey,
        section: MusicSection,
        sectionIndex: Int,
        chords: List<Chord>,
        motif: Motif,
        startBeat: Float,
        temperature: GenerationTemperature,
        random: Random
    ): List<NoteEvent> {
        val arranged = arrangementEngine.arrange(
            request = request,
            key = key,
            section = section,
            chords = chords,
            motif = motif,
            startBeat = startBeat,
            random = random
        )
        return variationEngine.varyNotes(arranged, profile, temperature, sectionIndex, random)
    }
}
