package com.donfuy.android.today.ui

import androidx.annotation.StringRes
import com.donfuy.android.today.R
import com.donfuy.android.today.model.Task

data class HomeUiState(
    val tasks: List<Task> = listOf(),
    val todayTasks: List<Task> = listOf(),
    val tomorrowTasks: List<Task> = listOf(),
    val showCompleted: Boolean = true,
    val completedToBottom: Boolean = true,
    val tabVisible: Boolean = tomorrowTasks.isNotEmpty(),
    val currentTab: HomeTab = HomeTab.TODAY,
    val currentEditItemId: Int = -1
)

enum class HomeTab(@StringRes val title: Int) {
    TODAY(title = R.string.today_tab_title),
    TOMORROW(title = R.string.tomorrow_tab_title)
}
