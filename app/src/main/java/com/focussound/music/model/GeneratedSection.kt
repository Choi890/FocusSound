package com.focussound.music.model

import com.focussound.composition.Chord
import com.focussound.composition.NoteEvent

data class GeneratedSection(
    val section: com.focussound.music.form.MusicSection,
    val startBar: Int,
    val chords: List<Chord>,
    val notes: List<NoteEvent>
)

data class AutomationEvent(
    val beat: Float,
    val target: String,
    val value: Float
)

typealias SoundType = com.focussound.data.SoundType
