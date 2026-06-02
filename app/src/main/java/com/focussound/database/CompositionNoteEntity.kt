package com.focussound.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.focussound.composition.NoteEvent
import com.focussound.composition.NoteLane

@Entity(
    tableName = "composition_notes",
    indices = [Index(value = ["patchId"])]
)
data class CompositionNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val patchId: String,
    val startBeat: Float,
    val durationBeats: Float,
    val midiNote: Int,
    val velocity: Float,
    val lane: String
) {
    fun toDomain(): NoteEvent = NoteEvent(
        startBeat = startBeat,
        durationBeats = durationBeats,
        midiNote = midiNote,
        velocity = velocity,
        lane = enumValueOrDefault(lane, NoteLane.PAD)
    )
}

fun NoteEvent.toEntity(patchId: String): CompositionNoteEntity = CompositionNoteEntity(
    patchId = patchId,
    startBeat = startBeat,
    durationBeats = durationBeats,
    midiNote = midiNote,
    velocity = velocity,
    lane = lane.name
)
