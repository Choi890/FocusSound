package com.focussound.ui.instrument

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focussound.instrument.InstrumentSourceType
import com.focussound.ui.FocusSoundUiState

@Composable
fun InstrumentPackScreen(
    state: FocusSoundUiState,
    onBack: () -> Unit,
    onImportWav: (Uri) -> Unit,
    onImportSf2: (Uri) -> Unit
) {
    val wavPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) onImportWav(uri)
    }
    val sf2Picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) onImportSf2(uri)
    }
    val builtInPresets = state.instrumentPresets.filter {
        it.sourceType == InstrumentSourceType.BUILT_IN_WAV ||
            it.sourceType == InstrumentSourceType.SYNTH_FALLBACK
    }
    val importedPresets = state.instrumentPresets.filter {
        it.sourceType == InstrumentSourceType.USER_IMPORTED_WAV ||
            it.sourceType == InstrumentSourceType.USER_IMPORTED_SF2
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "악기 팩",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "내장 신스, 가져온 WAV 샘플, SF2 정보는 모두 기기 안에만 보관됩니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        InstrumentImportScreen(
            onPickWav = { wavPicker.launch(arrayOf("audio/wav", "audio/x-wav", "audio/wave", "audio/*")) },
            onPickSf2 = { sf2Picker.launch(arrayOf("application/octet-stream", "*/*")) }
        )

        state.instrumentMessage?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        SampleMappingScreen()

        InstrumentSection(
            title = "내장 악기",
            emptyText = "내장 악기를 찾지 못했습니다.",
            presets = builtInPresets
        )
        InstrumentSection(
            title = "가져온 악기",
            emptyText = "WAV 또는 SF2 파일을 가져오면 여기에 표시됩니다.",
            presets = importedPresets
        )

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onBack,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("돌아가기")
        }
    }
}

@Composable
private fun InstrumentSection(
    title: String,
    emptyText: String,
    presets: List<com.focussound.instrument.InstrumentPreset>
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        if (presets.isEmpty()) {
            Text(
                text = emptyText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            presets.forEach { preset ->
                InstrumentPresetCard(preset = preset)
            }
        }
    }
}
