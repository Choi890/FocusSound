package com.focussound.timer

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FocusTimer(
    private val scope: CoroutineScope
) {
    private var timerJob: Job? = null

    fun start(
        durationSeconds: Int,
        onTick: (remainingSeconds: Int, elapsedSeconds: Int) -> Unit,
        onFinish: () -> Unit
    ) {
        stop()

        val total = durationSeconds.coerceAtLeast(1)
        val startedAt = SystemClock.elapsedRealtime()

        timerJob = scope.launch {
            onTick(total, 0)
            while (true) {
                delay(TICK_MS)
                val elapsed = ((SystemClock.elapsedRealtime() - startedAt) / 1000L)
                    .toInt()
                    .coerceAtMost(total)
                val remaining = (total - elapsed).coerceAtLeast(0)
                onTick(remaining, elapsed)

                if (remaining == 0) {
                    timerJob = null
                    onFinish()
                    break
                }
            }
        }
    }

    fun stop() {
        timerJob?.cancel()
        timerJob = null
    }

    private companion object {
        const val TICK_MS = 250L
    }
}
