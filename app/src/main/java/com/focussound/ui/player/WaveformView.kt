package com.focussound.ui.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun WaveformView(
    samples: FloatArray,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF75E6DA)
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(132.dp)
    ) {
        val centerY = size.height / 2f
        val widthStep = if (samples.size > 1) size.width / (samples.size - 1) else size.width
        val path = Path()

        samples.forEachIndexed { index, sample ->
            val x = index * widthStep
            val y = centerY - sample.coerceIn(-1f, 1f) * centerY * 0.78f
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawLine(
            color = Color.White.copy(alpha = 0.08f),
            start = Offset(0f, centerY),
            end = Offset(size.width, centerY),
            strokeWidth = 1.dp.toPx()
        )
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
