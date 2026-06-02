package com.focussound.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.focussound.audio.tone.ToneControlState
import com.focussound.composition.CompositionPatch
import com.focussound.data.FocusMode
import com.focussound.data.SoundProfile
import com.focussound.data.SoundType
import com.focussound.instrument.InstrumentSet
import com.focussound.playback.PlaybackMode
import com.focussound.sounddesign.NoiseType
import com.focussound.sounddesign.SoundPatch
import com.focussound.sounddesign.toNoiseType
import com.focussound.sounddesign.toSoundPatch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class PlaybackStatus {
    STOPPED,
    PLAYING,
    PAUSED
}

data class FocusPlaybackState(
    val status: PlaybackStatus = PlaybackStatus.STOPPED,
    val profile: SoundProfile = SoundProfile(),
    val patch: SoundPatch = SoundProfile().toSoundPatch(25),
    val compositionPatch: CompositionPatch? = null,
    val instrumentSet: InstrumentSet? = null,
    val playbackMode: PlaybackMode = PlaybackMode.SOUND_DESIGN,
    val startedAtMillis: Long = 0L
) {
    val isPlaying: Boolean = status == PlaybackStatus.PLAYING
    val isActive: Boolean = status != PlaybackStatus.STOPPED
}

object FocusSoundController {
    private val _playbackState = MutableStateFlow(FocusPlaybackState())
    val playbackState: StateFlow<FocusPlaybackState> = _playbackState.asStateFlow()

    @Volatile
    private var pendingCompositionPatch: CompositionPatch? = null

    @Volatile
    private var pendingInstrumentSet: InstrumentSet? = null

    fun play(context: Context, profile: SoundProfile, durationMinutes: Int) {
        playPatch(context, profile.toSoundPatch(durationMinutes), durationMinutes)
    }

    fun playPatch(
        context: Context,
        patch: SoundPatch,
        durationMinutes: Int,
        compositionPatch: CompositionPatch? = null,
        instrumentSet: InstrumentSet? = null,
        playbackMode: PlaybackMode = PlaybackMode.SOUND_DESIGN
    ) {
        pendingCompositionPatch = compositionPatch
        pendingInstrumentSet = instrumentSet
        val sleepFadeOutMillis = if (patch.mode == FocusMode.SLEEP) {
            durationMinutes.coerceAtLeast(1) * 60_000L
        } else {
            0L
        }
        ContextCompat.startForegroundService(
            context.applicationContext,
            FocusSoundService.intent(context, Action.START).apply {
                putPatch(patch)
                putExtra(Extra.PLAYBACK_MODE, playbackMode.name)
                putExtra(Extra.SLEEP_FADE_OUT_MILLIS, sleepFadeOutMillis)
            }
        )
    }

    fun pause(context: Context) {
        context.applicationContext.startService(FocusSoundService.intent(context, Action.PAUSE))
    }

    fun resume(context: Context) {
        context.applicationContext.startService(FocusSoundService.intent(context, Action.RESUME))
    }

    fun stop(context: Context) {
        context.applicationContext.startService(FocusSoundService.intent(context, Action.STOP))
    }

    fun updateTone(context: Context, tone: ToneControlState) {
        context.applicationContext.startService(
            FocusSoundService.intent(context, Action.UPDATE_TONE).apply {
                putExtra(Extra.TONE_BRIGHTNESS, tone.brightness)
                putExtra(Extra.TONE_WARMTH, tone.warmth)
                putExtra(Extra.TONE_COLDNESS, tone.coldness)
            }
        )
    }

    internal fun update(state: FocusPlaybackState) {
        _playbackState.value = state
    }

    internal fun takePendingCompositionPatch(): CompositionPatch? {
        return pendingCompositionPatch.also { pendingCompositionPatch = null }
    }

    internal fun takePendingInstrumentSet(): InstrumentSet? {
        return pendingInstrumentSet.also { pendingInstrumentSet = null }
    }

    object Action {
        const val START = "com.focussound.action.START"
        const val PAUSE = "com.focussound.action.PAUSE"
        const val RESUME = "com.focussound.action.RESUME"
        const val STOP = "com.focussound.action.STOP"
        const val UPDATE_TONE = "com.focussound.action.UPDATE_TONE"
    }

    object Extra {
        const val MODE = "mode"
        const val SOUND_TYPE = "sound_type"
        const val PATCH_ID = "patch_id"
        const val PATCH_NAME = "patch_name"
        const val NOISE_TYPE = "noise_type"
        const val BRIGHTNESS = "brightness"
        const val WARMTH = "warmth"
        const val MOVEMENT = "movement"
        const val HIGH_CUT = "high_cut"
        const val LOW_AMOUNT = "low_amount"
        const val STEREO_WIDTH = "stereo_width"
        const val NOISE_LAYER_AMOUNT = "noise_layer_amount"
        const val RAIN_LAYER_AMOUNT = "rain_layer_amount"
        const val PAD_LAYER_AMOUNT = "pad_layer_amount"
        const val MODULATION_DEPTH = "modulation_depth"
        const val MODULATION_RATE_HZ = "modulation_rate_hz"
        const val TARGET_FATIGUE_SCORE = "target_fatigue_score"
        const val DURATION_MINUTES = "duration_minutes"
        const val PLAYBACK_MODE = "playback_mode"
        const val SLEEP_FADE_OUT_MILLIS = "sleep_fade_out_millis"
        const val TONE_BRIGHTNESS = "tone_brightness"
        const val TONE_WARMTH = "tone_warmth"
        const val TONE_COLDNESS = "tone_coldness"
    }
}

