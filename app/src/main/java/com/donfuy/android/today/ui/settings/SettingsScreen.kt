package com.donfuy.android.today.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import com.donfuy.android.today.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    updateCompletedToBottom: (Boolean) -> Unit
) {
    val showCompletedValue = showCompleted.collectAsState(initial = false).value
    val completedToBottomValue = completedToBottom.collectAsState(initial = true).value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.settings_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
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

