package com.donfuy.android.today.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.donfuy.android.today.model.Task
import kotlinx.coroutines.flow.Flow
import com.donfuy.android.today.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    var tabState by remember { mutableStateOf(0) }
    val tabTitles = listOf(
        stringResource(id = R.string.today_tab_title),
        stringResource(id = R.string.tomorrow_tab_title)
    )
    val tabVisible = remember { mutableStateOf(false) }
    tabVisible.value = tomorrowTasks.isNotEmpty()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = {
        HomeTopBar(
            onClickSettings = onClickSettings, onClickBin = onClickBin
        )
    }, bottomBar = {
        BottomBarFlex(onSubmit = { task ->
            onAddTask(task, tabState == 1)
            coroutineScope.launch {
                homeListState.animateScrollToItem(
                    todayTasks.size - todayTasks.filter { it.checked }.size
                )
            }

        })
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
                HomeLazyList(
                    items = if ((tabState == 0) || !tabVisible.value) {
                        todayTasks
                    } else {
                        tomorrowTasks
                    },
                    onItemClicked = {
                        setCurrentEditItemId(it.id.toInt())
                    },
                    setCheck = setCheck,
                    setToday = setToday,
                    setTomorrow = setTomorrow,
                    onUpdateTask = {
                        onUpdateTask(it)
                        setCurrentEditItemId(-1)
                    },
                    onBinTask = onBinTask,
                    currentEditItemId = currentEditItemId,
                    state = homeListState
                )
            }

        }
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