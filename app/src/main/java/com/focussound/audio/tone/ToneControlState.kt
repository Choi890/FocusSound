package com.focussound.audio.tone

data class ToneControlState(
    val brightness: Float = 0.5f,
    val warmth: Float = 0.5f,
    val coldness: Float = 0.2f
) {
    fun clamped(): ToneControlState = copy(
        brightness = brightness.coerceIn(0f, 1f),
        warmth = warmth.coerceIn(0f, 1f),
        coldness = coldness.coerceIn(0f, 1f)
    )
}
