package com.focussound.composition

import com.focussound.condition.FatigueLevel
import com.focussound.condition.UserCondition
import com.focussound.data.FocusMode
import com.focussound.personalization.UserSoundTasteVector

class CompositionRecommender {
    fun adjustIntent(
        intent: CompositionIntent,
        taste: UserSoundTasteVector?,
        condition: UserCondition
    ): CompositionIntent {
        val tempoRange = taste?.preferredTempoRange
        val tempo = intent.tempoHintBpm ?: tempoRange?.let { (it.first + it.last) / 2 }
        val fatigueReduction = condition.sleepDebt || condition.selfReportedFatigue == FatigueLevel.HIGH
        return intent.copy(
            tempoHintBpm = if (fatigueReduction) (tempo ?: 68).coerceAtMost(68) else tempo,
            melodyDensityHint = when {
                fatigueReduction -> (intent.melodyDensityHint ?: taste?.preferredMelodyDensity ?: 0.2f) * 0.72f
                taste != null -> intent.melodyDensityHint ?: taste.preferredMelodyDensity
                else -> intent.melodyDensityHint
            },
            rhythmDensityHint = when {
                fatigueReduction -> (intent.rhythmDensityHint ?: taste?.preferredRhythmDensity ?: 0.12f) * 0.6f
                taste != null -> intent.rhythmDensityHint ?: taste.preferredRhythmDensity
                else -> intent.rhythmDensityHint
            },
            harmonicComplexityHint = taste?.preferredHarmonicComplexity?.let {
                intent.harmonicComplexityHint ?: it
            } ?: intent.harmonicComplexityHint,
            genre = taste?.preferredGenre ?: intent.genre,
            mode = if (condition.sleepDebt && intent.mode == FocusMode.CODING) FocusMode.CODING else intent.mode
        )
    }
}
