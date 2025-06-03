package com.donfuy.android.today

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                userPreferencesRepository.hourToDeleteTasks.collect { hourToCleanup ->
                    userPreferencesRepository.minToDeleteTasks.collect { minuteToCleanup ->
                        scheduleTodayCleanup(applicationContext, hourToCleanup, minuteToCleanup)
                    }
                }
            }
        }

        scheduleBinCleanup(applicationContext)

        setContent {
            TodayApp { restartApp() }
        }
    }

    private fun restartApp() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}



@Composable
fun TodayApp(restartApp: () -> Unit) {
    val taskViewModel: TaskViewModel = viewModel()

    TodayTheme(useDynamicColorScheme = runBlocking { taskViewModel.useDynamicTheme.first() }) {
        val navController = rememberNavController()
        Surface {
            TodayNavHost(
                navController = navController,
                taskViewModel = taskViewModel,
                restartApp = restartApp
            )
        }
    }
}

@Composable
fun TodayNavHost(
    navController: NavHostController,
    taskViewModel: TaskViewModel,
    restartApp: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE
    ) {
        composable(HOME_ROUTE) {
            val uiState by taskViewModel.homeUiState.collectAsStateWithLifecycle()
            HomeScreen(
                uiState = uiState,
                onClickSettings = { navController.navigate(SETTINGS_ROUTE) },
                onClickBin = { navController.navigate(BIN_ROUTE) },
                onAction = taskViewModel::onHomeAction
            )
        }
        composable(SETTINGS_ROUTE) {
            SettingsScreen(
                showCompleted = taskViewModel.showCompleted,
                completedToBottom = taskViewModel.completedToBottom,
                useDynamicTheme = taskViewModel.useDynamicTheme,
                hourToDeleteTasks = taskViewModel.hourToDeleteTasks,
                minToDeleteTasks = taskViewModel.minToDeleteTasks,
                onAction = taskViewModel::onSettingsAction,
                onBackClick = { navController.navigateUp() },
                onRestartApp = restartApp
            )
        }
        composable(BIN_ROUTE) {
            BinScreen(
                tasks = taskViewModel.binTasks,
                onBackClick = { navController.navigateUp() },
                onAction = taskViewModel::onBinAction
            )
        }
    }
}

private const val SETTINGS_ROUTE = "settings"
private const val BIN_ROUTE = "bin"
private const val HOME_ROUTE = "home"

@Suppress("unused")
private const val TAG = "MainActivity"