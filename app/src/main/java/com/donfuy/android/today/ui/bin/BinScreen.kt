package com.donfuy.android.today.ui.bin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.RestoreFromTrash
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.donfuy.android.today.R
import com.donfuy.android.today.model.Task
import com.donfuy.android.today.ui.BinAction
import com.donfuy.android.today.ui.TaskRow
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BinScreen(
    tasks: Flow<List<Task>>,
    onBackClick: () -> Unit,
    onAction: (BinAction) -> Unit
) {
    // TODO: Banner explaining what the bin is and what happens to the tasks in this list
    // TODO: Right action button should delete all tasks in the bin

    val taskItems = tasks.collectAsState(initial = listOf())

    val openDialog = remember { mutableStateOf(false) }

    DeleteAllAlertDialog(
        openDialog = openDialog,
        confirmOnClick = {
            onAction(BinAction.OnDeleteAllTasks)
            openDialog.value = false
        },
        dismissOnClick = { openDialog.value = false }
    )

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(id = R.string.bin_screen_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.bin_back_content_description)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { openDialog.value = true }) {
                            Icon(
                                imageVector = Icons.Filled.DeleteSweep,
                                contentDescription = stringResource(R.string.bin_right_action_delete_content_description)
                            )
                        }
                    }
                )
                HorizontalDivider(
                    thickness = Dp.Hairline,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            BinList(
                tasks = taskItems.value,
                onDeleteTask = {
                    onAction(BinAction.OnDeleteTask(it))
                },
                onRestoreTask = {
                    onAction(BinAction.OnRestoreTask(it))
                }
            )
        }
    }
}

@Composable
fun BinList(
    tasks: List<Task>,
    onDeleteTask: (Task) -> Unit,
    onRestoreTask: (Task) -> Unit
) {
    LazyColumn {
        items(tasks, key = { it.id }) { task ->
            BinRow(
                task = task,
                onSwipeLeft = { onDeleteTask(task) },
                onSwipeRight = { onRestoreTask(task) }
            )
            HorizontalDivider(thickness = Dp.Hairline, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun BinRow(
    task: Task,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    TaskRow(
        task = task,
        onSwipeLeft = onSwipeLeft,
        swipeLeftText = stringResource(id = R.string.swipe_action_delete_forever),
        swipeLeftTextColor = MaterialTheme.colorScheme.onErrorContainer,
        swipeLeftBackgroundColor = MaterialTheme.colorScheme.errorContainer,
        swipeLeftIcon = Icons.Outlined.Delete,
        swipeLeftIconTint = MaterialTheme.colorScheme.onErrorContainer,
        onSwipeRight = onSwipeRight,
        swipeRightText = stringResource(id = R.string.swipe_action_restore),
        swipeRightTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
        swipeRightBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
        swipeRightIcon = Icons.Outlined.RestoreFromTrash,
        swipeRightIconTint = MaterialTheme.colorScheme.onPrimaryContainer,
        checkBoxEnabled = false
    )
}

@Composable
fun DeleteAllAlertDialog(
    openDialog: MutableState<Boolean>,
    confirmOnClick: () -> Unit,
    dismissOnClick: () -> Unit
) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = dismissOnClick,
            title = {
                Text(stringResource(R.string.dialog_delete_all_binned_title))
            },
            text = {
                Text(stringResource(R.string.dialog_delete_all_binned_description))
            },
            confirmButton = {
                TextButton(onClick = {
                    openDialog.value = true
                    confirmOnClick()
                }) {
                    Text(stringResource(R.string.dialog_delete_all_binned_confirm_deletion))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    openDialog.value = false
                    dismissOnClick()
                }) {
                    Text(stringResource(R.string.dialog_delete_all_binned_cancel))
                }
            }
        )
    }
}