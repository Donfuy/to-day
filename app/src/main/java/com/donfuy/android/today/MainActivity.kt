package com.donfuy.android.today

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.donfuy.android.today.ui.theme.TodayTheme
import com.donfuy.android.today.ui.todo.TodayScreen
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.donfuy.android.today.data.UserPreferencesRepository
import com.donfuy.android.today.ui.bin.BinScreen
import com.donfuy.android.today.ui.settings.SettingsScreen

class MainActivity : ComponentActivity() {

    private val Context.dataStore by preferencesDataStore("settings")

    private val taskViewModel by viewModels<TaskViewModel> {
        TaskViewModel.TodoViewModelFactory(
            (this.applicationContext as BaseApplication).repository,
            UserPreferencesRepository(applicationContext.dataStore)
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "today"
    ) {
        composable("today") {
            TodayScreen(
                todayTasks = taskViewModel.todayTasks,
                tomorrowTasks = taskViewModel.tomorrowTasks,
                onAddItem = { task, tomorrow -> taskViewModel.newTask(task, tomorrow) },
                onUpdateItem = taskViewModel::updateItem,
                onDeleteItem = taskViewModel::recycleItem,
                onClickSettings = { navController.navigate("settings") },
                onClickBin = { navController.navigate("bin") },
                setCheck = taskViewModel::setCheck,
                showCompleted = taskViewModel.showCompleted,
                completedToBottom = taskViewModel.completedToBottom
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
                onDeleteItem = taskViewModel::deleteItem,
            )
        }
    }
}