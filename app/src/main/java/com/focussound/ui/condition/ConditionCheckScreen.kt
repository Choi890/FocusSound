package com.focussound.ui.condition

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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focussound.condition.FatigueLevel
import com.focussound.condition.MoodLevel
import com.focussound.condition.TimeOfDay
import com.focussound.ui.FocusSoundUiState
import kotlin.math.roundToInt

@Composable
fun ConditionCheckScreen(
    state: FocusSoundUiState,
    onBack: () -> Unit,
    onSave: (Int?, FatigueLevel, MoodLevel, TimeOfDay) -> Unit
) {
    var sleepMinutes by remember { mutableFloatStateOf((state.condition.sleepMinutes ?: 420).toFloat()) }
    var fatigue by remember { mutableStateOf(state.condition.selfReportedFatigue) }
    var mood by remember { mutableStateOf(state.condition.selfReportedMood) }
    var timeOfDay by remember { mutableStateOf(state.condition.timeOfDay) }

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
            text = "오늘 컨디션",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "수동 입력을 기준으로 추천 밝기와 세션 길이를 조정합니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text("수면 시간 ${sleepMinutes.roundToInt() / 60}시간 ${sleepMinutes.roundToInt() % 60}분")
        Slider(
            value = sleepMinutes,
            onValueChange = { sleepMinutes = it },
            valueRange = 180f..600f,
            steps = 13
        )

        SegmentedEnumRow("피로감", FatigueLevel.entries, fatigue) { fatigue = it }
        SegmentedEnumRow("기분", MoodLevel.entries, mood) { mood = it }
        SegmentedEnumRow("시간대", TimeOfDay.entries, timeOfDay) { timeOfDay = it }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onSave(sleepMinutes.roundToInt(), fatigue, mood, timeOfDay) },
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("컨디션 저장")
        }
    }
}

@Composable
private fun <T> SegmentedEnumRow(
    title: String,
    values: List<T>,
    selected: T,
    onSelected: (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            values.forEach { value ->
                val text = value.toString()
                if (value == selected) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { onSelected(value) },
                        shape = RoundedCornerShape(8.dp)
                    ) { Text(text, maxLines = 1) }
                } else {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { onSelected(value) },
                        shape = RoundedCornerShape(8.dp)
                    ) { Text(text, maxLines = 1) }
                }
            }
        }
    }
}
