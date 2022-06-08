package com.donfuy.android.today.ui.today

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.donfuy.android.today.model.Task
import com.donfuy.android.today.ui.TaskRow

@Composable
fun TodayTaskRow(
    task: Task,
    setCheck: (Boolean) -> Unit,
    onItemClicked: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    TaskRow(
        task = task,
        setCheck = setCheck,
        onItemClicked = onItemClicked,
        onSwipeLeft = onSwipeLeft,
        swipeLeftText = "Delete",
        swipeLeftTextColor = MaterialTheme.colorScheme.error,
        swipeLeftBackgroundColor = MaterialTheme.colorScheme.errorContainer,
        swipeLeftIcon = Icons.Outlined.Delete,
        swipeLeftIconTint = MaterialTheme.colorScheme.error,
        onSwipeRight = onSwipeRight,
        swipeRightText = "Tomorrow",
        swipeRightTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
        swipeRightBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
        swipeRightIcon = Icons.Outlined.DateRange,
        swipeRightIconTint = MaterialTheme.colorScheme.onPrimaryContainer
    )
}