package com.donfuy.android.today.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.donfuy.android.today.R
import com.donfuy.android.today.model.Task
import com.donfuy.android.today.ui.TaskRow

@Composable
fun TomorrowTaskRow(
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
        swipeLeftText = stringResource(id = R.string.swipe_action_today),
        swipeLeftTextColor = MaterialTheme.colorScheme.onPrimary,
        swipeLeftBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
        swipeLeftIcon = Icons.Outlined.DateRange,
        swipeLeftIconTint = MaterialTheme.colorScheme.onPrimary,
        onSwipeRight = onSwipeRight,
        swipeRightText = stringResource(id = R.string.swipe_action_delete),
        swipeRightTextColor = MaterialTheme.colorScheme.error,
        swipeRightBackgroundColor = MaterialTheme.colorScheme.errorContainer,
        swipeRightIcon = Icons.Outlined.Delete,
        swipeRightIconTint = MaterialTheme.colorScheme.error
    )
}