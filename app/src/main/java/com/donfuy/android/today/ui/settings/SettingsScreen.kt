package com.donfuy.android.today.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onClickBack: () -> Unit,
    showCompleted: Flow<Boolean>,
    updateShowCompleted: (Boolean) -> Unit
) {
    val showCompletedState = showCompleted.collectAsState(initial = false)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back to Today screen"
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            SwitchRow(title = "Show completed tasks", description = "Description", checked = showCompletedState.value, setCheck = updateShowCompleted)
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
    Row(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        Text(
            text = title,
            modifier = Modifier.align(Alignment.CenterVertically).weight(1f),
            softWrap = true,
        )
        Switch(checked = checked, onCheckedChange = setCheck)
    }
}

