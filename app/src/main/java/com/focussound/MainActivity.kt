package com.focussound

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.focussound.ui.FocusSoundApp
import com.focussound.ui.FocusSoundViewModel
import com.focussound.ui.theme.FocusSoundTheme

class MainActivity : ComponentActivity() {
    // One ViewModel owns the composition and audio engine state across configuration changes.
    private val viewModel: FocusSoundViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ask for notification access before the Compose tree starts emitting playback controls.
        requestNotificationPermissionIfNeeded()
        setContent {
            FocusSoundTheme {
                FocusSoundApp(viewModel = viewModel)
            }
        }
    }

    override fun onDestroy() {
        // Release audio only for a real Activity finish; rotations should keep the ViewModel alive.
        if (isFinishing) {
            viewModel.releaseAudio()
        }
        super.onDestroy()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }
}
