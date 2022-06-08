package com.donfuy.android.today.ui.settings

import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.donfuy.android.today.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow

private const val TAG = "SettingsScreen"

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
    restartApp: () -> Unit
) {
    val showCompletedValue = showCompleted.collectAsState(initial = false).value
    val completedToBottomValue = completedToBottom.collectAsState(initial = true).value
    val initialUseDynamicThemeValue = useDynamicTheme.collectAsState(initial = false).value

    val openDialog = remember { mutableStateOf(false) }

    RestartAppAlertDialog(
        openDialog = openDialog,
        confirmOnClick = {
            Log.d(TAG, "Wtf")
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
        Text(
            text = title,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f),
            softWrap = true,
        )
        Switch(checked = checked, onCheckedChange = setCheck)
    }
}

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
