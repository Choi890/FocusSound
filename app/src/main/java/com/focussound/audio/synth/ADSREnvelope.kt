package com.focussound.audio.synth

class ADSREnvelope(
    private val attackSamples: Int,
    private val decaySamples: Int,
    private val sustainLevel: Float,
    private val releaseSamples: Int
) {
    fun gain(positionSamples: Int, noteDurationSamples: Int): Float {
        if (positionSamples < 0) return 0f
        return when {
            positionSamples < attackSamples -> positionSamples / attackSamples.coerceAtLeast(1).toFloat()
            positionSamples < attackSamples + decaySamples -> {
                val decayProgress = (positionSamples - attackSamples) / decaySamples.coerceAtLeast(1).toFloat()
                1f - (1f - sustainLevel) * decayProgress
            }
            positionSamples < noteDurationSamples -> sustainLevel
            positionSamples < noteDurationSamples + releaseSamples -> {
                val releaseProgress = (positionSamples - noteDurationSamples) / releaseSamples.coerceAtLeast(1).toFloat()
                sustainLevel * (1f - releaseProgress)
            }
            else -> 0f
        }.coerceIn(0f, 1f)
    }

    val tailSamples: Int
        get() = releaseSamples
}
