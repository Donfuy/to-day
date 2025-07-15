package com.donfuy.android.today.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.donfuy.android.today.R
import com.donfuy.android.today.ui.HomeTab
import com.donfuy.android.today.ui.HomeUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onClickSettings: () -> Unit,
    onClickBin: () -> Unit,
    onAction: (HomeAction) -> Unit
) {
    val homeListState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            HomeTopBar(
                onClickSettings = onClickSettings,
                onClickBin = onClickBin,
                tomorrowVisible = uiState.tabVisible,
                currentTab = uiState.currentTab,
                onAction = onAction
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                // Show FAB if not in edit mode (no item is currently being edited)
                visible = uiState.currentEditItemId == -1,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                HomeFAB(onClick = { onAction(HomeAction.OnAddTask) })
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                TaskList(
                    tasks = if ((uiState.currentTab == HomeTab.TODAY) || !uiState.tabVisible) {
                        uiState.todayTasks
                    } else {
                        uiState.tomorrowTasks
                    },
                    onItemClicked = {
                        onAction(HomeAction.OnTaskClick(it))
                    },
                    setCheck = { task, checked ->
                        onAction(HomeAction.SetCheck(task, checked))
                    },
                    onSubmitTask = {
                        onAction(HomeAction.OnSubmitTask(it))
                    },
                    onBinTask = { onAction(HomeAction.OnBinTask(it)) },
                    currentEditItemId = uiState.currentEditItemId,
                    state = homeListState,
                    onSwipeLeft = { onAction(HomeAction.OnSwipeLeft(it)) },
                    onSwipeRight = { onAction(HomeAction.OnSwipeRight(it)) }
                )
            }

        }
    }
}

@Composable
fun HomeFAB(onClick: () -> Unit) {
        LargeFloatingActionButton(
            onClick =  onClick,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                Icons.Filled.Add,
                stringResource(id = R.string.add_task_content_description),
                modifier = Modifier.size(36.dp)
            )
        }
}

@Suppress("unused")
private const val TAG = "HomeScreen"
