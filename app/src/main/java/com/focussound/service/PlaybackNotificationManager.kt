package com.focussound.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.focussound.MainActivity
import com.focussound.R

class PlaybackNotificationManager(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "포커스사운드 재생",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "포커스사운드 백그라운드 재생 제어"
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun build(state: FocusPlaybackState): Notification {
        ensureChannel()
        val contentIntent = PendingIntent.getActivity(
            context,
            REQUEST_CONTENT,
            Intent(context, MainActivity::class.java),
            pendingIntentFlags()
        )

        val title = state.compositionPatch?.name ?: state.patch.name

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("포커스사운드")
            .setContentText("$title · ${state.status.label()}")
            .setContentIntent(contentIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(state.status != PlaybackStatus.STOPPED)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(
                android.R.drawable.ic_media_play,
                "재생",
                servicePendingIntent(FocusSoundController.Action.RESUME, REQUEST_RESUME)
            )
            .addAction(
                android.R.drawable.ic_media_pause,
                "일시정지",
                servicePendingIntent(FocusSoundController.Action.PAUSE, REQUEST_PAUSE)
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "정지",
                servicePendingIntent(FocusSoundController.Action.STOP, REQUEST_STOP)
            )
            .build()
    }

    fun notify(state: FocusPlaybackState) {
        notificationManager.notify(NOTIFICATION_ID, build(state))
    }

    private fun servicePendingIntent(action: String, requestCode: Int): PendingIntent {
        return PendingIntent.getService(
            context,
            requestCode,
            FocusSoundService.intent(context, action),
            pendingIntentFlags()
        )
    }

    private fun pendingIntentFlags(): Int {
        return PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    }

    private fun PlaybackStatus.label(): String = when (this) {
        PlaybackStatus.STOPPED -> "정지됨"
        PlaybackStatus.PLAYING -> "재생 중"
        PlaybackStatus.PAUSED -> "일시정지"
    }

    companion object {
        const val CHANNEL_ID = "focus_sound_playback"
        const val NOTIFICATION_ID = 2001
        private const val REQUEST_CONTENT = 3001
        private const val REQUEST_RESUME = 3002
        private const val REQUEST_PAUSE = 3003
        private const val REQUEST_STOP = 3004
    }
}
