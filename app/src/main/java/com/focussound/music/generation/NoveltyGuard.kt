package com.focussound.music.generation

class NoveltyGuard(
    private val threshold: Float = 0.72f
) {
    fun isTooSimilar(
        newFingerprint: MusicFingerprint,
        recentFingerprints: List<MusicFingerprint>
    ): Boolean {
        return recentFingerprints.any { old -> similarity(newFingerprint, old) > threshold }
    }

    fun similarity(a: MusicFingerprint, b: MusicFingerprint): Float {
        var score = 0f
        if (a.formSignature == b.formSignature) score += 0.25f
        if (a.key == b.key) score += 0.1f
        if (kotlin.math.abs(a.tempo - b.tempo) <= 3) score += 0.05f
        score += jaccard(a.chordNgrams, b.chordNgrams) * 0.3f
        if (a.motifContourHash == b.motifContourHash) score += 0.2f
        if (a.rhythmPatternHash == b.rhythmPatternHash) score += 0.1f
        return score.coerceIn(0f, 1f)
    }

    private fun jaccard(a: List<String>, b: List<String>): Float {
        val left = a.toSet()
        val right = b.toSet()
        val union = left union right
        val intersection = left intersect right
        return if (union.isEmpty()) 0f else intersection.size.toFloat() / union.size
    }
}
