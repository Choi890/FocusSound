package com.focussound.audio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AudioSampleBus {
    private val _samples = MutableStateFlow(FloatArray(WAVEFORM_SIZE))
    val samples: StateFlow<FloatArray> = _samples.asStateFlow()

    fun publish(samples: FloatArray) {
        _samples.value = samples.copyOf()
    }

    const val WAVEFORM_SIZE = 128
}
