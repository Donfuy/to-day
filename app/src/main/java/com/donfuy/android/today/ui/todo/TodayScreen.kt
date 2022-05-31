package com.donfuy.android.today.ui.todo

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import com.donfuy.android.today.model.TaskItem
import kotlinx.coroutines.flow.Flow

enum class Keyboard {
    Opened, Closed
}

@Composable
fun keyboardAsState(): State<Keyboard> {
    val keyboardState = remember { mutableStateOf(Keyboard.Closed) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                Keyboard.Opened
            } else {
                Keyboard.Closed
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    todayTasks: Flow<List<TaskItem>>,
    tomorrowTasks: Flow<List<TaskItem>>,
    onAddItem: (String, Boolean) -> Unit,
    onDeleteItem: (TaskItem) -> Unit,
    onUpdateItem: (TaskItem) -> Unit,
    onClickSettings: () -> Unit,
    onClickBin: () -> Unit,
    setCheck: (TaskItem) -> Unit,
    showCompleted: Flow<Boolean>,
    completedToBottom: Flow<Boolean>
) {

    var todoItems = todayTasks.collectAsState(initial = listOf()).value
    val tomorrowItems = tomorrowTasks.collectAsState(initial = listOf()).value

    if (!showCompleted.collectAsState(initial = false).value) {
        todoItems = todoItems.filter { !it.checked }
    }
    if (completedToBottom.collectAsState(initial = true).value) {
        todoItems = todoItems.sortedBy { it.checked }
    }

    // Id of task being edited - -1 if no task is being edited
    val (currentEditItemId, setCurrentEditItemId) = remember { mutableStateOf(-1) }

    val isKeyboardOpen by keyboardAsState()

    var tabState by remember { mutableStateOf(0) }
    val tabTitles = listOf("Today", "Tomorrow")
    val tabVisible = remember { mutableStateOf(false) }
    tabVisible.value = tomorrowItems.isNotEmpty()

    Scaffold(topBar = {
        TodayTopBar(
            onClickSettings = onClickSettings, onClickBin = onClickBin
        )
    }, floatingActionButton = {
        AnimatedVisibility(visible = false) {
            LargeFloatingActionButton(
                onClick = { }, containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Icon(
                    Icons.Filled.Add,
                    "Add task",
                    modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize)
                )
            }
        }
    }, bottomBar = {
        TodayBottomBar(
            onSubmit = { task ->
                onAddItem(task, tabState == 1)
            }
        )
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
                        Tab(
                            text = { Text(title) },
                            selected = tabState == index,
                            onClick = { tabState = index })
                    }
                }
            }
            Box(
                modifier = Modifier.weight(1f)
            ) {
                TodayList(
                    items = if ((tabState == 0) || !tabVisible.value) {
                        todoItems
                    } else {
                        tomorrowItems
                    },
                    onItemClicked = {
                        setCurrentEditItemId(it.id.toInt())
                    },
                    setCheck = setCheck,
                    onUpdateItem = {
                        onUpdateItem(it)
                        setCurrentEditItemId(-1)
                    },
                    onDeleteItem = onDeleteItem,
                    currentEditItemId = currentEditItemId
                )
            }

        }
    }
}