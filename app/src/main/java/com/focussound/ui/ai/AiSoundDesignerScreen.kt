package com.focussound.ui.ai

import androidx.compose.foundation.horizontalScroll
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
fun AiSoundDesignerScreen(
    state: FocusSoundUiState,
    onBack: () -> Unit,
    onPromptChanged: (String) -> Unit,
    onGenerate: () -> Unit,
    onStart: () -> Unit,
    onMakeWarmer: () -> Unit,
    onReduceRain: () -> Unit,
    onReduceMovement: () -> Unit
) {
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
            Text(
                text = "사운드 디자이너",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "원하는 집중 사운드를 자연어로 입력하면 재생 가능한 패치로 변환합니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.aiPrompt,
            onValueChange = onPromptChanged,
            minLines = 4,
            label = { Text("어떤 사운드로 만들까요?") },
            placeholder = { Text("비 오는 야간 코딩 사운드, 고역은 부드럽고 따뜻하게") }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            PromptSuggestionChip("비 오는 코딩") {
                onPromptChanged("비 오는 야간 코딩 사운드, 고역은 부드럽고 따뜻하게, 50분")
            }
            PromptSuggestionChip("도서관 독서") {
                onPromptChanged("도서관에서 독서하는 느낌, 움직임은 적고 포근하게, 30분")
            }
            PromptSuggestionChip("수면 전") {
                onPromptChanged("수면 전에 쓰기 좋은 어둡고 부드러운 브라운 노이즈, 45분")
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onGenerate,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("사운드 패치 생성")
        }

        state.generatedPatch?.let { patch ->
            GeneratedPatchCard(patch = patch)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onMakeWarmer,
                    shape = RoundedCornerShape(8.dp)
                ) { Text("더 따뜻하게") }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onReduceRain,
                    shape = RoundedCornerShape(8.dp)
                ) { Text("비 줄이기") }
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onReduceMovement,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("움직임 줄이기")
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onStart,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("이 사운드로 시작")
            }
        }
    }
}
