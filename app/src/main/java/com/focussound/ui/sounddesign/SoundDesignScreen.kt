package com.focussound.ui.sounddesign

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focussound.ui.FocusSoundUiState
import com.focussound.ui.asPercent

@Composable
fun SoundDesignScreen(
    state: FocusSoundUiState,
    onBack: () -> Unit,
    onBrightnessChanged: (Float) -> Unit,
    onWarmthChanged: (Float) -> Unit,
    onMovementChanged: (Float) -> Unit,
    onSavePreset: (String) -> Unit,
    onPlayer: () -> Unit
) {
    val profile = state.preference.toSoundProfile()
    var showSaveDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onBack, shape = RoundedCornerShape(8.dp)) {
                Text("뒤로")
            }
            OutlinedButton(onClick = { showSaveDialog = true }, shape = RoundedCornerShape(8.dp)) {
                Text("프리셋 저장")
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "사운드 설계",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "현재 사운드의 질감을 조절하고 프리셋으로 저장합니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.28f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "현재 사운드",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = profile.displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "청취 피로도 예상 ${state.fatigueScore} / 100",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SoundSlider(
            title = "밝기",
            valueLabel = profile.brightness.asPercent(),
            supportingText = "높을수록 선명하고 밝은 질감이 커집니다.",
            value = profile.brightness,
            onValueChange = onBrightnessChanged
        )
        SoundSlider(
            title = "따뜻함",
            valueLabel = profile.warmth.asPercent(),
            supportingText = "높을수록 부드럽고 낮은 질감이 커집니다.",
            value = profile.warmth,
            onValueChange = onWarmthChanged
        )
        SoundSlider(
            title = "움직임",
            valueLabel = profile.movement.asPercent(),
            supportingText = "높을수록 미세한 변화가 느껴집니다.",
            value = profile.movement,
            onValueChange = onMovementChanged
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onPlayer,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("플레이어로 이동")
        }
    }

    if (showSaveDialog) {
        PresetSaveDialog(
            onDismiss = { showSaveDialog = false },
            onSave = onSavePreset
        )
    }
}
