package com.focussound.composition

import com.focussound.ai.FatigueTarget
import com.focussound.data.FocusMode

class FatigueAwareArranger {
    fun arrange(intent: CompositionIntent, patch: CompositionPatch): CompositionPatch {
        val fatigueScore = estimateFatigue(intent, patch)
        if (fatigueScore <= patch.fatigueScore) return patch.copy(fatigueScore = fatigueScore)
        val reduction = when (intent.fatigueTarget) {
            FatigueTarget.VERY_LOW -> 0.55f
            FatigueTarget.LOW -> 0.72f
            FatigueTarget.NORMAL -> 0.9f
        }
        val filtered = patch.notes.filter { note ->
            when (note.lane) {
                NoteLane.MELODY -> intent.mode != FocusMode.SLEEP &&
                    note.midiNote <= 78 &&
                    (patch.melodyDensity < 0.18f || note.startBeat.toInt() % 4 == 0)
                NoteLane.RHYTHM -> note.durationBeats >= 0.08f &&
                    (patch.rhythmDensity < 0.16f || note.midiNote == 36)
                else -> true
            }
        }.map { note ->
            if (note.lane == NoteLane.MELODY || note.lane == NoteLane.RHYTHM || note.velocity > 0.42f) {
                note.copy(velocity = (note.velocity * reduction).coerceAtMost(0.46f))
            } else {
                note
            }
        }
        return patch.copy(
            notes = filtered,
            melodyDensity = patch.melodyDensity * reduction,
            rhythmDensity = patch.rhythmDensity * reduction,
            fatigueScore = estimateFatigue(intent, patch.copy(notes = filtered))
        )
    }

    fun estimateFatigue(intent: CompositionIntent, patch: CompositionPatch): Int {
        val targetOffset = when (intent.fatigueTarget) {
            FatigueTarget.VERY_LOW -> -16
            FatigueTarget.LOW -> -8
            FatigueTarget.NORMAL -> 0
        }
        val score = 22 +
            patch.tempoBpm * 0.08f +
            patch.melodyDensity * 28f +
            patch.rhythmDensity * 24f +
            patch.harmonicComplexity * 16f +
            targetOffset
        return score.toInt().coerceIn(5, 100)
    }
}
