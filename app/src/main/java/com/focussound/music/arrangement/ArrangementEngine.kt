package com.focussound.music.arrangement

import com.focussound.composition.Chord
import com.focussound.composition.MusicalKey
import com.focussound.composition.NoteEvent
import com.focussound.music.form.MusicSection
import com.focussound.music.melody.CounterMelodyGenerator
import com.focussound.music.melody.MelodyGenerator
import com.focussound.music.melody.Motif
import com.focussound.music.model.LiveCompositionRequest
import kotlin.random.Random

class ArrangementEngine(
    private val piano: PianoPatternGenerator = PianoPatternGenerator(),
    private val strings: StringsArranger = StringsArranger(),
    private val bass: BassLineGenerator = BassLineGenerator(),
    private val pad: PadArranger = PadArranger(),
    private val rhythm: RhythmArranger = RhythmArranger(),
    private val melody: MelodyGenerator = MelodyGenerator(),
    private val counter: CounterMelodyGenerator = CounterMelodyGenerator()
) {
    fun arrange(
        request: LiveCompositionRequest,
        key: MusicalKey,
        section: MusicSection,
        chords: List<Chord>,
        motif: Motif,
        startBeat: Float,
        random: Random
    ): List<NoteEvent> {
        return buildList {
            addAll(pad.generate(request, key, section, chords, startBeat, random))
            addAll(strings.generate(request, key, section, chords, startBeat, random))
            addAll(piano.generate(request, key, section, chords, startBeat, random))
            addAll(bass.generate(request, section, chords, startBeat))
            addAll(melody.generate(request, key, section, chords, motif, startBeat, random))
            addAll(counter.generate(request, key, section, chords, startBeat))
            addAll(rhythm.generate(request, section, startBeat))
        }
    }
}
