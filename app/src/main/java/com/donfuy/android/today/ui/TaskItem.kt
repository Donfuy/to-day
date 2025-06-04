package com.donfuy.android.today.ui

import com.donfuy.android.today.model.Task

data class TaskItem(
    val task: Task,
    val isEditing: Boolean
)
