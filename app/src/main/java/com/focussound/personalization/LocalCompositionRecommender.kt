package com.focussound.personalization

import com.focussound.ai.FatigueTarget
import com.focussound.composition.CompositionIntent
import com.focussound.condition.FatigueLevel
import com.focussound.condition.UserCondition

class LocalCompositionRecommender {
    fun applyTasteAndCondition(
        intent: CompositionIntent,
        taste: UserSoundTasteVector?,
        condition: UserCondition
    ): CompositionIntent {
        val tired = condition.sleepDebt || condition.selfReportedFatigue == FatigueLevel.HIGH
        val tempoRange = taste?.preferredTempoRange
        val baseTempo = intent.tempoHintBpm ?: tempoRange?.let { (it.first + it.last) / 2 }
        return intent.copy(
            genre = taste?.preferredGenre ?: intent.genre,
            tempoHintBpm = if (tired) {
                (baseTempo ?: 64).coerceAtMost(64)
            } else {
                baseTempo
            },
            melodyDensityHint = if (tired) {
                (intent.melodyDensityHint ?: taste?.preferredMelodyDensity ?: 0.18f) * 0.62f
            } else {
                intent.melodyDensityHint ?: taste?.preferredMelodyDensity
            },
            rhythmDensityHint = if (tired) {
                (intent.rhythmDensityHint ?: taste?.preferredRhythmDensity ?: 0.08f) * 0.5f
            } else {
                intent.rhythmDensityHint ?: taste?.preferredRhythmDensity
            },
            harmonicComplexityHint = intent.harmonicComplexityHint ?: taste?.preferredHarmonicComplexity,
            fatigueTarget = if (tired) FatigueTarget.VERY_LOW else intent.fatigueTarget
        )
    }
}
