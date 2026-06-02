package com.focussound.audio.sampler

import java.io.File

class SampleCache(
    private val decoder: SampleDecoder = SampleDecoder()
) {
    fun get(path: String): SampleData? {
        synchronized(sharedCache) {
            if (sharedCache.containsKey(path)) return sharedCache[path]
        }

        val decoded = decoder.decodeWav(File(path))
        synchronized(sharedCache) {
            sharedCache[path] = decoded
            return decoded
        }
    }

    fun preload(paths: Iterable<String>) {
        paths.distinct().forEach(::get)
    }

    private companion object {
        val sharedCache = mutableMapOf<String, SampleData?>()
    }
}