internal fun Intent.getToneControlState(): ToneControlState = ToneControlState(
    brightness = getFloatExtra(FocusSoundController.Extra.TONE_BRIGHTNESS, 0.5f),
    warmth = getFloatExtra(FocusSoundController.Extra.TONE_WARMTH, 0.5f),
    coldness = getFloatExtra(FocusSoundController.Extra.TONE_COLDNESS, 0.2f)
)

internal fun Intent.getPlaybackMode(): PlaybackMode = enumValueOrDefault(
    getStringExtra(FocusSoundController.Extra.PLAYBACK_MODE),
    PlaybackMode.SOUND_DESIGN
)

internal fun Intent.putProfile(profile: SoundProfile) {
    putExtra(FocusSoundController.Extra.MODE, profile.mode.name)
    putExtra(FocusSoundController.Extra.SOUND_TYPE, profile.soundType.name)
    putExtra(FocusSoundController.Extra.BRIGHTNESS, profile.brightness)
    putExtra(FocusSoundController.Extra.WARMTH, profile.warmth)
    putExtra(FocusSoundController.Extra.MOVEMENT, profile.movement)
}

internal fun Intent.putPatch(patch: SoundPatch) {
    putExtra(FocusSoundController.Extra.PATCH_ID, patch.id)
    putExtra(FocusSoundController.Extra.PATCH_NAME, patch.name)
    putExtra(FocusSoundController.Extra.MODE, patch.mode.name)
    putExtra(FocusSoundController.Extra.SOUND_TYPE, patch.baseNoiseType.name)
    putExtra(FocusSoundController.Extra.NOISE_TYPE, patch.baseNoiseType.name)
    putExtra(FocusSoundController.Extra.BRIGHTNESS, patch.brightness)
    putExtra(FocusSoundController.Extra.WARMTH, patch.warmth)
    putExtra(FocusSoundController.Extra.MOVEMENT, patch.movement)
    putExtra(FocusSoundController.Extra.HIGH_CUT, patch.highCut)
    putExtra(FocusSoundController.Extra.LOW_AMOUNT, patch.lowAmount)
    putExtra(FocusSoundController.Extra.STEREO_WIDTH, patch.stereoWidth)
    putExtra(FocusSoundController.Extra.NOISE_LAYER_AMOUNT, patch.noiseLayerAmount)
    putExtra(FocusSoundController.Extra.RAIN_LAYER_AMOUNT, patch.rainLayerAmount)
    putExtra(FocusSoundController.Extra.PAD_LAYER_AMOUNT, patch.padLayerAmount)
    putExtra(FocusSoundController.Extra.MODULATION_DEPTH, patch.modulationDepth)
    putExtra(FocusSoundController.Extra.MODULATION_RATE_HZ, patch.modulationRateHz)
    putExtra(FocusSoundController.Extra.TARGET_FATIGUE_SCORE, patch.targetFatigueScore)
    putExtra(FocusSoundController.Extra.DURATION_MINUTES, patch.durationMinutes)
}

internal fun Intent.getProfile(): SoundProfile = SoundProfile(
    mode = enumValueOrDefault(
        getStringExtra(FocusSoundController.Extra.MODE),
        FocusMode.STUDY
    ),
    soundType = enumValueOrDefault(
        getStringExtra(FocusSoundController.Extra.SOUND_TYPE),
        SoundType.PINK
    ),
    brightness = getFloatExtra(FocusSoundController.Extra.BRIGHTNESS, 0.35f),
    warmth = getFloatExtra(FocusSoundController.Extra.WARMTH, 0.6f),
    movement = getFloatExtra(FocusSoundController.Extra.MOVEMENT, 0.2f)
)

internal fun Intent.getPatch(): SoundPatch {
    val profile = getProfile()
    return SoundPatch(
        id = getStringExtra(FocusSoundController.Extra.PATCH_ID).orEmpty().ifBlank { profile.toSoundPatch(25).id },
        name = getStringExtra(FocusSoundController.Extra.PATCH_NAME) ?: profile.displayName,
        mode = profile.mode,
        baseNoiseType = enumValueOrDefault(
            getStringExtra(FocusSoundController.Extra.NOISE_TYPE)
                ?: getStringExtra(FocusSoundController.Extra.SOUND_TYPE),
            profile.soundType.toNoiseType()
        ),
        brightness = profile.brightness,
        warmth = profile.warmth,
        movement = profile.movement,
        highCut = getFloatExtra(FocusSoundController.Extra.HIGH_CUT, 0.55f),
        lowAmount = getFloatExtra(FocusSoundController.Extra.LOW_AMOUNT, 0.55f),
        stereoWidth = getFloatExtra(FocusSoundController.Extra.STEREO_WIDTH, 0.35f),
        noiseLayerAmount = getFloatExtra(FocusSoundController.Extra.NOISE_LAYER_AMOUNT, 1f),
        rainLayerAmount = getFloatExtra(FocusSoundController.Extra.RAIN_LAYER_AMOUNT, 0f),
        padLayerAmount = getFloatExtra(FocusSoundController.Extra.PAD_LAYER_AMOUNT, 0f),
        modulationDepth = getFloatExtra(FocusSoundController.Extra.MODULATION_DEPTH, profile.movement * 0.25f),
        modulationRateHz = getFloatExtra(FocusSoundController.Extra.MODULATION_RATE_HZ, 0.02f + profile.movement * 0.08f),
        targetFatigueScore = getIntExtra(FocusSoundController.Extra.TARGET_FATIGUE_SCORE, 30),
        durationMinutes = getIntExtra(FocusSoundController.Extra.DURATION_MINUTES, 25)
    )
}

private inline fun <reified T : Enum<T>> enumValueOrDefault(name: String?, fallback: T): T {
    return enumValues<T>().firstOrNull { it.name == name } ?: fallback
}
