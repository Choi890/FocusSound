package com.focussound.ui.setup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focussound.instrument.InstrumentPreset
import com.focussound.ui.FocusSoundUiState
import com.focussound.ui.RecommendedInstrumentSet

@Composable
fun InstrumentSelectionScreen(
    state: FocusSoundUiState,
    onBack: () -> Unit,
    onToggleInstrument: (InstrumentPreset) -> Unit,
    onRecommendedSet: (RecommendedInstrumentSet) -> Unit,
    onNext: () -> Unit
) {
    val selectedIds = state.compositionSetup.selectedInstruments.map { it.id }.toSet()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(onClick = onBack, shape = RoundedCornerShape(8.dp)) {
            Text("뒤로")
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("작곡에 사용할 악기를 선택하세요", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("먼저 악기를 고르면 작곡 엔진이 각 악기에 맞게 멜로디, 패드, 베이스 역할을 나눕니다.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            RecommendedInstrumentSet.entries.forEach { item ->
                AssistChip(onClick = { onRecommendedSet(item) }, label = { Text(item.label) })
            }
        }

        if (state.compositionSetup.selectedInstruments.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("선택된 악기", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(
                        state.compositionSetup.selectedInstruments.joinToString(" · ") { it.name },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        state.instrumentPresets.forEach { preset ->
            SelectableInstrumentCard(
                preset = preset,
                selected = preset.id in selectedIds,
                onClick = { onToggleInstrument(preset) }
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onNext,
            enabled = state.compositionSetup.selectedInstruments.isNotEmpty(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("다음")
        }
    }
}

@Composable
private fun SelectableInstrumentCard(
    preset: InstrumentPreset,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(preset.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(if (selected) "선택됨" else "선택", color = MaterialTheme.colorScheme.primary)
            }
            Text(
                text = "${preset.category.label()} · ${preset.sourceType.label()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "밝기 ${(preset.brightness * 100).toInt()}% · 따뜻함 ${(preset.warmth * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun com.focussound.instrument.InstrumentCategory.label(): String = when (this) {
    com.focussound.instrument.InstrumentCategory.PIANO -> "피아노"
    com.focussound.instrument.InstrumentCategory.STRINGS -> "현악"
    com.focussound.instrument.InstrumentCategory.BASS -> "베이스"
    com.focussound.instrument.InstrumentCategory.PAD -> "패드"
    com.focussound.instrument.InstrumentCategory.WOODWIND -> "목관"
    com.focussound.instrument.InstrumentCategory.BRASS -> "금관"
    com.focussound.instrument.InstrumentCategory.PERCUSSION -> "퍼커션"
}

private fun com.focussound.instrument.InstrumentSourceType.label(): String = when (this) {
    com.focussound.instrument.InstrumentSourceType.BUILT_IN_WAV -> "내장 WAV"
    com.focussound.instrument.InstrumentSourceType.USER_IMPORTED_WAV -> "가져온 WAV"
    com.focussound.instrument.InstrumentSourceType.USER_IMPORTED_SF2 -> "가져온 SF2"
    com.focussound.instrument.InstrumentSourceType.SYNTH_FALLBACK -> "내장 신스"
}
