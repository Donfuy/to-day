package com.donfuy.android.today

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.donfuy.android.today.ui.bin.BinScreen
import com.donfuy.android.today.ui.home.HomeScreen
import com.donfuy.android.today.ui.settings.SettingsScreen
import com.donfuy.android.today.ui.theme.TodayTheme
import com.donfuy.android.today.workers.scheduleBinCleanup
import com.donfuy.android.today.workers.scheduleTodayCleanup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleTodayCleanup(applicationContext)
        scheduleBinCleanup(applicationContext)

        setContent {
            TodayApp()
        }
    }
}

@Composable
fun TodayApp() {
    val taskViewModel: TaskViewModel = viewModel()
    TodayTheme {
        val navController = rememberNavController()
        Surface {
            TodayNavHost(navController = navController, taskViewModel = taskViewModel)
        }
    }
}

@Composable
fun TodayNavHost(
    navController: NavHostController,
    taskViewModel: TaskViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE
    ) {
        composable(HOME_ROUTE) {
            HomeScreen(
                todayTasksFlow = taskViewModel.todayTasks,
                tomorrowTasksFlow = taskViewModel.tomorrowTasks,
                onAddTask = { task, tomorrow -> taskViewModel.newTask(task, tomorrow) },
                onUpdateTask = taskViewModel::updateTask,
                onBinTask = taskViewModel::binTask,
                setCheck = taskViewModel::setCheck,
                setToday = taskViewModel::setToday,
                setTomorrow = taskViewModel::setTomorrow,
                showCompletedFlow = taskViewModel.showCompleted,
                completedToBottomFlow = taskViewModel.completedToBottom,
                onClickSettings = { navController.navigate(SETTINGS_ROUTE) },
                onClickBin = { navController.navigate(BIN_ROUTE) },
            )
        }
        composable(SETTINGS_ROUTE) {
            SettingsScreen(
                onClickBack = { navController.navigateUp() },
                showCompleted = taskViewModel.showCompleted,
                updateShowCompleted = taskViewModel::updateShowCompleted,
                completedToBottom = taskViewModel.completedToBottom,
                updateCompletedToBottom = taskViewModel::updateCompletedToBottom
            )
        }
        composable(BIN_ROUTE) {
            BinScreen(
                tasks = taskViewModel.binTasks,
                onClickBack = { navController.navigateUp() },
                onDeleteTask = taskViewModel::deleteTask,
                onRestoreTask = taskViewModel::restoreTask
            )
        }
    }
}

const val SETTINGS_ROUTE = "settings"
const val BIN_ROUTE = "bin"
const val HOME_ROUTE = "home"

private const val PREFS_DATA_STORE_NAME = "settings"