package com.focussound.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focussound.data.SoundType
import com.focussound.instrument.InstrumentPreset
import com.focussound.instrument.InstrumentRole
import com.focussound.instrument.InstrumentSourceType
import com.focussound.music.model.MusicStyle
import com.focussound.music.model.MusicTask
import com.focussound.ui.FocusSoundUiState
import com.focussound.ui.formatSeconds

@Composable
fun HomeScreen(
    state: FocusSoundUiState,
    onTaskSelected: (MusicTask) -> Unit,
    onStyleSelected: (MusicStyle) -> Unit,
    onSoundTypeSelected: (SoundType) -> Unit,
    onInstrumentSelected: (InstrumentPreset) -> Unit,
    onBrightnessChanged: (Float) -> Unit,
    onWarmthChanged: (Float) -> Unit,
    onColdnessChanged: (Float) -> Unit,
    onMelodyAmountChanged: (Float) -> Unit,
    onRhythmAmountChanged: (Float) -> Unit,
    onDiversityChanged: (Float) -> Unit,
    onGenerateMusic: () -> Unit,
    onStop: () -> Unit
) {
    val profile = state.preference.toSoundProfile()
    val selectedInstruments = state.compositionSetup.selectedInstruments
    val canGenerate = !state.isRunning && state.instrumentPresets.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "포커스 작곡",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "작업과 스타일을 고르면 실제 악기 샘플로 완성형 음악을 계속 생성합니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (state.isRunning) {
            PlaybackStatusCard(
                title = state.selectedComposition?.name ?: "생성한 음악 재생 중",
                remainingSeconds = state.remainingSeconds,
                onStop = onStop
            )
        }

        ChoiceSection(
            title = "작업 선택",
            values = MusicTask.entries,
            selected = state.selectedMusicTask,
            onSelected = onTaskSelected,
            label = { it.label },
            description = { it.description }
        )

        ChoiceSection(
            title = "음악 스타일",
            values = MusicStyle.entries,
            selected = state.selectedMusicStyle,
            onSelected = onStyleSelected,
            label = { it.label },
            description = { it.styleDescription() }
        )

        ChoiceSection(
            title = "보조 사운드 타입",
            values = SoundType.entries,
            selected = profile.soundType,
            onSelected = onSoundTypeSelected,
            label = { it.label },
            description = { it.description }
        )

        InstrumentSection(
            instruments = state.instrumentPresets,
            selectedInstruments = selectedInstruments,
            onInstrumentSelected = onInstrumentSelected
        )

        LiveControlSection(
            state = state,
            onBrightnessChanged = onBrightnessChanged,
            onWarmthChanged = onWarmthChanged,
            onColdnessChanged = onColdnessChanged,
            onMelodyAmountChanged = onMelodyAmountChanged,
            onRhythmAmountChanged = onRhythmAmountChanged,
            onDiversityChanged = onDiversityChanged
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            onClick = onGenerateMusic,
            enabled = canGenerate,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(if (state.isRunning) "재생 중" else "음악 생성")
        }

        if (state.instrumentPresets.isEmpty()) {
            Text(
                text = "내장 악기를 준비하는 중입니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        state.errorMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun LiveControlSection(
    state: FocusSoundUiState,
    onBrightnessChanged: (Float) -> Unit,
    onWarmthChanged: (Float) -> Unit,
    onColdnessChanged: (Float) -> Unit,
    onMelodyAmountChanged: (Float) -> Unit,
    onRhythmAmountChanged: (Float) -> Unit,
    onDiversityChanged: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "재생 중 조정",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            ControlSlider("밝기", state.toneBrightness, onBrightnessChanged)
            ControlSlider("따뜻함", state.toneWarmth, onWarmthChanged)
            ControlSlider("차가움", state.toneColdness, onColdnessChanged)
            ControlSlider("멜로디 양", state.liveMelodyAmount, onMelodyAmountChanged)
            ControlSlider("리듬 양", state.liveRhythmAmount, onRhythmAmountChanged)
            ControlSlider("다양성", state.liveDiversity, onDiversityChanged)
        }
    }
}

@Composable
private fun ControlSlider(
    label: String,
    value: Float,
    onValueChanged: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Slider(
            value = value.coerceIn(0f, 1f),
            onValueChange = onValueChanged
        )
    }
}

@Composable
private fun PlaybackStatusCard(
    title: String,
    remainingSeconds: Int,
    onStop: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.36f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "남은 시간 ${formatSeconds(remainingSeconds)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onStop,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("정지")
            }
        }
    }
}

@Composable
private fun <T> ChoiceSection(
    title: String,
    values: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    label: (T) -> String,
    description: (T) -> String
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        values.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { item ->
                    SelectableCard(
                        modifier = Modifier.weight(1f),
                        selected = item == selected,
                        title = label(item),
                        description = description(item),
                        onClick = { onSelected(item) }
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun InstrumentSection(
    instruments: List<InstrumentPreset>,
    selectedInstruments: List<InstrumentPreset>,
    onInstrumentSelected: (InstrumentPreset) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "악기 선택",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "최대 4개까지 선택할 수 있습니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        instruments.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { instrument ->
                    val selected = selectedInstruments.any { it.id == instrument.id }
                    SelectableCard(
                        modifier = Modifier.weight(1f),
                        selected = selected,
                        title = instrument.name,
                        description = "${instrument.role.koreanLabel()} · ${instrument.sourceType.koreanLabel()}",
                        onClick = { onInstrumentSelected(instrument) }
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SelectableCard(
    modifier: Modifier,
    selected: Boolean,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun InstrumentRole.koreanLabel(): String = when (this) {
    InstrumentRole.MELODY -> "멜로디"
    InstrumentRole.PAD -> "패드"
    InstrumentRole.BASS -> "베이스"
    InstrumentRole.RHYTHM -> "리듬"
}

private fun InstrumentSourceType.koreanLabel(): String = when (this) {
    InstrumentSourceType.BUILT_IN_WAV -> "내장 샘플"
    InstrumentSourceType.USER_IMPORTED_WAV -> "가져온 WAV"
    InstrumentSourceType.USER_IMPORTED_SF2 -> "가져온 SF2"
    InstrumentSourceType.SYNTH_FALLBACK -> "내부 신스"
}

private fun MusicStyle.styleDescription(): String = when (this) {
    MusicStyle.RELAXING_PIANO -> "부드러운 피아노와 따뜻한 화성"
    MusicStyle.AMBIENT -> "공간감 있는 낮은 자극"
    MusicStyle.LOFI -> "약한 펄스와 반복감"
    MusicStyle.CLASSICAL_MINIMAL -> "단순한 모티프와 안정적인 진행"
    MusicStyle.ORCHESTRAL_PAD -> "현악 패드 중심의 긴 호흡"
    MusicStyle.SLEEP_DRONE -> "수면용 저자극 롱톤"
    MusicStyle.MINIMAL_ELECTRONIC -> "간결한 리듬과 베이스"
}
