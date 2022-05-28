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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
            (this.applicationContext as BaseApplication).database.taskItemDao(),
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
                items = taskViewModel.todayItems,
                onAddItem = taskViewModel::addItem,
                onUpdateItem = taskViewModel::updateItem,
                onDeleteItem = taskViewModel::recycleItem,
                onClickSettings = { navController.navigate("settings") },
                onClickBin = { navController.navigate("bin") },
                setCheck = taskViewModel::setCheck
            )
        }
        composable("settings") {
            SettingsScreen(
                onClickBack = { navController.navigateUp() },
                showCompleted = taskViewModel.showCompleted,
                updateShowCompleted = taskViewModel::updateShowCompleted
            )
        }
        composable("bin") {
            BinScreen(
                tasks = taskViewModel.binItems,
                onClickBack = { navController.navigateUp() },
                onDeleteItem = taskViewModel::deleteItem,
            )
        }
    }
}