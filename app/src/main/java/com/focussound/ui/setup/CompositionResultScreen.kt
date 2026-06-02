package com.focussound.ui.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.focussound.ui.FocusSoundUiState
import com.focussound.ui.composer.CompositionResultCard
import com.focussound.ui.composer.ExportCompositionDialog

@Composable
fun CompositionResultScreen(
    state: FocusSoundUiState,
    onBack: () -> Unit,
    onPlay: () -> Unit,
    onRegenerate: () -> Unit,
    onChangeInstruments: () -> Unit,
    onChangeMood: () -> Unit,
    onExport: () -> Unit,
    onExportWav: () -> Unit,
    onExportMidi: () -> Unit,
    onDismissExport: () -> Unit
) {
    if (state.showExportDialog) {
        ExportCompositionDialog(
            message = state.exportMessage,
            onDismiss = onDismissExport,
            onExportWav = onExportWav,
            onExportMidi = onExportMidi
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedButton(onClick = onBack, shape = RoundedCornerShape(8.dp)) {
            Text("뒤로")
        }

        state.selectedComposition?.let { composition ->
            CompositionResultCard(patch = composition)
            Text("사운드 타입: ${state.compositionSetup.soundType.label}")
            Text("선택 악기: ${state.compositionSetup.selectedInstruments.joinToString(" · ") { it.name }}")
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(modifier = Modifier.weight(1f), onClick = onPlay, shape = RoundedCornerShape(8.dp)) {
                Text("재생")
            }
            OutlinedButton(modifier = Modifier.weight(1f), onClick = onRegenerate, shape = RoundedCornerShape(8.dp)) {
                Text("다시 작곡")
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(modifier = Modifier.weight(1f), onClick = onChangeInstruments, shape = RoundedCornerShape(8.dp)) {
                Text("악기 바꾸기")
            }
            OutlinedButton(modifier = Modifier.weight(1f), onClick = onChangeMood, shape = RoundedCornerShape(8.dp)) {
                Text("분위기 바꾸기")
            }
        }
        OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onExport, shape = RoundedCornerShape(8.dp)) {
            Text("WAV/MIDI 저장")
        }
    }
}
