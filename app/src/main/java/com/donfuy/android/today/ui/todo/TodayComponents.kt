package com.donfuy.android.today.ui.todo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AutoDelete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.donfuy.android.today.model.TaskItem
import com.donfuy.android.today.ui.SwipeableRow
import com.donfuy.android.today.ui.theme.TodayTheme

// TodoTopBar
@Composable
fun TodayTopBar(
    onClickSettings: () -> Unit,
    onClickBin: () -> Unit
) {
    Column {
        CenterAlignedTopAppBar(
            title = {
                Text("To-day!")
            },
            actions = {
                IconButton(onClick = { onClickSettings() }) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings"
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { onClickBin() }) {
                    Icon(imageVector = Icons.Outlined.AutoDelete, contentDescription = "Bin")
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
        )
        Divider(thickness = Dp.Hairline, color = MaterialTheme.colorScheme.secondary)
    }

}

// TodoList
@Composable
fun TodayList(
    items: List<TaskItem>,
    onItemClicked: (TaskItem) -> Unit,
    setCheck: (TaskItem) -> Unit,
    modifier: Modifier = Modifier,
    onUpdateItem: (TaskItem) -> Unit,
    onDeleteItem: (TaskItem) -> Unit,
    currentEditItemId: Int
) {
    LazyColumn {
        items(items) { task ->
            key(task) {
                if (task.id.toInt() == currentEditItemId) {
                    // Show inline editor row
                    TodayEditRow(
                        onItemSubmit = onUpdateItem,
                        onEmptySubmit = { onDeleteItem(task) },
                        todo = task,
                    )
                } else {
                    TodayRow(
                        todo = task,
                        setCheck = setCheck,
                        onSwipedLeft = { onDeleteItem(task) },
                        onItemClicked = onItemClicked
                    )
                }
                Divider(thickness = Dp.Hairline, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayRow(
    todo: TaskItem,
    setCheck: (TaskItem) -> Unit,
    onSwipedLeft: () -> Unit,
    onItemClicked: (TaskItem) -> Unit
) {
    SwipeableRow(
        onItemClicked = { onItemClicked(todo) },
        onSwipedLeft = onSwipedLeft,
        onSwipedRight = { /*TODO*/ },
        swipeLeftContent = {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .defaultMinSize(minHeight = 24.dp)
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Delete",
                    modifier = Modifier.align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Icon(
                    Icons.Outlined.Delete,
                    "Delete Task",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
        },
        swipeRightContent = { /*TODO*/ }
    ) {
        Column {
            Row(
                modifier = Modifier
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
                    style = MaterialTheme.typography.titleSmall,
                    softWrap = true
                )
            }
        }
    }
}

// TodoEditRow
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TodayEditRow(
    todo: TaskItem, onItemSubmit: (TaskItem) -> Unit, onEmptySubmit: () -> Unit
) {
    // Workaround to set the cursor at the end of the BasicTextField
    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = todo.task,
                selection = TextRange(todo.task.length)
            )
        )
    }
    val (checked, setChecked) = remember { mutableStateOf(todo.checked) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current

    Surface {
        Column {
            Row(
                modifier = Modifier
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
                    value = textFieldValueState,
                    onValueChange = { textFieldValueState = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        if (textFieldValueState.text != "") {
                            onItemSubmit(
                                todo.copy(
                                    task = textFieldValueState.text,
                                    checked = checked
                                )
                            )
                        } else {
                            onEmptySubmit()
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    }),
                    textStyle = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }
            LaunchedEffect(Unit) { focusRequester.requestFocus() }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TodayBottomBar(
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val (text, setText) = remember { mutableStateOf("") }
    val (isFocused, setFocused) = remember { mutableStateOf(false) }
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier
            .height(80.dp)
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            BasicTextField(
                value = text,
                onValueChange = setText,
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(text = "New task", color = MaterialTheme.colorScheme.surfaceTint)
                    }
                    innerTextField()
                },
                textStyle = MaterialTheme.typography.titleSmall,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                keyboardActions = KeyboardActions(onDone = {
                    if (text != "") {
                        onSubmit(text)
                        setText("")
                    } else {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                }),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
                    .align(Alignment.CenterVertically)
                    .focusRequester(focusRequester)
            )
            FloatingActionButton(
                onClick = {
                    if (!isFocused && text.isEmpty()) {
                        focusRequester.requestFocus()
                        setFocused(true)
                    } else {
                        onSubmit(text)
                        setText("")
                    }
                },
                elevation = BottomAppBarDefaults.floatingActionButtonElevation(),
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Icon(Icons.Filled.Add, "Add task")
            }
        }
    }
}

@Preview(widthDp = 412)
@Composable
fun PreviewBottomBarTextField() {
    TodayTheme {
        TodayBottomBar(onSubmit = {})
    }
}

@Composable
fun PreviewTodoList() {
    TodayTheme {

    }
}

@Preview(widthDp = 412)
@Composable
fun PreviewTodoRow() {
    TodayTheme {

    }
}

@Composable
fun PreviewTodoEditRow() {
    TodayTheme {

    }
}