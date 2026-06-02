package com.focussound.audio.soundfont

import com.focussound.composition.NoteEvent

class FluidSynthBridge : SoundFontEngine {
    override fun loadSoundFont(path: String): Boolean = false

    override fun noteOn(note: NoteEvent) = Unit

    override fun renderStereo(frames: Int): FloatArray = FloatArray(frames * 2)

    override fun release() = Unit

    companion object {
        const val TODO = "Android NDK FluidSynth 연동 후보. V1은 WAV 샘플러를 기본으로 사용합니다."
    }
}
