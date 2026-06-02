package com.focussound.music.arrangement

import com.focussound.composition.Chord
import com.focussound.composition.MusicalKey
import com.focussound.composition.NoteEvent
import com.focussound.composition.NoteLane
import com.focussound.music.form.MusicSection
import com.focussound.music.harmony.VoicingGenerator
import com.focussound.music.model.LiveCompositionRequest
import com.focussound.music.model.MusicTask
import kotlin.random.Random

class PadArranger(
    private val voicingGenerator: VoicingGenerator = VoicingGenerator()
) {
    fun generate(
        request: LiveCompositionRequest,
        key: MusicalKey,
        section: MusicSection,
        chords: List<Chord>,
        startBeat: Float,
        random: Random
    ): List<NoteEvent> {
        if (request.task == MusicTask.WORKOUT) return emptyList()
        return chords.filterIndexed { index, _ -> index % 2 == 0 }.flatMapIndexed { index, chord ->
            voicingGenerator.padVoicing(key, chord, request.task, random).mapIndexed { voice, midi ->
                NoteEvent(
                    startBeat = startBeat + index * 8f + voice * 0.05f,
                    durationBeats = if (request.task == MusicTask.SLEEP) 13f else 10.5f,
                    midiNote = midi.coerceIn(40, 76),
                    velocity = (0.14f + section.energy * 0.15f).coerceIn(0.12f, 0.3f),
                    lane = NoteLane.PAD
                )
            }
        }
    }
}
