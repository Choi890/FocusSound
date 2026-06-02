package com.focussound.recommendation

import com.focussound.data.FocusMode
import com.focussound.data.FocusSession
import com.focussound.data.RecommendedSound
import com.focussound.data.SoundProfile
import com.focussound.data.SoundType
import com.focussound.data.UserPreference
import com.focussound.data.UserSoundPreset

class SoundRecommender(
    private val fatigueEstimator: FatigueEstimator = FatigueEstimator()
) {
    fun recommend(
        preference: UserPreference,
        sessions: List<FocusSession>
    ): SoundProfile = recommendToday(preference, sessions, emptyList()).profile

    fun recommendToday(
        preference: UserPreference,
        sessions: List<FocusSession>,
        presets: List<UserSoundPreset>
    ): RecommendedSound {
        val recentModeSessions = sessions
            .filter { it.mode == preference.mode }
            .takeLast(RECENT_SESSION_LIMIT)

        val base = if (recentModeSessions.size < MIN_HISTORY_FOR_PERSONALIZATION) {
            defaultProfileFor(preference.mode).copy(soundType = preference.soundType)
        } else {
            personalizedProfile(preference, recentModeSessions)
        }

        val recentPreset = presets.firstOrNull { it.profile.mode == preference.mode }
        val blended = if (recentPreset != null && recentModeSessions.size < 6) {
            base.blendWith(recentPreset.profile, presetWeight = 0.35f)
        } else {
            base
        }

        val score = fatigueEstimator.estimate(blended, preference.timerMinutes)
        val reason = when {
            recentModeSessions.isEmpty() -> "기본 설정과 현재 작업 모드를 기준으로 추천"
            recentPreset != null -> "최근 프리셋과 세션 피드백을 함께 반영"
            else -> "최근 집중도와 체감 피로도 기록을 반영"
        }

        return RecommendedSound(
            profile = blended,
            title = "오늘 추천 사운드",
            reason = reason,
            fatigueScore = score
        )
    }

    private fun personalizedProfile(
        preference: UserPreference,
        sessions: List<FocusSession>
    ): SoundProfile {
        val bestType = sessions
            .groupBy { it.soundType }
            .maxByOrNull { (_, grouped) -> grouped.v2QualityScore() }
            ?.key ?: preference.soundType

        val relevant = sessions.filter { it.soundType == bestType }.ifEmpty { sessions }
        val avgBrightness = relevant.map { it.brightness }.averageOrDefault(preference.brightness.toDouble()).toFloat()
        val avgWarmth = relevant.map { it.warmth }.averageOrDefault(preference.warmth.toDouble()).toFloat()
        val avgMovement = relevant.map { it.movement }.averageOrDefault(preference.movement.toDouble()).toFloat()

        val brightComplaints = relevant.count { it.tooBright }.toFloat() / relevant.size.coerceAtLeast(1)
        val muffledComplaints = relevant.count { it.tooMuffled }.toFloat() / relevant.size.coerceAtLeast(1)
        val bassComplaints = relevant.count { it.tooMuchBass }.toFloat() / relevant.size.coerceAtLeast(1)
        val highFatigue = (relevant.map { it.fatigueRating }.averageOrDefault(3.0) - 3.0)
            .toFloat()
            .coerceAtLeast(0f)

        return SoundProfile(
            mode = preference.mode,
            soundType = when {
                bassComplaints > 0.35f && bestType == SoundType.BROWN -> SoundType.PINK
                brightComplaints > 0.45f && bestType == SoundType.WHITE -> SoundType.PINK
                else -> bestType
            },
            brightness = (avgBrightness - brightComplaints * 0.22f + muffledComplaints * 0.12f - highFatigue * 0.05f)
                .coerceIn(0.1f, 0.82f),
            warmth = (avgWarmth + brightComplaints * 0.12f - muffledComplaints * 0.08f - bassComplaints * 0.12f)
                .coerceIn(0.18f, 0.9f),
            movement = (avgMovement - highFatigue * 0.05f)
                .coerceIn(0.04f, 0.72f)
        )
    }

    private fun List<FocusSession>.v2QualityScore(): Double {
        return map { session ->
            val brightPenalty = if (session.tooBright) 0.7 else 0.0
            val muffledPenalty = if (session.tooMuffled) 0.35 else 0.0
            val bassPenalty = if (session.tooMuchBass) 0.45 else 0.0
            val completionBonus = if (session.completed) 0.45 else -0.35
            session.focusRating * 1.55 -
                session.fatigueRating * 1.05 -
                session.fatigueScore * 0.025 -
                brightPenalty -
                muffledPenalty -
                bassPenalty +
                completionBonus
        }.averageOrDefault(0.0)
    }

    private fun defaultProfileFor(mode: FocusMode): SoundProfile = when (mode) {
        FocusMode.STUDY -> SoundProfile(mode = mode, soundType = SoundType.PINK, brightness = 0.36f, warmth = 0.6f, movement = 0.17f)
        FocusMode.CODING -> SoundProfile(mode = mode, soundType = SoundType.PINK, brightness = 0.42f, warmth = 0.52f, movement = 0.24f)
        FocusMode.READING -> SoundProfile(mode = mode, soundType = SoundType.BROWN, brightness = 0.24f, warmth = 0.72f, movement = 0.12f)
        FocusMode.SLEEP -> SoundProfile(mode = mode, soundType = SoundType.BROWN, brightness = 0.13f, warmth = 0.82f, movement = 0.07f)
    }

    private fun SoundProfile.blendWith(other: SoundProfile, presetWeight: Float): SoundProfile {
        val currentWeight = 1f - presetWeight
        return copy(
            soundType = other.soundType,
            brightness = brightness * currentWeight + other.brightness * presetWeight,
            warmth = warmth * currentWeight + other.warmth * presetWeight,
            movement = movement * currentWeight + other.movement * presetWeight
        )
    }

    private fun List<Number>.averageOrDefault(default: Double): Double {
        if (isEmpty()) return default
        return map { it.toDouble() }.average()
    }

    private companion object {
        const val RECENT_SESSION_LIMIT = 32
        const val MIN_HISTORY_FOR_PERSONALIZATION = 3
    }
}
