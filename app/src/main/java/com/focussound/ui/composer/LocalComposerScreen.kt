package com.focussound.ui.composer

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focussound.ui.FocusSoundUiState

@Composable
fun LocalComposerScreen(
    state: FocusSoundUiState,
    onBack: () -> Unit,
    onPromptChanged: (String) -> Unit,
    onGenerate: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onSave: () -> Unit,
    onExport: () -> Unit,
    onExportWav: () -> Unit,
    onExportMidi: () -> Unit,
    onDismissExport: () -> Unit,
    onMakeWarmer: () -> Unit,
    onReduceMelody: () -> Unit,
    onReduceRhythm: () -> Unit,
    onMakeDarker: () -> Unit,
    onIncreasePad: () -> Unit,
    onSleep: () -> Unit
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "로컬 작곡",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "인터넷 없이 작동합니다. 코드, 멜로디, 베이스, 패드, 리듬을 앱 내부에서 생성하고 재생합니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.compositionPrompt,
            onValueChange = onPromptChanged,
            minLines = 3,
            label = { Text("작곡 요청") },
            placeholder = { Text("예: 새벽 코딩용, 따뜻한 패드, 멜로디 적게") }
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onGenerate,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("로컬 작곡 생성")
        }

        state.generatedComposition?.let { composition ->
            CompositionResultCard(patch = composition)
            ComposerAdjustmentChips(
                onMakeWarmer = onMakeWarmer,
                onReduceMelody = onReduceMelody,
                onReduceRhythm = onReduceRhythm,
                onMakeDarker = onMakeDarker,
                onIncreasePad = onIncreasePad,
                onSleep = onSleep
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onStart,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("재생")
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onStop,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("정지")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onSave,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("저장")
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onExport,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("내보내기")
                }
            }
            state.exportMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } ?: EmptyLocalComposerCard()

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
private fun EmptyLocalComposerCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "생성 대기",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "외부 API, 서버, 로그인 없이 로컬 Room DB와 내부 신스만 사용합니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
