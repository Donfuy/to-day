package com.donfuy.android.today

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.donfuy.android.today.data.UserPreferencesRepository
import com.donfuy.android.today.ui.bin.BinScreen
import com.donfuy.android.today.ui.home.HomeScreen
import com.donfuy.android.today.ui.settings.SettingsScreen
import com.donfuy.android.today.ui.theme.TodayTheme
import com.donfuy.android.today.workers.scheduleBinCleanup
import com.donfuy.android.today.workers.scheduleTodayCleanup

class MainActivity : ComponentActivity() {

    private val Context.dataStore by preferencesDataStore("settings")

    private val taskViewModel by viewModels<TaskViewModel> {
        TaskViewModel.TaskViewModelFactory(
            (this.applicationContext as BaseApplication).repository,
            UserPreferencesRepository(applicationContext.dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleTodayCleanup(applicationContext)
        scheduleBinCleanup(applicationContext)

        setContent {
            TodayApp(taskViewModel = taskViewModel)
        }
    }
}

@Composable
fun TodayApp(taskViewModel: TaskViewModel) {
    TodayTheme {
        val navController = rememberNavController()
        TodayNavHost(navController = navController, taskViewModel = taskViewModel)
    }
}

@Composable
fun TodayNavHost(
    navController: NavHostController,
    taskViewModel: TaskViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = "today"
    ) {
        composable("today") {
            HomeScreen(
                todayTasksFlow = taskViewModel.todayTasks,
                tomorrowTasksFlow = taskViewModel.tomorrowTasks,
                onAddTask = { task, tomorrow -> taskViewModel.newTask(task, tomorrow) },
                onUpdateTask = taskViewModel::updateTask,
                onBinTask = taskViewModel::binTask,
                setCheck = taskViewModel::setCheck,
                setToday = taskViewModel::setToday,
                setTomorrow = taskViewModel::setTomorrow,
                showCompleted = taskViewModel.showCompleted,
                completedToBottom = taskViewModel.completedToBottom,
                onClickSettings = { navController.navigate("settings") },
                onClickBin = { navController.navigate("bin") },
            )
        }
        composable("settings") {
            SettingsScreen(
                onClickBack = { navController.navigateUp() },
                showCompleted = taskViewModel.showCompleted,
                updateShowCompleted = taskViewModel::updateShowCompleted,
                completedToBottom = taskViewModel.completedToBottom,
                updateCompletedToBottom = taskViewModel::updateCompletedToBottom
            )
        }
        composable("bin") {
            BinScreen(
                tasks = taskViewModel.binTasks,
                onClickBack = { navController.navigateUp() },
                onDeleteTask = taskViewModel::deleteTask,
                onRestoreTask = taskViewModel::restoreTask
            )
        }
    }
}