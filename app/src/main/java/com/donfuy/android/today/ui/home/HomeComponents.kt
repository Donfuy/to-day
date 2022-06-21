package com.donfuy.android.today.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.AutoDelete
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.donfuy.android.today.R
import com.donfuy.android.today.model.Task
import com.donfuy.android.today.ui.today.TodayTaskRow
import kotlinx.coroutines.flow.Flow

@Composable
fun TaskList(
    tasks: List<Task>,
    onItemClicked: (Task) -> Unit,
    setCheck: (Task, Boolean) -> Unit,
    setToday: (Task) -> Unit,
    setTomorrow: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onBinTask: (Task) -> Unit,
    currentEditItemId: Int,
    state: LazyListState,
    showCompletedFlow: Flow<Boolean>,
    setShowCompleted: (Boolean) -> Unit,
    completedToBottomFlow: Flow<Boolean>
) {

    val showCompleted = showCompletedFlow.collectAsState(initial = false).value
    val completedToBottom = completedToBottomFlow.collectAsState(initial = true).value

    LazyColumn(state = state) {
        items(tasks.filter { !it.checked }, key = { it.id }) { task ->
            when {
                task.id.toInt() == currentEditItemId -> {
                    TaskEditRow(
                        onSubmitEdit = onUpdateTask,
                        onEmptySubmit = { onBinTask(task) },
                        task = task,
                    )
                }
                !task.tomorrow -> {
                    TodayTaskRow(
                        task = task,
                        setCheck = { setCheck(task, it) },
                        onSwipeLeft = { onBinTask(task) },
                        onSwipeRight = { setTomorrow(task) },
                        onItemClicked = { onItemClicked(task) }
                    )
                }
                else -> {
                    TomorrowTaskRow(
                        task = task,
                        setCheck = { setCheck(task, it) },
                        onItemClicked = { onItemClicked(task) },
                        onSwipeLeft = { setToday(task) },
                        onSwipeRight = { onBinTask(task) }
                    )
                }
            }
//            Divider(thickness = Dp.Hairline, color = MaterialTheme.colorScheme.outline)
        }
        if (completedToBottom && !tasks.none { it.checked }) {
            item {
                ShowCompletedButton(
                    showCompleted = showCompleted,
                    setShowCompleted = setShowCompleted
                )
            }
            // TODO: AnimatedVisibility

            if (showCompleted) {
                items(tasks.filter { it.checked }, key = { it.id }) { task ->
                    when {
                        task.id.toInt() == currentEditItemId -> {
                            TaskEditRow(
                                onSubmitEdit = onUpdateTask,
                                onEmptySubmit = { onBinTask(task) },
                                task = task,
                            )
                        }
                        !task.tomorrow -> {
                            TodayTaskRow(
                                task = task,
                                setCheck = { setCheck(task, it) },
                                onSwipeLeft = { onBinTask(task) },
                                onSwipeRight = { setTomorrow(task) },
                                onItemClicked = { onItemClicked(task) }
                            )
                        }
                        else -> {
                            TomorrowTaskRow(
                                task = task,
                                setCheck = { setCheck(task, it) },
                                onItemClicked = { onItemClicked(task) },
                                onSwipeLeft = { onBinTask(task) },
                                onSwipeRight = { setToday(task)  }
                            )
                        }
                    }
//                    Divider(thickness = Dp.Hairline, color = MaterialTheme.colorScheme.outline)
                }
            }
        }

    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ShowCompletedButton(
    showCompleted: Boolean,
    setShowCompleted: (Boolean) -> Unit,
) {

    Surface(Modifier.clickable { setShowCompleted(!showCompleted) }) {
        Column {
            Divider(thickness = Dp.Hairline, color = MaterialTheme.colorScheme.secondary)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Completed",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                AnimatedContent(targetState = showCompleted) { showCompleted ->
                    if (showCompleted) {
                        Icon(
                            imageVector = Icons.Filled.ExpandLess,
                            contentDescription = "Hide Completed Tasks",
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.ExpandMore,
                            contentDescription = "Show Completed Tasks"
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun HomeTopBar(
    onClickSettings: () -> Unit, onClickBin: () -> Unit
) {
    Column {
        CenterAlignedTopAppBar(
            title = {
                Text(stringResource(id = R.string.home_screen_title))
            }, actions = {
                IconButton(onClick = { onClickSettings() }) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(id = R.string.settings_button_content_description)
                    )
                }
            }, navigationIcon = {
                IconButton(onClick = { onClickBin() }) {
                    Icon(
                        imageVector = Icons.Outlined.AutoDelete,
                        contentDescription = stringResource(id = R.string.bin_button_content_description)
                    )
                }
            }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
        )
        Divider(thickness = Dp.Hairline, color = MaterialTheme.colorScheme.secondary)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TaskEntryBottomBar(
    onSubmit: (String) -> Unit,
    taskEntryFocusRequester: FocusRequester,
    onCloseClick: () -> Unit
) {
    val (text, setText) = rememberSaveable { mutableStateOf("") }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
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
                        onCloseClick()
                    }
                }),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 8.dp)
                    .focusRequester(taskEntryFocusRequester)
                    .align(Alignment.CenterVertically)
            )
            SmallFloatingActionButton(
                onClick = {
                    if (text.isEmpty()) {
                        onCloseClick()
                    } else {
                        onSubmit(text)
                        setText("")
                    }
                },
                elevation = BottomAppBarDefaults.floatingActionButtonElevation(),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                AnimatedContent(targetState = text.isEmpty()) { isEmpty ->
                    if (!isEmpty) {
                        Icon(
                            Icons.Filled.Add,
                            stringResource(id = R.string.add_task_content_description)
                        )
                    } else {
                        Icon(Icons.Filled.Close, "Dismiss keyboard")
                    }
                }
            }
        }
    }
    taskEntryFocusRequester.requestFocus()
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskEditRow(
    task: Task, onSubmitEdit: (Task) -> Unit, onEmptySubmit: () -> Unit
) {
    // Workaround for the workaround not being able to be rememberSaveable
    val (text, setText) = rememberSaveable { mutableStateOf(task.task) }

    // Workaround to set the cursor at the end of the BasicTextField
    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = text, selection = TextRange(task.task.length)
            )
        )
    }
    val (checked, setChecked) = remember { mutableStateOf(task.checked) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current

    // Access lifecycle events to ensure unsubmitted text doesn't get lost.
    // This should be moved to an extension function at a later date.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                if (textFieldValueState.text != "") {
                    onSubmitEdit(task.copy(task = textFieldValueState.text, checked = checked))
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
                    onValueChange = {
                        textFieldValueState = it
                        setText(it.text)
                    },
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

@Suppress("unused")
private const val TAG = "HomeComponents"
