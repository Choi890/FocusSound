package com.focussound.audio.synth

import com.focussound.audio.StereoFrame

object Mixer {
    fun add(base: StereoFrame, layer: StereoFrame, layerGain: Float): StereoFrame {
        return StereoFrame(
            left = base.left + layer.left * layerGain,
            right = base.right + layer.right * layerGain
        )
    }
}
