package com.focussound.audio.playback

import com.focussound.audio.FocusSoundEngine
import com.focussound.composition.CompositionPatch
import com.focussound.instrument.InstrumentSet
import com.focussound.playback.PlaybackMode
import com.focussound.sounddesign.SoundPatch

class LiveMusicPlaybackEngine(
    private val engine: FocusSoundEngine
) {
    fun start(patch: SoundPatch, composition: CompositionPatch, instrumentSet: InstrumentSet?) {
        engine.start(
            patch = patch,
            compositionPatch = composition,
            instrumentSet = instrumentSet,
            playbackMode = if (patch.noiseLayerAmount > 0f || patch.rainLayerAmount > 0f) {
                PlaybackMode.AI_COMPOSITION_WITH_TEXTURE
            } else {
                PlaybackMode.AI_COMPOSITION_ONLY
            }
        )
    }

    fun stop() {
        engine.stop()
    }
}
