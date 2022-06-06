package com.donfuy.android.today.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AutoDelete
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.donfuy.android.today.R
import com.donfuy.android.today.model.Task
import com.donfuy.android.today.ui.today.TodayTaskRow

@Composable
fun HomeLazyList(
    items: List<Task>,
    onItemClicked: (Task) -> Unit,
    setCheck: (Task, Boolean) -> Unit,
    setToday: (Task) -> Unit,
    setTomorrow: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onBinTask: (Task) -> Unit,
    currentEditItemId: Int,
    state: LazyListState
) {

    LazyColumn(state = state) {
        items(items) { task ->
            key(task) {
                if (task.id.toInt() == currentEditItemId) {
                    TaskEditRow(
                        onSubmitEdit = onUpdateTask,
                        onEmptySubmit = { onBinTask(task) },
                        task = task,
                    )
                } else {
                    if (task.tomorrow) {
                        TomorrowTaskRow(task = task,
                            setCheck = { setCheck(task, it) },
                            onItemClicked = { onItemClicked(task) },
                            onSwipeLeft = { setToday(task) },
                            onSwipeRight = { onBinTask(task) })
                    } else {
                        TodayTaskRow(task = task,
                            setCheck = { setCheck(task, it) },
                            onSwipeLeft = { onBinTask(task) },
                            onSwipeRight = { setTomorrow(task) },
                            onItemClicked = { onItemClicked(task) })
                    }
                }
                Divider(thickness = Dp.Hairline, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun HomeTopBar(
    onClickSettings: () -> Unit, onClickBin: () -> Unit
) {
    Column {
        CenterAlignedTopAppBar(title = {
            Text(stringResource(id = R.string.home_screen_title))
        }, actions = {
            IconButton(onClick = { onClickSettings() }) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(
                        id = R.string.settings_button_content_description
                    )
                )
            }
        }, navigationIcon = {
            IconButton(onClick = { onClickBin() }) {
                Icon(
                    imageVector = Icons.Outlined.AutoDelete,
                    contentDescription = stringResource(
                        id = R.string.bin_button_content_description
                    )
                )
            }
        }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
        )
        Divider(thickness = Dp.Hairline, color = MaterialTheme.colorScheme.secondary)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BottomBarFlex(
    onSubmit: (String) -> Unit
) {
    val (text, setText) = remember { mutableStateOf("") }
    val (isFocused, setFocused) = remember { mutableStateOf(false) }
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var size by remember { mutableStateOf(IntSize.Zero) }
    val width = size.width - 300
    BottomAppBar(
        icons = {
            Surface {
                BasicTextField(
                    value = text,
                    onValueChange = setText,
                    decorationBox = { innerTextField ->
                        if (text.isEmpty()) {
                            Text(text = stringResource(id = R.string.new_task_hint), color = MaterialTheme.colorScheme.surfaceTint)
                        }
                        innerTextField()
                    },
                    textStyle = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
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
                        .padding(start = 16.dp, end = 16.dp)
                        .width(with(LocalDensity.current) { width.toDp() })
                        .align(Alignment.CenterVertically)
                        .focusRequester(focusRequester)
                )
            }
        },
        floatingActionButton = {
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
                Icon(Icons.Filled.Add, stringResource(id = R.string.add_task_content_description))
            }
        },
        modifier = Modifier.onSizeChanged { size = it }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeBottomBar(
    onSubmit: (String) -> Unit
) {
    val (text, setText) = remember { mutableStateOf("") }
    val (isFocused, setFocused) = remember { mutableStateOf(false) }
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.height(80.dp)
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            BasicTextField(
                value = text,
                onValueChange = setText,
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.new_task_hint),
                            color = MaterialTheme.colorScheme.surfaceTint
                        )
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
                Icon(Icons.Filled.Add, stringResource(id = R.string.add_task_content_description))
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskEditRow(
    task: Task, onSubmitEdit: (Task) -> Unit, onEmptySubmit: () -> Unit
) {
    // Workaround to set the cursor at the end of the BasicTextField
    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = task.task, selection = TextRange(task.task.length)
            )
        )
    }
    val (checked, setChecked) = remember { mutableStateOf(task.checked) }
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
                            onSubmitEdit(
                                task.copy(
                                    task = textFieldValueState.text, checked = checked
                                )
                            )
                        } else {
                            onEmptySubmit()
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    }),
                    textStyle = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
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

