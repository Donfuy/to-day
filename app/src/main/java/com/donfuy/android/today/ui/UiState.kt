package com.donfuy.android.today.ui

import com.donfuy.android.today.model.Task

data class HomeUiState(
    val todayTasks: List<Task> = listOf(),
    val tomorrowTasks: List<Task> = listOf(),
    val showCompleted: Boolean = true,
    val completedToBottom: Boolean = true,
    val tabVisible: Boolean = tomorrowTasks.isNotEmpty()
)

data class BinUiState(
    val binTasks: List<Task> = listOf()
)

data class SettingsUiState(
    val showCompleted: Boolean = true,
    val completedToBottom: Boolean = true,
    val useDynamicTheme: Boolean = false,
    val hourToDeleteTasks: Int = 3,
    val minToDeleteTasks: Int = 0
)