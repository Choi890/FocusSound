package com.focussound.music.learning

class ModelSerializer {
    fun summarize(model: LearnedTaskStyleModel): String {
        return buildString {
            append(model.task.name)
            append('/')
            append(model.style.name)
            append(" form=")
            append(model.formModel.templates.size)
            append(" harmony=")
            append(model.harmonyModel.degreeTemplates.size)
            append(" motif=")
            append(model.motifModel.contourTemplates.size)
        }
    }
}
