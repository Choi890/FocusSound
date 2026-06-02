package com.focussound.music.realtime

import com.focussound.music.model.LiveCompositionRequest
import com.focussound.music.model.MusicTask

class LiveVariationController {
    fun normalizedRequest(request: LiveCompositionRequest): LiveCompositionRequest {
        return when (request.task) {
            MusicTask.SLEEP -> request.copy(
                melodyAmount = request.melodyAmount.coerceAtMost(0.22f),
                rhythmAmount = 0f,
                diversity = request.diversity.coerceAtMost(0.42f)
            )
            MusicTask.READING -> request.copy(
                melodyAmount = request.melodyAmount.coerceAtMost(0.26f),
                rhythmAmount = 0f
            )
            MusicTask.STUDY -> request.copy(
                melodyAmount = request.melodyAmount.coerceAtMost(0.45f),
                rhythmAmount = request.rhythmAmount.coerceAtMost(0.18f)
            )
            MusicTask.WORKOUT -> request.copy(
                melodyAmount = request.melodyAmount.coerceAtLeast(0.38f),
                rhythmAmount = request.rhythmAmount.coerceAtLeast(0.68f)
            )
            else -> request
        }
    }
}
