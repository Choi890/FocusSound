package com.focussound.personalization

import com.focussound.data.FocusSession
import com.focussound.sounddesign.NoiseType

class TasteVectorUpdater {
    fun update(
        current: UserSoundTasteVector,
        session: FocusSession
    ): UserSoundTasteVector {
        val ratingBoost = if (session.focusedWell || session.useAgain) 1 else 0
        val rating = (((session.focusRating + (6 - session.fatigueRating)) / 2f).toInt() + ratingBoost)
            .coerceIn(1, 5)
        val noiseScores = current.preferredNoiseTypes.toMutableMap()
        val noiseType = NoiseType.valueOf(session.soundType.name)
        val oldNoiseScore = noiseScores[noiseType] ?: 0.33f
        noiseScores[noiseType] = updatePreference(oldNoiseScore, 1f, rating, alpha = 0.08f)

        return current.copy(
            preferredBrightness = updatePreference(
                current.preferredBrightness,
                when {
                    session.tooBright -> (session.brightness - 0.12f).coerceIn(0f, 1f)
                    session.tooDark -> (session.brightness + 0.12f).coerceIn(0f, 1f)
                    else -> session.brightness
                },
                rating
            ),
            preferredWarmth = updatePreference(current.preferredWarmth, session.warmth, rating),
            preferredMovement = updatePreference(current.preferredMovement, session.movement, rating),
            preferredHighCut = updatePreference(current.preferredHighCut, session.highCut, rating),
            preferredStereoWidth = updatePreference(current.preferredStereoWidth, session.stereoWidth, rating),
            preferredNoiseTypes = noiseScores.normalizeScores(),
            preferredSessionMinutes = updateSessionMinutes(current.preferredSessionMinutes, session.durationMinutes, rating),
            confidence = (current.confidence + 0.08f).coerceIn(0f, 1f),
            preferredMelodyDensity = updatePreference(
                current.preferredMelodyDensity,
                session.melodyDensity,
                if (session.melodyAnnoying) 2 else rating
            ),
            preferredRhythmDensity = updatePreference(
                current.preferredRhythmDensity,
                session.rhythmDensity,
                if (session.rhythmAnnoying) 2 else rating
            ),
            preferredHarmonicComplexity = updatePreference(
                current.preferredHarmonicComplexity,
                session.harmonicComplexity,
                if (session.harmonyLiked) 5 else rating
            ),
            preferredGenre = if (
                session.compositionGenre != null &&
                (rating >= 4 || session.harmonyLiked || session.useAgain) &&
                !session.melodyAnnoying &&
                !session.rhythmAnnoying &&
                !session.tooRepetitive
            ) {
                session.compositionGenre
            } else {
                current.preferredGenre
            }
        )
    }

    fun updatePreference(
        oldValue: Float,
        newValue: Float,
        rating: Int,
        alpha: Float = 0.12f
    ): Float {
        val weight = when (rating) {
            5 -> 1.0f
            4 -> 0.7f
            3 -> 0.2f
            2 -> -0.3f
            else -> -0.6f
        }
        return (oldValue + alpha * weight * (newValue - oldValue)).coerceIn(0f, 1f)
    }

    private fun updateSessionMinutes(oldValue: Int, newValue: Int, rating: Int): Int {
        val target = if (rating >= 4) newValue else oldValue
        return (oldValue * 0.85f + target * 0.15f).toInt().coerceIn(5, 180)
    }

    private fun MutableMap<NoiseType, Float>.normalizeScores(): Map<NoiseType, Float> {
        val total = values.sum().takeIf { it > 0f } ?: 1f
        return mapValues { (_, value) -> (value / total).coerceIn(0f, 1f) }
    }
}
