package com.focussound.music.realtime

import com.focussound.composition.CompositionPatch
import com.focussound.music.generation.TaskAwareComposer
import com.focussound.music.model.LiveCompositionRequest

class RollingComposer(
    private val taskAwareComposer: TaskAwareComposer = TaskAwareComposer(),
    private val variationController: LiveVariationController = LiveVariationController(),
    private val nextSectionGenerator: NextSectionGenerator = NextSectionGenerator(taskAwareComposer)
) {
    fun compose(request: LiveCompositionRequest): CompositionPatch {
        val normalized = variationController.normalizedRequest(request)
        return taskAwareComposer.generatePatch(normalized)
    }

    fun prepareAhead(
        request: LiveCompositionRequest,
        memory: MusicMemory,
        queue: RealTimeGenerationQueue,
        playbackBar: Int,
        aheadBars: Int = DEFAULT_AHEAD_BARS
    ): MusicMemory {
        if (queue.barsAhead(playbackBar) >= aheadBars) return memory
        val normalized = variationController.normalizedRequest(request)
        val segment = nextSectionGenerator.generate(
            request = normalized,
            memory = memory,
            startBar = playbackBar + queue.barsAhead(playbackBar),
            minBars = aheadBars
        )
        queue.enqueue(segment)
        return memory.remember(segment.piece)
    }

    fun recentMemory(): List<com.focussound.music.generation.MusicFingerprint> {
        return taskAwareComposer.recentFingerprints()
    }

    private companion object {
        const val DEFAULT_AHEAD_BARS = 12
    }
}
