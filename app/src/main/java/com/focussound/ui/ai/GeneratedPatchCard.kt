package com.focussound.ui.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focussound.sounddesign.NoiseType
import com.focussound.sounddesign.SoundPatch
import com.focussound.ui.asPercent

@Composable
fun GeneratedPatchCard(
    patch: SoundPatch,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = patch.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${patch.mode.label} · ${patch.baseNoiseType.label()} · ${patch.durationMinutes}분",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("밝기 ${patch.brightness.asPercent()}", style = MaterialTheme.typography.bodySmall)
                Text("따뜻함 ${patch.warmth.asPercent()}", style = MaterialTheme.typography.bodySmall)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("움직임 ${patch.movement.asPercent()}", style = MaterialTheme.typography.bodySmall)
                Text("비 질감 ${patch.rainLayerAmount.asPercent()}", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "청취 피로도 목표 ${patch.targetFatigueScore} / 100",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun NoiseType.label(): String = when (this) {
    NoiseType.WHITE -> "화이트 노이즈"
    NoiseType.PINK -> "핑크 노이즈"
    NoiseType.BROWN -> "브라운 노이즈"
}
