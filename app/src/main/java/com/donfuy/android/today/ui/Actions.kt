package com.donfuy.android.today.ui

import com.donfuy.android.today.model.Task

sealed interface HomeAction {
    data class OnAddTask(val task: String, val tomorrow: Boolean) : HomeAction
    data class OnBinTask(val task: Task) : HomeAction
    data class OnUpdateTask(val task: Task) : HomeAction
    data class SetCheck(val task: Task, val checked: Boolean) : HomeAction
    data class SetToday(val task: Task) : HomeAction
    data class SetTomorrow(val task: Task) : HomeAction
    data class SetShowCompleted(val showCompleted: Boolean) : HomeAction
    data class OnTabClick(val tab: HomeTab) : HomeAction
    data class SetTaskEntryVisible(val visible: Boolean) : HomeAction
    data class OnTaskClick(val task: Task) : HomeAction
    data class OnSwipeLeft(val task: Task) : HomeAction
    data class OnSwipeRight(val task: Task) : HomeAction
}

sealed interface BinAction {
    data class OnDeleteTask(val task: Task) : BinAction
    data class OnRestoreTask(val task: Task) : BinAction
    data object OnDeleteAllTasks : BinAction
}

sealed interface SettingsAction {
    data class OnToggleShowCompleted(val showCompleted: Boolean) : SettingsAction
    data class OnToggleCompletedToBottom(val completedToBottom: Boolean) : SettingsAction
    data class OnToggleDynamicTheme(val useDynamicTheme: Boolean) : SettingsAction
    data class OnUpdateHourToDeleteTasks(val hourToDeleteTasks: Int) : SettingsAction
    data class OnUpdateMinToDeleteTasks(val minToDeleteTasks: Int) : SettingsAction
}