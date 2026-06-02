package com.focussound.personalization

import com.focussound.condition.FatigueLevel
import com.focussound.condition.UserCondition
import com.focussound.data.FocusMode
import com.focussound.data.FocusSession
import com.focussound.sounddesign.SoundPatch

class AdaptiveSoundRecommender {
    fun score(
        patch: SoundPatch,
        mode: FocusMode,
        taste: UserSoundTasteVector?,
        condition: UserCondition?,
        history: List<FocusSession>
    ): Float {
        var score = 0f
        if (patch.mode == mode) score += 35f

        taste?.let {
            score += similarity(patch.brightness, it.preferredBrightness) * 10f
            score += similarity(patch.warmth, it.preferredWarmth) * 10f
            score += similarity(patch.movement, it.preferredMovement) * 8f
            score += similarity(patch.highCut, it.preferredHighCut) * 8f
            score += similarity(patch.stereoWidth, it.preferredStereoWidth) * 5f
            score += (it.preferredNoiseTypes[patch.baseNoiseType] ?: 0f) * 10f
        }

        val related = history.filter {
            it.mode == mode && it.soundType.name == patch.baseNoiseType.name
        }
        val avgFocus = related.map { it.focusRating }.averageOrDefault(3.0).toFloat()
        val avgFatigue = related.map { it.fatigueRating }.averageOrDefault(3.0).toFloat()
        score += avgFocus * 8f
        score += (6f - avgFatigue) * 7f

        if (condition?.sleepDebt == true) {
            score += (1f - patch.brightness) * 8f
            score += patch.highCut * 8f
            score -= patch.movement * 5f
        }
        if (condition?.selfReportedFatigue == FatigueLevel.HIGH) {
            score += patch.highCut * 6f
            score -= patch.brightness * 6f
        }
        score += explorationBonus(patch, history)
        return score
    }

    private fun similarity(a: Float, b: Float): Float {
        return 1f - kotlin.math.abs(a - b).coerceIn(0f, 1f)
    }

    private fun explorationBonus(patch: SoundPatch, history: List<FocusSession>): Float {
        val useCount = history.count { it.patchId == patch.id }
        return when (useCount) {
            0 -> 4f
            1 -> 2f
            else -> 0f
        }
    }

    private fun List<Int>.averageOrDefault(default: Double): Double {
        if (isEmpty()) return default
        return average()
    }
}
