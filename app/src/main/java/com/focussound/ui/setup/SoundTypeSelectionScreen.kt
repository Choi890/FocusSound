package com.focussound.ui.setup

import androidx.compose.foundation.BorderStroke
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
import com.focussound.data.SoundType
import com.focussound.ui.FocusSoundUiState

@Composable
fun SoundTypeSelectionScreen(
    state: FocusSoundUiState,
    onBack: () -> Unit,
    onSoundTypeSelected: (SoundType) -> Unit,
    onGenerate: () -> Unit
) {
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
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("사운드 타입을 선택하세요", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("기본값은 선택하지 않음입니다. 이 경우 노이즈 없이 실제 악기 샘플만 재생합니다.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        SoundType.entries.forEach { soundType ->
            SoundTypeCard(
                soundType = soundType,
                selected = state.compositionSetup.soundType == soundType,
                onClick = { onSoundTypeSelected(soundType) }
            )
        }

        Button(modifier = Modifier.fillMaxWidth(), onClick = onGenerate, shape = RoundedCornerShape(8.dp)) {
            Text("작곡 생성")
        }
    }
}

@Composable
private fun SoundTypeCard(
    soundType: SoundType,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(soundType.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(soundType.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
