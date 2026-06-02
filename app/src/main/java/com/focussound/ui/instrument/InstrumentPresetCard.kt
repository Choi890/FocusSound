package com.focussound.ui.instrument

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
import com.focussound.instrument.InstrumentPreset
import com.focussound.instrument.InstrumentRole
import com.focussound.instrument.InstrumentSourceType

@Composable
fun InstrumentPresetCard(
    preset: InstrumentPreset,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = preset.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = preset.role.label(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "${preset.sourceType.label()} · 샘플 영역 ${preset.sampleZones.size}개",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "출처: ${preset.license.sourceName} · ${preset.license.licenseName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (preset.sourcePath != null) {
                Text(
                    text = preset.sourcePath,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun InstrumentRole.label(): String = when (this) {
    InstrumentRole.MELODY -> "멜로디"
    InstrumentRole.PAD -> "패드"
    InstrumentRole.BASS -> "베이스"
    InstrumentRole.RHYTHM -> "리듬"
}

private fun InstrumentSourceType.label(): String = when (this) {
    InstrumentSourceType.BUILT_IN_WAV -> "내장 WAV"
    InstrumentSourceType.USER_IMPORTED_WAV -> "가져온 WAV"
    InstrumentSourceType.USER_IMPORTED_SF2 -> "가져온 SF2"
    InstrumentSourceType.SYNTH_FALLBACK -> "내장 신스"
}
