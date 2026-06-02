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
    // Activity는 권한 요청과 Compose 화면 연결만 맡고, 음악 생성/재생 상태는 ViewModel이 유지한다.
    // 이렇게 하면 화면 회전 같은 구성 변경에도 오디오 엔진 상태가 갑자기 사라지지 않는다.
    private val viewModel: FocusSoundViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Android 13 이상에서는 알림 권한이 없으면 재생 알림을 띄울 수 없어서 화면 구성 전에 요청한다.
        requestNotificationPermissionIfNeeded()
        setContent {
            FocusSoundTheme {
                FocusSoundApp(viewModel = viewModel)
            }
        }
    }

    override fun onDestroy() {
        // 사용자가 앱을 실제로 닫을 때만 오디오 리소스를 해제한다.
        // 단순 회전/재생성에서는 ViewModel을 살려 두어 재생 흐름이 끊기지 않게 한다.
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
