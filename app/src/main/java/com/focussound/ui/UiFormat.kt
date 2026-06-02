package com.focussound.ui

internal fun Float.asPercent(): String = "${(coerceIn(0f, 1f) * 100).toInt()}%"

internal fun formatSeconds(seconds: Int): String {
    val safeSeconds = seconds.coerceAtLeast(0)
    val minutes = safeSeconds / 60
    val remainder = safeSeconds % 60
    return "%02d:%02d".format(minutes, remainder)
}

internal fun formatRating(value: Float): String = if (value == 0f) {
    "-"
} else {
    "%.1f".format(value)
}
