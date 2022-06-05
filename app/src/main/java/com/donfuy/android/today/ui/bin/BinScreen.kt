package com.donfuy.android.today.ui.bin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.RestoreFromTrash
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.donfuy.android.today.model.Task
import com.donfuy.android.today.ui.SwipeableRow
import com.donfuy.android.today.ui.TaskRow
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BinScreen(
    tasks: Flow<List<Task>>,
    onClickBack: () -> Unit,
    onDeleteTask: (Task) -> Unit,
    onRestoreTask: (Task) -> Unit
) {
    // TODO: Banner explaining what the bin is and what happens to the tasks in this list
    // TODO: Right action button should delete all tasks in the bin

    val taskItems = tasks.collectAsState(initial = listOf())

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("Bin") },
                    navigationIcon = {
                        IconButton(onClick = onClickBack) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back to Today Screen"
                            )
                        }
                    }
                )
                Divider(thickness = Dp.Hairline, color = MaterialTheme.colorScheme.secondary)
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
                onDeleteTask,
                onRestoreTask
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
        items(tasks) { task ->
            key(task) {
                BinRow(
                    task = task,
                    onDeleteTask = { onDeleteTask(task) },
                    onRestoreTask = { onRestoreTask(task) }
                )
                Divider(thickness = Dp.Hairline, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun BinRow(
    task: Task,
    onDeleteTask: () -> Unit,
    onRestoreTask: () -> Unit
) {
    TaskRow(
        task = task,
        onSwipeLeft = onDeleteTask,
        swipeLeftText = "Delete",
        swipeLeftTextColor = MaterialTheme.colorScheme.error,
        swipeLeftBackgroundColor = MaterialTheme.colorScheme.errorContainer,
        swipeLeftIcon = Icons.Outlined.Delete,
        swipeLeftIconTint = MaterialTheme.colorScheme.error,
        onSwipeRight = onRestoreTask,
        swipeRightText = "Restore",
        swipeRightTextColor = MaterialTheme.colorScheme.inversePrimary,
        swipeRightBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
        swipeRightIcon = Icons.Outlined.RestoreFromTrash,
        swipeRightIconTint = MaterialTheme.colorScheme.inversePrimary,
        checkBoxEnabled = false
    )
}