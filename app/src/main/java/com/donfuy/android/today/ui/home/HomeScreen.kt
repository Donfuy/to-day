package com.donfuy.android.today.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.donfuy.android.today.model.Task
import kotlinx.coroutines.flow.Flow


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
    showCompleted: Flow<Boolean>,
    completedToBottom: Flow<Boolean>,
    onClickSettings: () -> Unit,
    onClickBin: () -> Unit,
) {

    var todayTasks = todayTasksFlow.collectAsState(initial = listOf()).value
    val tomorrowTasks = tomorrowTasksFlow.collectAsState(initial = listOf()).value

    if (!showCompleted.collectAsState(initial = false).value) {
        todayTasks = todayTasks.filter { !it.checked }
    }

    if (completedToBottom.collectAsState(initial = true).value) {
        todayTasks = todayTasks.sortedBy { it.checked }
    }

    // Id of task being edited - -1 if no task is being edited
    val (currentEditItemId, setCurrentEditItemId) = remember { mutableStateOf(-1) }

    var tabState by remember { mutableStateOf(0) }
    val tabTitles = listOf("Today", "Tomorrow")
    val tabVisible = remember { mutableStateOf(false) }
    tabVisible.value = tomorrowTasks.isNotEmpty()

    Scaffold(topBar = {
        HomeTopBar(
            onClickSettings = onClickSettings, onClickBin = onClickBin
        )
    }, bottomBar = {
        HomeBottomBar(onSubmit = { task ->
            onAddTask(task, tabState == 1)
        })
//        BottomBarFlex(false)
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
                )
            }

        }
    }
}