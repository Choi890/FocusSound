package com.focussound.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.focussound.composition.CompositionGenre
import com.focussound.data.FocusMode
import com.focussound.personalization.UserSoundTasteVector
import com.focussound.sounddesign.NoiseType
import org.json.JSONObject

@Entity(tableName = "user_taste_vectors")
data class UserTasteVectorEntity(
    @PrimaryKey
    val mode: String,
    val preferredBrightness: Float,
    val preferredWarmth: Float,
    val preferredMovement: Float,
    val preferredHighCut: Float,
    val preferredStereoWidth: Float,
    val preferredNoiseTypesJson: String,
    val preferredSessionMinutes: Int,
    val confidence: Float,
    val preferredGenre: String,
    val preferredTempoMin: Int,
    val preferredTempoMax: Int,
    val preferredMelodyDensity: Float,
    val preferredRhythmDensity: Float,
    val preferredHarmonicComplexity: Float,
    val updatedAtMillis: Long
) {
    fun toDomain(): UserSoundTasteVector {
        val noiseScores = runCatching {
            val json = JSONObject(preferredNoiseTypesJson)
            NoiseType.entries.associateWith { type -> json.optDouble(type.name, 0.33).toFloat() }
        }.getOrElse {
            NoiseType.entries.associateWith { 1f / NoiseType.entries.size }
        }
        return UserSoundTasteVector(
            mode = enumValueOrDefault(mode, FocusMode.STUDY),
            preferredBrightness = preferredBrightness,
            preferredWarmth = preferredWarmth,
            preferredMovement = preferredMovement,
            preferredHighCut = preferredHighCut,
            preferredStereoWidth = preferredStereoWidth,
            preferredNoiseTypes = noiseScores,
            preferredSessionMinutes = preferredSessionMinutes,
            confidence = confidence,
            preferredGenre = enumValueOrDefault(preferredGenre, CompositionGenre.AMBIENT_CODING),
            preferredTempoRange = preferredTempoMin..preferredTempoMax,
            preferredMelodyDensity = preferredMelodyDensity,
            preferredRhythmDensity = preferredRhythmDensity,
            preferredHarmonicComplexity = preferredHarmonicComplexity
        )
    }
}

fun UserSoundTasteVector.toEntity(nowMillis: Long = System.currentTimeMillis()): UserTasteVectorEntity {
    val json = JSONObject()
    preferredNoiseTypes.forEach { (type, score) -> json.put(type.name, score.toDouble()) }
    return UserTasteVectorEntity(
        mode = mode.name,
        preferredBrightness = preferredBrightness,
        preferredWarmth = preferredWarmth,
        preferredMovement = preferredMovement,
        preferredHighCut = preferredHighCut,
        preferredStereoWidth = preferredStereoWidth,
        preferredNoiseTypesJson = json.toString(),
        preferredSessionMinutes = preferredSessionMinutes,
        confidence = confidence,
        preferredGenre = preferredGenre.name,
        preferredTempoMin = preferredTempoRange.first,
        preferredTempoMax = preferredTempoRange.last,
        preferredMelodyDensity = preferredMelodyDensity,
        preferredRhythmDensity = preferredRhythmDensity,
        preferredHarmonicComplexity = preferredHarmonicComplexity,
        updatedAtMillis = nowMillis
    )
}
