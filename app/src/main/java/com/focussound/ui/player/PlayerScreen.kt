package com.focussound.ui.player

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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focussound.service.PlaybackStatus
import com.focussound.ui.FocusSoundUiState
import com.focussound.ui.TimerPreset
import com.focussound.ui.asPercent
import com.focussound.ui.formatSeconds
import kotlin.math.roundToInt

@Composable
fun PlayerScreen(
    state: FocusSoundUiState,
    onBack: () -> Unit,
    onPresetSelected: (TimerPreset) -> Unit,
    onCustomMinutesChanged: (Int) -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onToneBrightnessChanged: (Float) -> Unit,
    onToneWarmthChanged: (Float) -> Unit,
    onToneColdnessChanged: (Float) -> Unit,
    onSoundDesign: () -> Unit
) {
    val patch = state.activePatch
    val profile = patch.toSoundProfile()

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
            OutlinedButton(onClick = onSoundDesign, shape = RoundedCornerShape(8.dp)) {
                Text("설계")
            }
        }

        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = patch.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatSeconds(state.remainingSeconds),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = playbackLabel(state.playbackState.status, patch.durationMinutes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "밝기 ${profile.brightness.asPercent()} · 따뜻함 ${profile.warmth.asPercent()} · 움직임 ${profile.movement.asPercent()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = "실시간 파형",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                WaveformView(samples = state.waveformSamples)
            }
        }

        TimerPresetCard(
            state = state,
            onPresetSelected = onPresetSelected,
            onCustomMinutesChanged = onCustomMinutesChanged
        )

        FatigueScoreCard(score = state.fatigueScore)
        ToneControlPanel(
            brightness = state.toneBrightness,
            warmth = state.toneWarmth,
            coldness = state.toneColdness,
            onBrightnessChanged = onToneBrightnessChanged,
            onWarmthChanged = onToneWarmthChanged,
            onColdnessChanged = onToneColdnessChanged
        )
        SoundAnalysisCard(patch = patch)

        state.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall
            )
        }

        PlaybackControls(
            status = state.playbackState.status,
            onStart = onStart,
            onPause = onPause,
            onResume = onResume,
            onStop = onStop
        )
    }
}

@Composable
private fun TimerPresetCard(
    state: FocusSoundUiState,
    onPresetSelected: (TimerPreset) -> Unit,
    onCustomMinutesChanged: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "집중 타이머",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetButton(
                    modifier = Modifier.weight(1f),
                    text = "25분",
                    selected = state.timerPreset == TimerPreset.TWENTY_FIVE,
                    enabled = !state.isRunning,
                    onClick = { onPresetSelected(TimerPreset.TWENTY_FIVE) }
                )
                PresetButton(
                    modifier = Modifier.weight(1f),
                    text = "50분",
                    selected = state.timerPreset == TimerPreset.FIFTY,
                    enabled = !state.isRunning,
                    onClick = { onPresetSelected(TimerPreset.FIFTY) }
                )
                PresetButton(
                    modifier = Modifier.weight(1f),
                    text = "사용자",
                    selected = state.timerPreset == TimerPreset.CUSTOM,
                    enabled = !state.isRunning,
                    onClick = { onPresetSelected(TimerPreset.CUSTOM) }
                )
            }

            if (state.timerPreset == TimerPreset.CUSTOM) {
                Text(
                    text = "사용자 지정 ${state.durationMinutes}분",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = state.durationMinutes.toFloat(),
                    onValueChange = { onCustomMinutesChanged(it.roundToInt()) },
                    enabled = !state.isRunning,
                    valueRange = 5f..180f,
                    steps = 34
                )
            }
        }
    }
}

@Composable
private fun PresetButton(
    modifier: Modifier,
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    if (selected) {
        Button(
            modifier = modifier,
            enabled = enabled,
            onClick = onClick,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text)
        }
    } else {
        OutlinedButton(
            modifier = modifier,
            enabled = enabled,
            onClick = onClick,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text)
        }
    }
}

@Composable
private fun PlaybackControls(
    status: PlaybackStatus,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        when (status) {
            PlaybackStatus.STOPPED -> Button(
                modifier = Modifier.weight(1f),
                onClick = onStart,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("시작")
            }

            PlaybackStatus.PLAYING -> Button(
                modifier = Modifier.weight(1f),
                onClick = onPause,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("일시정지")
            }

            PlaybackStatus.PAUSED -> Button(
                modifier = Modifier.weight(1f),
                onClick = onResume,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("재생")
            }
        }

        OutlinedButton(
            modifier = Modifier.weight(1f),
            enabled = status != PlaybackStatus.STOPPED,
            onClick = onStop,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("정지")
        }
    }
}

private fun playbackLabel(status: PlaybackStatus, durationMinutes: Int): String = when (status) {
    PlaybackStatus.STOPPED -> "${durationMinutes}분 집중 세션 대기"
    PlaybackStatus.PLAYING -> "백그라운드 재생 중"
    PlaybackStatus.PAUSED -> "일시정지됨"
}
