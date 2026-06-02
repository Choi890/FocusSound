package com.focussound.data

import com.focussound.composition.CompositionGenre
import org.json.JSONObject
import java.util.UUID

data class FocusSession(
    val id: String = UUID.randomUUID().toString(),
    val mode: FocusMode,
    val soundType: SoundType,
    val durationMinutes: Int,
    val elapsedSeconds: Int,
    val startedAtMillis: Long,
    val endedAtMillis: Long,
    val focusRating: Int,
    val fatigueRating: Int,
    val brightness: Float,
    val warmth: Float,
    val movement: Float,
    val fatigueScore: Int,
    val completed: Boolean,
    val tooBright: Boolean = false,
    val tooMuffled: Boolean = false,
    val tooMuchBass: Boolean = false,
    val patchId: String? = null,
    val patchName: String? = null,
    val highCut: Float = 0.55f,
    val stereoWidth: Float = 0.35f,
    val rainLayerAmount: Float = 0f,
    val padLayerAmount: Float = 0f,
    val conditionSnapshotId: Long? = null,
    val compositionPatchId: String? = null,
    val compositionPatchName: String? = null,
    val compositionGenre: CompositionGenre? = null,
    val melodyDensity: Float = 0f,
    val rhythmDensity: Float = 0f,
    val harmonicComplexity: Float = 0f,
    val melodyAnnoying: Boolean = false,
    val rhythmAnnoying: Boolean = false,
    val harmonyLiked: Boolean = false,
    val tooRepetitive: Boolean = false,
    val focusedWell: Boolean = false,
    val tooDark: Boolean = false,
    val useAgain: Boolean = false
) {
    fun toJsonObject(): JSONObject = JSONObject()
        .put("id", id)
        .put("mode", mode.name)
        .put("soundType", soundType.name)
        .put("durationMinutes", durationMinutes)
        .put("elapsedSeconds", elapsedSeconds)
        .put("startedAtMillis", startedAtMillis)
        .put("endedAtMillis", endedAtMillis)
        .put("focusRating", focusRating)
        .put("fatigueRating", fatigueRating)
        .put("brightness", brightness.toDouble())
        .put("warmth", warmth.toDouble())
        .put("movement", movement.toDouble())
        .put("fatigueScore", fatigueScore)
        .put("completed", completed)
        .put("tooBright", tooBright)
        .put("tooMuffled", tooMuffled)
        .put("tooMuchBass", tooMuchBass)
        .put("patchId", patchId)
        .put("patchName", patchName)
        .put("highCut", highCut.toDouble())
        .put("stereoWidth", stereoWidth.toDouble())
        .put("rainLayerAmount", rainLayerAmount.toDouble())
        .put("padLayerAmount", padLayerAmount.toDouble())
        .put("conditionSnapshotId", conditionSnapshotId)
        .put("compositionPatchId", compositionPatchId)
        .put("compositionPatchName", compositionPatchName)
        .put("compositionGenre", compositionGenre?.name)
        .put("melodyDensity", melodyDensity.toDouble())
        .put("rhythmDensity", rhythmDensity.toDouble())
        .put("harmonicComplexity", harmonicComplexity.toDouble())
        .put("melodyAnnoying", melodyAnnoying)
        .put("rhythmAnnoying", rhythmAnnoying)
        .put("harmonyLiked", harmonyLiked)
        .put("tooRepetitive", tooRepetitive)
        .put("focusedWell", focusedWell)
        .put("tooDark", tooDark)
        .put("useAgain", useAgain)

    companion object {
        fun fromJsonObject(json: JSONObject): FocusSession? = runCatching {
            FocusSession(
                id = json.optString("id", UUID.randomUUID().toString()),
                mode = json.optEnum("mode", FocusMode.STUDY),
                soundType = json.optEnum("soundType", SoundType.PINK),
                durationMinutes = json.optInt("durationMinutes", 25),
                elapsedSeconds = json.optInt("elapsedSeconds", 0),
                startedAtMillis = json.optLong("startedAtMillis", 0L),
                endedAtMillis = json.optLong("endedAtMillis", 0L),
                focusRating = json.optInt("focusRating", 3),
                fatigueRating = json.optInt("fatigueRating", 3),
                brightness = json.optDouble("brightness", 0.35).toFloat(),
                warmth = json.optDouble("warmth", 0.6).toFloat(),
                movement = json.optDouble("movement", 0.2).toFloat(),
                fatigueScore = json.optInt("fatigueScore", 0),
                completed = json.optBoolean("completed", true),
                tooBright = json.optBoolean("tooBright", false),
                tooMuffled = json.optBoolean("tooMuffled", false),
                tooMuchBass = json.optBoolean("tooMuchBass", false),
                patchId = json.optString("patchId").takeIf { it.isNotBlank() },
                patchName = json.optString("patchName").takeIf { it.isNotBlank() },
                highCut = json.optDouble("highCut", 0.55).toFloat(),
                stereoWidth = json.optDouble("stereoWidth", 0.35).toFloat(),
                rainLayerAmount = json.optDouble("rainLayerAmount", 0.0).toFloat(),
                padLayerAmount = json.optDouble("padLayerAmount", 0.0).toFloat(),
                conditionSnapshotId = json.optLong("conditionSnapshotId", 0L).takeIf { it > 0L },
                compositionPatchId = json.optString("compositionPatchId").takeIf { it.isNotBlank() },
                compositionPatchName = json.optString("compositionPatchName").takeIf { it.isNotBlank() },
                compositionGenre = json.optString("compositionGenre").takeIf { it.isNotBlank() }?.let {
                    enumValues<CompositionGenre>().firstOrNull { genre -> genre.name == it }
                },
                melodyDensity = json.optDouble("melodyDensity", 0.0).toFloat(),
                rhythmDensity = json.optDouble("rhythmDensity", 0.0).toFloat(),
                harmonicComplexity = json.optDouble("harmonicComplexity", 0.0).toFloat(),
                melodyAnnoying = json.optBoolean("melodyAnnoying", false),
                rhythmAnnoying = json.optBoolean("rhythmAnnoying", false),
                harmonyLiked = json.optBoolean("harmonyLiked", false),
                tooRepetitive = json.optBoolean("tooRepetitive", false),
                focusedWell = json.optBoolean("focusedWell", false),
                tooDark = json.optBoolean("tooDark", false),
                useAgain = json.optBoolean("useAgain", false)
            )
        }.getOrNull()
    }
}

private inline fun <reified T : Enum<T>> JSONObject.optEnum(key: String, fallback: T): T {
    val value = optString(key, fallback.name)
    return enumValues<T>().firstOrNull { it.name == value } ?: fallback
}
