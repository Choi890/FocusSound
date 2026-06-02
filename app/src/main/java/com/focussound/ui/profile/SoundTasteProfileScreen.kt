package com.focussound.ui.profile

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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focussound.data.FocusMode
import com.focussound.personalization.UserSoundTasteVector
import com.focussound.ui.FocusSoundUiState
import com.focussound.ui.asPercent

@Composable
fun SoundTasteProfileScreen(
    state: FocusSoundUiState,
    onBack: () -> Unit
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
        Text(
            text = "집중 사운드 취향",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "세션 피드백이 쌓이면 모드별 취향 벡터가 업데이트됩니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        FocusMode.entries.forEach { mode ->
            val vector = state.tasteVectors.firstOrNull { it.mode == mode }
            TasteVectorBlock(mode.label, vector)
        }
    }
}

@Composable
private fun TasteVectorBlock(title: String, vector: UserSoundTasteVector?) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        if (vector == null) {
            Text(
                text = "아직 충분한 기록이 없습니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return
        }
        MetricRow("밝기", vector.preferredBrightness)
        MetricRow("따뜻함", vector.preferredWarmth)
        MetricRow("움직임", vector.preferredMovement)
        MetricRow("고역 완화", vector.preferredHighCut)
        MetricRow("공간감", vector.preferredStereoWidth)
        Text(
            text = "선호 세션 ${vector.preferredSessionMinutes}분 · 신뢰도 ${vector.confidence.asPercent()}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MetricRow(label: String, value: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text(value.asPercent(), style = MaterialTheme.typography.bodySmall)
        }
        LinearProgressIndicator(
            progress = { value.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
