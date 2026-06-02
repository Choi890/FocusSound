package com.focussound.composition

import com.focussound.ai.FatigueTarget
import com.focussound.data.FocusMode

data class CompositionIntent(
    val mode: FocusMode,
    val genre: CompositionGenre,
    val moodKeywords: List<String>,
    val fatigueTarget: FatigueTarget,
    val keyHint: MusicalKey?,
    val tempoHintBpm: Int?,
    val melodyDensityHint: Float?,
    val rhythmDensityHint: Float?,
    val harmonicComplexityHint: Float?,
    val padFocus: Boolean,
    val durationMinutes: Int?,
    val padAmountHint: Float? = null
)
