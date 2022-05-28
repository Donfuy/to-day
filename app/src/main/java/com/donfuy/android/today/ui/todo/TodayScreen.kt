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
import com.donfuy.android.today.ui.theme.TodayTheme
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
    items: Flow<List<TaskItem>>,
    onAddItem: (TaskItem) -> Unit,
    onDeleteItem: (TaskItem) -> Unit,
    onUpdateItem: (TaskItem) -> Unit,
    onClickSettings: () -> Unit,
    onClickBin: () -> Unit,
    setCheck: (TaskItem) -> Unit
) {

    val todoItems = items.collectAsState(initial = listOf())

    // Id of task being edited - -1 if no task is being edited
    val (currentEditItemId, setCurrentEditItemId) = remember { mutableStateOf(-1) }

    val isKeyboardOpen by keyboardAsState()

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
        AnimatedVisibility(visible = true) {
            TodayBottomBar(onSubmit = { task -> onAddItem(TaskItem(task = task)) })
        }
    }) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                TodayList(
                    items = todoItems.value,
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

@Composable
fun TodoScreenPreview() {
    TodayTheme {

    }
}

//@Preview
//@Composable
//fun TodoScreenPreview() {
//    ToDayTheme {
//        val items = listOf<TodoItem>(
//            TodoItem(2, "a", complete = false, position = 0),
//            TodoItem(3, "b", complete = false, position = 1))
//        val itemsFlow: Flow<List<TodoItem>> = flow {
//            while (true) {
//                emit(items)
//            }
//        }
//        TodoScreen(items = itemsFlow, {}, {})
//    }
//}