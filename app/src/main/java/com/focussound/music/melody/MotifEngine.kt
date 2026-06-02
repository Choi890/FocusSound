package com.focussound.music.melody

import com.focussound.music.model.MusicTask
import kotlin.random.Random

data class Motif(
    val degrees: List<Int>,
    val rhythms: List<Float>
)

class MotifEngine {
    fun create(task: MusicTask, random: Random): Motif {
        val pool = when (task) {
            MusicTask.SLEEP -> listOf(
                Motif(listOf(0, 2, 1), listOf(4f, 4f, 8f)),
                Motif(listOf(0, 3, 2), listOf(6f, 4f, 6f))
            )
            MusicTask.READING -> listOf(
                Motif(listOf(0, 2, 4), listOf(4f, 4f, 8f)),
                Motif(listOf(4, 2, 0), listOf(6f, 2f, 8f))
            )
            MusicTask.WORKOUT -> listOf(
                Motif(listOf(0, 0, 2, 3), listOf(1f, 1f, 1f, 1f)),
                Motif(listOf(0, 2, 4, 2), listOf(0.5f, 0.5f, 1f, 2f))
            )
            else -> listOf(
                Motif(listOf(0, 2, 4, 2), listOf(1f, 1f, 2f, 4f)),
                Motif(listOf(4, 3, 1, 2), listOf(2f, 1f, 1f, 4f)),
                Motif(listOf(0, 1, 3, 5), listOf(1.5f, 0.5f, 2f, 4f)),
                Motif(listOf(5, 4, 2, 0), listOf(2f, 2f, 1f, 3f))
            )
        }
        return pool.random(random)
    }
}
