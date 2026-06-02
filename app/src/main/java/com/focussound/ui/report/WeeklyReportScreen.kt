package com.focussound.ui.report

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focussound.ui.FocusSoundUiState
import com.focussound.ui.formatRating

@Composable
fun WeeklyReportScreen(
    state: FocusSoundUiState,
    onBack: () -> Unit
) {
    val report = state.weeklyReport

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
                text = "이번 주 리포트",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "이번 주 세션 기록을 기준으로 요약합니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        ReportCard(
            title = "총 집중 시간",
            value = "${report.totalFocusMinutes}분",
            supportingText = "${report.sessionCount}개 세션"
        )

        ReportCard(
            title = "가장 잘 맞은 사운드",
            value = report.bestSoundLabel,
            supportingText = "집중도와 체감 피로도 기록 기준"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ReportCard(
                modifier = Modifier.weight(1f),
                title = "평균 집중도",
                value = formatRating(report.averageFocus),
                supportingText = "5점 기준"
            )
            ReportCard(
                modifier = Modifier.weight(1f),
                title = "평균 피로도",
                value = formatRating(report.averageFatigue),
                supportingText = "5점 기준"
            )
        }
    }
}
