package com.donfuy.android.today.ui.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.U
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import com.donfuy.android.today.model.TodoItem
import com.donfuy.android.today.ui.theme.ToDayTheme

// TodoTopBar

// TodoBottomNav

// TodoList
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TodoList(
    items: List<TodoItem>,
    onItemClicked: (TodoItem) -> Unit,
    setCheck: (TodoItem) -> Unit,
    modifier: Modifier = Modifier,
    onUpdateItem: (TodoItem) -> Unit,
    onDeleteItem: (TodoItem) -> Unit
) {
    LazyColumn() {
        items(items) { todo ->
            if (todo.editing) {
                // Show inline editor row
                TodoEditRow(
                    onItemSubmit = onUpdateItem,
                    onEmptySubmit = { onDeleteItem(todo) },
                    todo = todo,

                )
            } else {
                TodoRow(
                    todo = todo,
                    onItemClicked = { onItemClicked(todo) },
                    setCheck = { setCheck(todo) },
                    modifier = modifier
                        .fillParentMaxWidth()
                )
            }
        }
    }
}

// TodoRow
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoRow(
    todo: TodoItem,
    onItemClicked: (TodoItem) -> Unit,
    setCheck: (TodoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface {
        Row(
            modifier = Modifier
                .clickable { onItemClicked(todo) }
                .height(56.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Checkbox(
                checked = todo.checked,
                onCheckedChange = { setCheck(todo) },
                modifier = Modifier
                    .padding(end = 32.dp)
                    .size(24.dp, 24.dp)
            )
            Text(
                todo.task,
                modifier = Modifier.align(Alignment.CenterVertically),
                style = MaterialTheme.typography.titleSmall
            )
        }

    }
}

// TodoEditRow
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TodoEditRow(
    todo: TodoItem,
    onItemSubmit: (TodoItem) -> Unit,
    onEmptySubmit: () -> Unit
) {
    val (text, setText) = remember { mutableStateOf(todo.task) }
    val (checked, setChecked) = remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = FocusRequester()
    Surface {
        Row(
            modifier = Modifier
                .clickable { }
                .height(56.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { setChecked(!checked) },
                modifier = Modifier
                    .padding(end = 32.dp)
                    .size(24.dp, 24.dp)
            )
            BasicTextField(
                value = text,
                onValueChange = setText,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (text != "") {
                            onItemSubmit(todo.copy(task = text, checked = checked, editing = false))
                        } else {
                            onEmptySubmit()
                            keyboardController?.hide()
                        }
                    }),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}

@Composable
fun PreviewTodoList() {
    ToDayTheme {

    }
}

@Composable
fun PreviewTodoRow() {
    ToDayTheme {

    }
}

@Composable
fun PreviewTodoEditRow() {
    ToDayTheme {

    }
}

@Composable
fun BoxExample() {
    Box {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Cyan)
        )
        Box(
            Modifier
                .matchParentSize()
                .padding(top = 20.dp, bottom = 20.dp)
                .background(Color.Yellow)
        )
        Box(
            Modifier
                .matchParentSize()
                .padding(40.dp)
                .background(Color.Magenta)
        )
        Box(
            Modifier
                .align(Alignment.Center)
                .size(300.dp, 300.dp)
                .background(Color.Green)
        )
        Box(
            Modifier
                .align(Alignment.TopStart)
                .size(150.dp, 150.dp)
                .background(Color.Red)
        )
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .size(150.dp, 150.dp)
                .background(Color.Blue)
        )
    }
}

//@Preview
@Composable
fun PreviewBoxExample() {
    ToDayTheme {
        BoxExample()
    }
}