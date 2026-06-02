package com.focussound.ui.composer

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ExportCompositionDialog(
    message: String?,
    onDismiss: () -> Unit,
    onExportWav: () -> Unit,
    onExportMidi: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("로컬 내보내기") },
        text = {
            Text(message ?: "앱 전용 로컬 저장소에 WAV 또는 MIDI 파일로 저장합니다.")
        },
        confirmButton = {
            TextButton(onClick = onExportWav) {
                Text("WAV")
            }
        },
        dismissButton = {
            TextButton(onClick = onExportMidi) {
                Text("MIDI")
            }
            TextButton(onClick = onDismiss) {
                Text("닫기")
            }
        }
    )
}
