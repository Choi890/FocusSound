package com.focussound.music.harmony

import com.focussound.composition.MusicalKey
import com.focussound.music.model.MusicTask

class ModulationPlanner {
    fun maybeModulate(task: MusicTask, currentKey: MusicalKey, sectionIndex: Int): MusicalKey {
        return if (task == MusicTask.WORKOUT && sectionIndex >= 4) {
            currentKey.copy(root = currentKey.root.transpose(2))
        } else {
            currentKey
        }
    }
}
