package com.focussound.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FocusDarkScheme = darkColorScheme(
    primary = Color(0xFF75E6DA),
    onPrimary = Color(0xFF06201E),
    secondary = Color(0xFFF7C873),
    onSecondary = Color(0xFF231A05),
    tertiary = Color(0xFFA7C7E7),
    background = Color(0xFF0E1117),
    onBackground = Color(0xFFE7EDF4),
    surface = Color(0xFF151A22),
    onSurface = Color(0xFFE7EDF4),
    surfaceVariant = Color(0xFF202733),
    onSurfaceVariant = Color(0xFFC2CBD6),
    outline = Color(0xFF526070)
)

@Composable
fun FocusSoundTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FocusDarkScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
