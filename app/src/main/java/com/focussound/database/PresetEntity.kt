package com.focussound.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.focussound.data.FocusMode
import com.focussound.data.SoundProfile
import com.focussound.data.SoundType
import com.focussound.data.UserSoundPreset

@Entity(tableName = "sound_presets")
data class PresetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val mode: String,
    val soundType: String,
    val brightness: Float,
    val warmth: Float,
    val movement: Float,
    val createdAtMillis: Long,
    val lastUsedAtMillis: Long
) {
    fun toDomain(): UserSoundPreset = UserSoundPreset(
        id = id,
        name = name,
        profile = SoundProfile(
            mode = enumValueOrDefault(mode, FocusMode.STUDY),
            soundType = enumValueOrDefault(soundType, SoundType.PINK),
            brightness = brightness,
            warmth = warmth,
            movement = movement
        ),
        createdAtMillis = createdAtMillis,
        lastUsedAtMillis = lastUsedAtMillis
    )
}

fun UserSoundPreset.toEntity(): PresetEntity = PresetEntity(
    id = id,
    name = name,
    mode = profile.mode.name,
    soundType = profile.soundType.name,
    brightness = profile.brightness,
    warmth = profile.warmth,
    movement = profile.movement,
    createdAtMillis = createdAtMillis,
    lastUsedAtMillis = lastUsedAtMillis
)

internal inline fun <reified T : Enum<T>> enumValueOrDefault(name: String, fallback: T): T {
    return enumValues<T>().firstOrNull { it.name == name } ?: fallback
}
