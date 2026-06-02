package com.focussound.ui.setup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import com.focussound.composition.CompositionMood
import com.focussound.composition.CompositionStyle
import com.focussound.composition.FocusIntensity
import com.focussound.ui.FocusSoundUiState

@Composable
fun MoodStyleSelectionScreen(
    state: FocusSoundUiState,
    onBack: () -> Unit,
    onMoodSelected: (CompositionMood) -> Unit,
    onStyleSelected: (CompositionStyle) -> Unit,
    onIntensitySelected: (FocusIntensity) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        OutlinedButton(onClick = onBack, shape = RoundedCornerShape(8.dp)) {
            Text("뒤로")
        }
        Text("분위기와 스타일을 선택하세요", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        ChipSection("분위기") {
            CompositionMood.entries.forEach { mood ->
                SelectChip(mood.label, state.compositionSetup.mood == mood) { onMoodSelected(mood) }
            }
        }
        ChipSection("스타일") {
            CompositionStyle.entries.forEach { style ->
                SelectChip(style.label, state.compositionSetup.style == style) { onStyleSelected(style) }
            }
        }
        ChipSection("집중 강도") {
            FocusIntensity.entries.forEach { intensity ->
                SelectChip(intensity.label, state.compositionSetup.focusIntensity == intensity) { onIntensitySelected(intensity) }
            }
        }

        Button(modifier = Modifier.fillMaxWidth(), onClick = onNext, shape = RoundedCornerShape(8.dp)) {
            Text("다음")
        }
    }
}

@Composable
private fun ChipSection(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun SelectChip(label: String, selected: Boolean, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(if (selected) "$label 선택됨" else label) }
    )
}
