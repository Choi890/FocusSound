package com.focussound.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.focussound.composition.Chord
import com.focussound.composition.ChordExtension
import com.focussound.composition.ChordQuality
import com.focussound.composition.CompositionGenre
import com.focussound.composition.CompositionPatch
import com.focussound.composition.MusicalKey
import com.focussound.composition.PitchClass
import com.focussound.composition.ScaleType
import com.focussound.data.FocusMode
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "composition_patches")
data class CompositionPatchEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val mode: String,
    val genre: String,
    val tempoBpm: Int,
    val keyRoot: String,
    val keyScaleType: String,
    val chordProgressionJson: String,
    val melodyDensity: Float,
    val rhythmDensity: Float,
    val harmonicComplexity: Float,
    val padAmount: Float,
    val moodKeywordsJson: String,
    val instrumentNamesJson: String,
    val fatigueScore: Int,
    val durationMinutes: Int,
    val createdAtMillis: Long,
    val lastUsedAtMillis: Long
) {
    fun toDomain(notes: List<CompositionNoteEntity>): CompositionPatch = CompositionPatch(
        id = id,
        name = name,
        mode = enumValueOrDefault(mode, FocusMode.STUDY),
        genre = enumValueOrDefault(genre, CompositionGenre.AMBIENT_CODING),
        tempoBpm = tempoBpm,
        key = MusicalKey(
            root = enumValueOrDefault(keyRoot, PitchClass.C),
            scaleType = enumValueOrDefault(keyScaleType, ScaleType.AMBIENT)
        ),
        chordProgression = chordProgressionJson.toChordProgression(),
        notes = notes.map { it.toDomain() },
        melodyDensity = melodyDensity,
        rhythmDensity = rhythmDensity,
        harmonicComplexity = harmonicComplexity,
        padAmount = padAmount,
        moodKeywords = moodKeywordsJson.toStringList(),
        instrumentNames = instrumentNamesJson.toStringList(),
        fatigueScore = fatigueScore,
        durationMinutes = durationMinutes,
        createdAtMillis = createdAtMillis
    )
}

fun CompositionPatch.toEntity(
    nowMillis: Long = System.currentTimeMillis()
): CompositionPatchEntity = CompositionPatchEntity(
    id = id,
    name = name,
    mode = mode.name,
    genre = genre.name,
    tempoBpm = tempoBpm,
    keyRoot = key.root.name,
    keyScaleType = key.scaleType.name,
    chordProgressionJson = chordProgression.toChordProgressionJson(),
    melodyDensity = melodyDensity,
    rhythmDensity = rhythmDensity,
    harmonicComplexity = harmonicComplexity,
    padAmount = padAmount,
    moodKeywordsJson = moodKeywords.toJsonArrayString(),
    instrumentNamesJson = instrumentNames.toJsonArrayString(),
    fatigueScore = fatigueScore,
    durationMinutes = durationMinutes,
    createdAtMillis = createdAtMillis,
    lastUsedAtMillis = nowMillis
)

private fun List<Chord>.toChordProgressionJson(): String {
    val array = JSONArray()
    forEach { chord ->
        array.put(
            JSONObject()
                .put("root", chord.root.name)
                .put("quality", chord.quality.name)
                .put("extension", chord.extension.name)
                .put("degree", chord.degree)
        )
    }
    return array.toString()
}

private fun String.toChordProgression(): List<Chord> = runCatching {
    val array = JSONArray(this)
    buildList {
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            add(
                Chord(
                    root = enumValueOrDefault(item.optString("root"), PitchClass.C),
                    quality = enumValueOrDefault(item.optString("quality"), ChordQuality.MINOR),
                    extension = enumValueOrDefault(item.optString("extension"), ChordExtension.NONE),
                    degree = item.optInt("degree", index)
                )
            )
        }
    }
}.getOrElse {
    listOf(Chord(PitchClass.C, ChordQuality.MINOR, degree = 0))
}

private fun List<String>.toJsonArrayString(): String {
    val array = JSONArray()
    forEach { array.put(it) }
    return array.toString()
}

private fun String.toStringList(): List<String> = runCatching {
    val array = JSONArray(this)
    buildList {
        for (index in 0 until array.length()) {
            add(array.optString(index))
        }
    }.filter { it.isNotBlank() }
}.getOrElse {
    emptyList()
}
