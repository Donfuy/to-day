package com.donfuy.android.today.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.donfuy.android.today.R
import com.donfuy.android.today.model.Task
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    todayTasksFlow: Flow<List<Task>>,
    tomorrowTasksFlow: Flow<List<Task>>,
    onAddTask: (String, Boolean) -> Unit,
    onBinTask: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    setCheck: (Task, Boolean) -> Unit,
    setToday: (Task) -> Unit,
    setTomorrow: (Task) -> Unit,
    showCompletedFlow: Flow<Boolean>,
    setShowCompleted: (Boolean) -> Unit,
    completedToBottomFlow: Flow<Boolean>,
    onClickSettings: () -> Unit,
    onClickBin: () -> Unit,
) {
    val completedToBottom = completedToBottomFlow.collectAsState(initial = false).value
    val showCompleted = showCompletedFlow.collectAsState(initial = false).value

    val todayTasks = todayTasksFlow.collectAsState(initial = listOf()).value
        .completedToBottom(completedToBottom)
        .showCompleted(showCompleted)

    val tomorrowTasks = tomorrowTasksFlow.collectAsState(initial = listOf()).value
        .completedToBottom(completedToBottom)
        .showCompleted(showCompleted)

    val homeListState = rememberLazyListState()

    // Id of task being edited - -1 if no task is being edited
    val (currentEditItemId, setCurrentEditItemId) = rememberSaveable { mutableStateOf(-1) }

    // Tabs
    var tabState by remember { mutableStateOf(0) }
    val tabTitles = listOf(
        stringResource(id = R.string.today_tab_title),
        stringResource(id = R.string.tomorrow_tab_title)
    )
    val tabVisible = remember { mutableStateOf(false) }
    tabVisible.value = tomorrowTasks.isNotEmpty()

    // BottomBar + FAB
    val taskEntryFocusRequester = remember { FocusRequester() }
    val (taskEntryVisible, setTaskEntryVisible) = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Scaffold(topBar = {
        HomeTopBar(
            onClickSettings = onClickSettings, onClickBin = onClickBin
        )
    }, bottomBar = {
        if (taskEntryVisible) {
            TaskEntryBottomBar(
                onSubmit = { onAddTask(it, tabState == 1) },
                taskEntryFocusRequester = taskEntryFocusRequester,
                onCloseClick = {
                    focusManager.clearFocus()
                    setTaskEntryVisible(false)
                }
            )
        }

    }, floatingActionButton = {
        AnimatedVisibility(
            visible = !taskEntryVisible,
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            HomeFAB(onClick = { setTaskEntryVisible(true) })
        }

    }) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            AnimatedVisibility(visible = tabVisible.value) {
                TabRow(selectedTabIndex = tabState) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(text = { Text(title) },
                            selected = tabState == index,
                            onClick = { tabState = index })
                    }
                }
            }
            Box(
                modifier = Modifier.weight(1f)
            ) {
                TaskList(
                    tasks = if ((tabState == 0) || !tabVisible.value) {
                        todayTasks
                    } else {
                        tomorrowTasks
                    },
                    onItemClicked = { setCurrentEditItemId(it.id.toInt()) },
                    setCheck = setCheck,
                    setToday = setToday,
                    setTomorrow = setTomorrow,
                    onUpdateTask = {
                        onUpdateTask(it)
                        setCurrentEditItemId(-1)
                    },
                    onBinTask = onBinTask,
                    currentEditItemId = currentEditItemId,
                    state = homeListState,
                    showCompletedFlow = showCompletedFlow,
                    setShowCompleted = setShowCompleted,
                    completedToBottomFlow = completedToBottomFlow
                )
            }

        }
    }
}

@Composable
fun HomeFAB(onClick: () -> Unit) {
        LargeFloatingActionButton(
            onClick =  onClick,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                Icons.Filled.Add,
                stringResource(id = R.string.add_task_content_description),
                modifier = Modifier.size(36.dp)
            )
        }
}

private fun List<Task>.showCompleted(showCompleted: Boolean): List<Task> {
    return if (!showCompleted) {
        this.filter { !it.checked }
    } else {
        this
    }
}

private fun List<Task>.completedToBottom(completedToBottom: Boolean): List<Task> {
    return if (completedToBottom) {
        this.sortedBy { it.checked }
    } else {
        this
    }
}

@Suppress("unused")
private const val TAG = "HomeScreen"