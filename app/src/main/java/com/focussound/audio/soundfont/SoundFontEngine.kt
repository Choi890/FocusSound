package com.focussound.audio.soundfont

import com.focussound.composition.NoteEvent

interface SoundFontEngine {
    fun loadSoundFont(path: String): Boolean
    fun noteOn(note: NoteEvent)
    fun renderStereo(frames: Int): FloatArray
    fun release()
}
