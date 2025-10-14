package com.donfuy.android.today.ui.home

import com.donfuy.android.today.model.Task
import com.donfuy.android.today.ui.HomeTab

sealed interface HomeAction {
    data object OnAddTask : HomeAction
    data class OnBinTask(val task: Task) : HomeAction
    data class OnSubmitTask(val task: Task) : HomeAction
    data class SetCheck(val task: Task, val checked: Boolean) : HomeAction
    data class SetToday(val task: Task) : HomeAction
    data class SetTomorrow(val task: Task) : HomeAction
    data class OnTabClick(val tab: HomeTab) : HomeAction
    data class OnTaskClick(val task: Task) : HomeAction
    data class OnSwipeLeft(val task: Task) : HomeAction
    data class OnSwipeRight(val task: Task) : HomeAction
}