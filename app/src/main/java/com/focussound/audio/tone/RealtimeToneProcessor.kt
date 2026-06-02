package com.focussound.audio.tone

import com.focussound.audio.StereoFrame
import java.util.concurrent.atomic.AtomicReference

class RealtimeToneProcessor {
    private val state = AtomicReference(ToneControlState().clamped())
    private val left = ChannelToneProcessor()
    private val right = ChannelToneProcessor()

    fun updateTone(next: ToneControlState) {
        state.set(next.clamped())
    }

    fun process(frame: StereoFrame): StereoFrame {
        val current = state.get()
        return StereoFrame(
            left = left.process(frame.left, current),
            right = right.process(frame.right, current)
        )
    }
}

private class ChannelToneProcessor {
    private val low150 = OnePoleLowPass(0.03f)
    private val low500 = OnePoleLowPass(0.11f)
    private val low2000 = OnePoleLowPass(0.22f)
    private val low6000 = OnePoleLowPass(0.5f)
    private val softLowPass = OnePoleLowPass(0.22f)

    fun process(sample: Float, state: ToneControlState): Float {
        val brightness = state.brightness.coerceIn(0f, 1f)
        val warmth = state.warmth.coerceIn(0f, 1f)
        val coldness = state.coldness.coerceIn(0f, 1f)

        low150.setAlpha(0.018f)
        low500.setAlpha(0.09f)
        low2000.setAlpha(0.22f)
        low6000.setAlpha(0.52f)
        softLowPass.setAlpha(0.08f + brightness * 0.46f)

        val low = low150.process(sample)
        val low500Value = low500.process(sample)
        val low2000Value = low2000.process(sample)
        val low6000Value = low6000.process(sample)
        val lowMid = low500Value - low
        val upperMid = low6000Value - low2000Value
        val high = sample - low6000Value

        var out = sample
        out += lowMid * ((warmth - 0.5f) * 0.55f)
        out += upperMid * (coldness * 0.38f)
        out -= lowMid * (coldness * 0.22f)
        out += high * ((brightness - 0.5f) * 0.75f)

        if (brightness < 0.36f) {
            val filtered = softLowPass.process(out)
            val blend = ((0.36f - brightness) / 0.36f).coerceIn(0f, 1f)
            out = out * (1f - blend) + filtered * blend
        }

        return out.coerceIn(-1f, 1f)
    }
}
