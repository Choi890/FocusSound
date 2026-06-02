package com.focussound.ui.composer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun ComposerAdjustmentChips(
    onMakeWarmer: () -> Unit,
    onReduceMelody: () -> Unit,
    onReduceRhythm: () -> Unit,
    onMakeDarker: () -> Unit,
    onIncreasePad: () -> Unit,
    onSleep: () -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(onClick = onMakeWarmer, label = { Text("더 따뜻하게") })
        AssistChip(onClick = onReduceMelody, label = { Text("멜로디 줄이기") })
        AssistChip(onClick = onReduceRhythm, label = { Text("리듬 줄이기") })
        AssistChip(onClick = onMakeDarker, label = { Text("더 어둡게") })
        AssistChip(onClick = onIncreasePad, label = { Text("패드 늘리기") })
        AssistChip(onClick = onSleep, label = { Text("수면용으로 바꾸기") })
    }
}
