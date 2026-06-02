package com.focussound.ui.ai

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PromptSuggestionChip(
    text: String,
    onClick: () -> Unit
) {
    AssistChip(
        modifier = Modifier.padding(end = 8.dp),
        onClick = onClick,
        label = { Text(text) },
        shape = RoundedCornerShape(8.dp)
    )
}
