package com.donfuy.android.today.ui.settings

sealed interface SettingsAction {
    data class OnToggleShowCompleted(val showCompleted: Boolean) : SettingsAction
    data class OnToggleCompletedToBottom(val completedToBottom: Boolean) : SettingsAction
    data class OnToggleDynamicTheme(val useDynamicTheme: Boolean) : SettingsAction
    data class OnUpdateHourToDeleteTasks(val hourToDeleteTasks: Int) : SettingsAction
    data class OnUpdateMinToDeleteTasks(val minToDeleteTasks: Int) : SettingsAction
}