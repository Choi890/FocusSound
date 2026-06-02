package com.focussound.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.focussound.audio.FocusSoundEngine
import com.focussound.data.FocusMode

class FocusSoundService : Service() {
    private val engine = FocusSoundEngine()
    private lateinit var notifications: PlaybackNotificationManager
    private var currentState = FocusPlaybackState()
    private var foregroundStarted = false

    override fun onCreate() {
        super.onCreate()
        notifications = PlaybackNotificationManager(this)
        notifications.ensureChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            FocusSoundController.Action.START -> handleStart(intent)
            FocusSoundController.Action.PAUSE -> handlePause()
            FocusSoundController.Action.RESUME -> handleResume()
            FocusSoundController.Action.STOP -> handleStop()
            FocusSoundController.Action.UPDATE_TONE -> intent.let { engine.updateTone(it.getToneControlState()) }
            else -> Unit
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        engine.release()
        FocusSoundController.update(FocusPlaybackState(status = PlaybackStatus.STOPPED))
        super.onDestroy()
    }

    private fun handleStart(intent: Intent) {
        val patch = intent.getPatch()
        val compositionPatch = FocusSoundController.takePendingCompositionPatch()
        val instrumentSet = FocusSoundController.takePendingInstrumentSet()
        val playbackMode = intent.getPlaybackMode()
        val profile = patch.toSoundProfile()
        val sleepFadeOutMillis = intent.getLongExtra(
            FocusSoundController.Extra.SLEEP_FADE_OUT_MILLIS,
            0L
        )
        val newState = FocusPlaybackState(
            status = PlaybackStatus.PLAYING,
            profile = profile,
            patch = patch,
            compositionPatch = compositionPatch,
            instrumentSet = instrumentSet,
            playbackMode = playbackMode,
            startedAtMillis = System.currentTimeMillis()
        )

        startAsForeground(newState)
        engine.start(
            patch = patch,
            compositionPatch = compositionPatch,
            instrumentSet = instrumentSet,
            playbackMode = playbackMode,
            sleepFadeOutMillis = sleepFadeOutMillis
        )
        updateState(newState)
    }

    private fun handlePause() {
        if (!currentState.isActive) return
        engine.pause()
        updateState(currentState.copy(status = PlaybackStatus.PAUSED))
    }

    private fun handleResume() {
        if (!currentState.isActive) return
        engine.resume()
        updateState(currentState.copy(status = PlaybackStatus.PLAYING))
    }

    private fun handleStop() {
        val fadeOut = if (currentState.profile.mode == FocusMode.SLEEP) {
            SLEEP_STOP_FADE_MS
        } else {
            DEFAULT_STOP_FADE_MS
        }
        engine.stop(fadeOutMillis = fadeOut)
        updateState(FocusPlaybackState(status = PlaybackStatus.STOPPED))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    private fun startAsForeground(state: FocusPlaybackState) {
        val notification = notifications.build(state)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                PlaybackNotificationManager.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(PlaybackNotificationManager.NOTIFICATION_ID, notification)
        }
        foregroundStarted = true
    }

    private fun updateState(state: FocusPlaybackState) {
        currentState = state
        FocusSoundController.update(state)
        if (foregroundStarted && state.status != PlaybackStatus.STOPPED) {
            notifications.notify(state)
        }
    }

    companion object {
        private const val DEFAULT_STOP_FADE_MS = 1_100L
        private const val SLEEP_STOP_FADE_MS = 4_000L

        fun intent(context: Context, action: String): Intent {
            return Intent(context.applicationContext, FocusSoundService::class.java).setAction(action)
        }
    }
}
