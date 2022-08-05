package com.donfuy.android.today.ui.settings

import android.app.TimePickerDialog
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.donfuy.android.today.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onClickBack: () -> Unit,
    showCompleted: Flow<Boolean>,
    updateShowCompleted: (Boolean) -> Unit,
    completedToBottom: Flow<Boolean>,
    updateCompletedToBottom: (Boolean) -> Unit,
    useDynamicTheme: Flow<Boolean>,
    updateUseDynamicTheme: (Boolean) -> Unit,
    hourToDeleteTasks: Flow<Int>,
    updateHourToDeleteTasks: (Int) -> Unit,
    minToDeleteTasks: Flow<Int>,
    updateMinToDeleteTasks: (Int) -> Unit,
    restartApp: () -> Unit
) {
    val showCompletedValue = showCompleted.collectAsState(initial = false).value
    val completedToBottomValue = completedToBottom.collectAsState(initial = true).value
    val initialUseDynamicThemeValue = useDynamicTheme.collectAsState(initial = false).value
    val hourToDeleteTasksValue = hourToDeleteTasks.collectAsState(initial = 2).value
    val minToDeleteTasksValue = minToDeleteTasks.collectAsState(initial = 0).value

    val openDialog = remember { mutableStateOf(false) }

    RestartAppAlertDialog(
        openDialog = openDialog,
        confirmOnClick = {
            restartApp()
        },
        dismissOnClick = { openDialog.value = false }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.settings_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        onClickBack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.settings_back_content_description)
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            SwitchRow(
                title = stringResource(id = R.string.setting_show_completed_tasks_title),
                description = stringResource(id = R.string.setting_show_completed_tasks_description),
                checked = showCompletedValue,
                setCheck = updateShowCompleted
            )
            SwitchRow(
                title = stringResource(id = R.string.setting_move_completed_tasks_to_bottom_title),
                description = stringResource(id = R.string.setting_move_completed_tasks_to_bottom_description),
                checked = completedToBottomValue,
                setCheck = updateCompletedToBottom
            )
            TimePickerRow(
                title = stringResource(R.string.setting_move_to_bin_time_title),
                description = stringResource(R.string.setting_move_to_bin_time_description),
                currentHour = hourToDeleteTasksValue,
                currentMinute = minToDeleteTasksValue,
                updateHour = updateHourToDeleteTasks,
                updateMinute = updateMinToDeleteTasks
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                SwitchRow(
                    title = stringResource(R.string.setting_use_dynamic_theme_title),
                    description = stringResource(R.string.setting_use_dynamic_theme_description),
                    checked = initialUseDynamicThemeValue,
                    setCheck = {
                        updateUseDynamicTheme(it)
                        openDialog.value = true
                    }
                )
            }
        }
    }
}


@Composable
fun SwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    setCheck: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
        ) {
            Text(
                text = title,
                softWrap = true,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelMedium,
                softWrap = true,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = setCheck,
            modifier = Modifier
                .padding(start = 16.dp)
        )
    }
}

//@Preview
//@Composable
//fun PreviewSwitchRow() {
//    TodayTheme(useDynamicColorScheme = true) {
//        Column {
//            SwitchRow(title = "Title", description = "Description", checked = false, setCheck = {})
//            SwitchRow(title = "Title", description = "Description", checked = true, setCheck = {})
//        }
//    }
//}

@Composable
fun TimePickerRow(
    title: String,
    description: String,
    currentHour: Int,
    currentMinute: Int,
    updateHour: (Int) -> Unit,
    updateMinute: (Int) -> Unit
) {
    val timePicker = TimePickerDialog(
        LocalContext.current,
        {_, hourSelection: Int, minuteSelection: Int ->
            updateHour(hourSelection)
            updateMinute(minuteSelection)
        },
        currentHour,
        currentMinute,
        true
    )
    
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { timePicker.show() }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
        ) {
            Text(
                text = title,
                softWrap = true,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                softWrap = true
            )
        }
        Text(
            text = "$currentHour:$currentMinute",
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp)
        )
    }
}

//@Preview
//@Composable
//fun PreviewTimePickerRow() {
//    TodayTheme(useDynamicColorScheme = true) {
//        Column {
//            TimePickerRow(
//                title = "Time to bin tasks",
//                description = "Description",
//                currentHour = 20,
//                currentMinute = 10,
//                updateHour = {},
//                updateMinute = {}
//            )
//        }
//    }
//}

@Composable
fun RestartAppAlertDialog(
    openDialog: MutableState<Boolean>,
    confirmOnClick: () -> Unit,
    dismissOnClick: () -> Unit
) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(text = stringResource(R.string.dialog_required_restart_title))
            },
            text = { Text(text = stringResource(R.string.dialog_required_restart_prompt)) },
            confirmButton = {
                TextButton(onClick = {
                    openDialog.value = false
                    confirmOnClick()
                }) {
                    Text(stringResource(R.string.dialog_required_restart_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    openDialog.value = false
                    dismissOnClick()
                }) {
                    Text(stringResource(R.string.dialog_required_restart_later))
                }
            }
        )
    }
}

@Suppress("unused")
private const val TAG = "SettingsScreen"