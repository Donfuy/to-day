package com.donfuy.android.today.ui.todo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.donfuy.android.today.model.TodoItem
import kotlinx.coroutines.flow.Flow

// Todo: Pressing the IME action key should add a new item and change focus to it.
// TODO: Move components to TodoComponents.kt
// TODO: maxlines of 5 for each row

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TodoScreen(
    items: Flow<List<TodoItem>>,
    editingItems: Flow<List<TodoItem>>,
    onAddItem: (TodoItem) -> Unit,
    onDeleteItem: (TodoItem) -> Unit,
    onUpdateItem: (TodoItem) -> Unit,
    currentlyEditingItem: TodoItem?,
    setCheck: (TodoItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val todoItems = items.collectAsState(initial = listOf())

    val isEditing = editingItems.collectAsState(initial = listOf()).value.isNotEmpty()

    // Hack to fix the keyboard bug in compose
    LocalFocusManager.current.clearFocus(true)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Today!")
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        },
        floatingActionButton = {
            AnimatedVisibility(/*!isEditing*/ true) {
                FloatingActionButton(
                    onClick = {
                        onAddItem(TodoItem(task = "", editing = true))
                    },
                ) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = "New Task")
                }
            }
        }
    ) { contentPadding ->
        val (text, setText) = remember { mutableStateOf("") }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column {
                TodoList(
                    items = todoItems.value,
                    onItemClicked = onDeleteItem,
                    setCheck = setCheck,
                    onUpdateItem = onUpdateItem,
                    onDeleteItem = onDeleteItem,
                )
            }
//            AnimatedVisibility(
//                expanded,
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//
//            ) {
//                Row(
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxWidth()
//                ) {
//                    TextField(
//                        value = text,
//                        onValueChange = setText,
//                        shape = RoundedCornerShape(
//                            topStart = 16.dp,
//                            bottomStart = 16.dp,
//                            topEnd = 0.dp,
//                            bottomEnd = 0.dp
//                        ),
//                        colors = TextFieldDefaults.textFieldColors(
//                            focusedIndicatorColor = Color.Transparent
//                        ),
//                        modifier = Modifier
//                            .weight(1f)
//                            .focusRequester(focusRequester)
//                    )
//                    FloatingActionButton(
//                        onClick = {
//                            onAddItem(TodoItem(task = text, position = 0))
//                        },
//                        shape = RoundedCornerShape(
//                            topStart = 0.dp,
//                            bottomStart = 0.dp,
//                            topEnd = 16.dp,
//                            bottomEnd = 16.dp
//                        ),
//                        elevation = FloatingActionButtonDefaults.elevation(
//                            0.dp,
//                            0.dp
//                        )
//                    ) {
//                        Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add Task")
//                    }
//                    LaunchedEffect(Unit) {
//                        focusRequester.requestFocus()
//                    }
//                }
//
//            }

        }
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