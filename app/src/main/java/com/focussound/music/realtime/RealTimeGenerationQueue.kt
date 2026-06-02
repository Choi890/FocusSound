package com.focussound.music.realtime

import com.focussound.composition.CompositionPatch
import com.focussound.music.model.GeneratedPiece

data class QueuedMusicSegment(
    val startBar: Int,
    val endBar: Int,
    val piece: GeneratedPiece,
    val patch: CompositionPatch
)

class RealTimeGenerationQueue {
    private val segments = ArrayDeque<QueuedMusicSegment>()

    fun enqueue(segment: QueuedMusicSegment) {
        segments.addLast(segment)
        while (segments.size > MAX_SEGMENTS) {
            segments.removeFirst()
        }
    }

    fun pollReady(playbackBar: Int): QueuedMusicSegment? {
        val first = segments.firstOrNull() ?: return null
        return if (first.startBar <= playbackBar + READY_MARGIN_BARS) {
            segments.removeFirst()
        } else {
            null
        }
    }

    fun barsAhead(playbackBar: Int): Int {
        val furthestBar = segments.maxOfOrNull { it.endBar } ?: playbackBar
        return (furthestBar - playbackBar).coerceAtLeast(0)
    }

    fun snapshot(): List<QueuedMusicSegment> = segments.toList()

    fun clear() {
        segments.clear()
    }

    private companion object {
        const val MAX_SEGMENTS = 8
        const val READY_MARGIN_BARS = 2
    }
}
