package com.donfuy.android.today.ui.bin

import com.donfuy.android.today.model.Task

sealed interface BinAction {
    data class OnDeleteTask(val task: Task) : BinAction
    data class OnRestoreTask(val task: Task) : BinAction
    data object OnDeleteAllTasks : BinAction
}