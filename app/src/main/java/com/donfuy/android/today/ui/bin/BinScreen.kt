package com.donfuy.android.today.ui.bin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.donfuy.android.today.model.TaskItem
import com.donfuy.android.today.ui.SwipeableRow
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BinScreen(
    tasks: Flow<List<TaskItem>>,
    onClickBack: () -> Unit,
    onDeleteItem: (TaskItem) -> Unit
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
                .padding(contentPadding)) {
            BinList(tasks = taskItems.value, onDeleteItem = onDeleteItem)
        }
    }
}

@Composable
fun BinList(
    tasks: List<TaskItem>,
    onDeleteItem: (TaskItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn {
        items(tasks) { task ->
            BinRow(task = task.task, checked = task.checked)
            Divider(thickness = Dp.Hairline, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BinRow(
    task: String,
    checked: Boolean
) {
    // Inactive checkboxes
    // "+" button to add them back to today
    // Swipe actions: Left to permanently delete, right to move them to today
    SwipeableRow(
        onItemClicked = { /*TODO*/ },
        onSwipedLeft = { /*TODO*/ },
        onSwipedRight = { /*TODO*/ },
        swipeLeftContent = { /*TODO*/ },
        swipeRightContent = { /*TODO*/ }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = null,
                    enabled = false,
                    modifier = Modifier
                        .padding(end = 32.dp)
                        .size(24.dp)
                )
                Text(
                    text = task,
                    modifier = Modifier.align(CenterVertically),
                    style = MaterialTheme.typography.titleSmall,
                    softWrap = true
                )

            }
        }
    }
}