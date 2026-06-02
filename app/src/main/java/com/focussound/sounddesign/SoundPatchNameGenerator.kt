package com.focussound.sounddesign

import com.focussound.ai.TextureHint
import com.focussound.data.FocusMode

class SoundPatchNameGenerator {
    fun buildName(mode: FocusMode, texture: TextureHint): String {
        return when (texture) {
            TextureHint.RAINY -> "비 오는 ${mode.label} 집중음"
            TextureHint.NIGHT -> "밤의 ${mode.label} 집중음"
            TextureHint.WARM_PAD -> "따뜻한 패드 집중음"
            TextureHint.DEEP_ROOM -> "깊은 공간 집중음"
            TextureHint.LIBRARY -> "도서관처럼 차분한 노이즈"
            TextureHint.CYBER -> "부드러운 코딩 그리드"
            TextureHint.SLEEP_DARK -> "어두운 수면 노이즈"
            TextureHint.CLEAN -> "부드러운 ${mode.label} 노이즈"
        }
    }
}
