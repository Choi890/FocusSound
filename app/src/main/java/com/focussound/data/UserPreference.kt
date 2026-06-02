package com.focussound.data

data class UserPreference(
    val mode: FocusMode = FocusMode.STUDY,
    val soundType: SoundType = SoundType.NONE,
    val brightness: Float = 0.35f,
    val warmth: Float = 0.6f,
    val movement: Float = 0.2f,
    val timerMinutes: Int = 25,
    val customTimerMinutes: Int = 35
) {
    fun toSoundProfile(): SoundProfile = SoundProfile(
        mode = mode,
        soundType = soundType,
        brightness = brightness,
        warmth = warmth,
        movement = movement
    )
}
