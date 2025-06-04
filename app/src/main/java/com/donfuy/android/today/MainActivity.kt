package com.donfuy.android.today

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.donfuy.android.today.data.UserPreferencesRepository
import com.donfuy.android.today.ui.bin.BinScreen
import com.donfuy.android.today.ui.bin.BinViewModel
import com.donfuy.android.today.ui.home.HomeScreen
import com.donfuy.android.today.ui.home.HomeViewModel
import com.donfuy.android.today.ui.settings.SettingsScreen
import com.donfuy.android.today.ui.settings.SettingsViewModel
import com.donfuy.android.today.ui.theme.TodayTheme
import com.donfuy.android.today.workers.scheduleTodayCleanup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                combine(
                    userPreferencesRepository.hourToDeleteTasks,
                    userPreferencesRepository.minToDeleteTasks
                ) { hour, min ->
                    Pair(hour, min)
                }.collect {
                    scheduleTodayCleanup(applicationContext, it.first, it.second)
                }
            }
        }

        setContent {
            TodayApp(userPreferencesRepository)
        }
    }

    private fun restartApp() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

@Composable
fun TodayApp(
    userPreferencesRepository: UserPreferencesRepository
) {
    val useDynamicTheme by userPreferencesRepository.useDynamicTheme.collectAsStateWithLifecycle(initialValue = false)

    TodayTheme(useDynamicColorScheme = useDynamicTheme) {
        val navController = rememberNavController()
        Surface {
            TodayNavHost(navController = navController)
        }
    }
}

@Composable
fun TodayNavHost(
    navController: NavHostController
) {
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val settingsViewModel = hiltViewModel<SettingsViewModel>()
    val binViewModel = hiltViewModel<BinViewModel>()

    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE
    ) {
        composable(HOME_ROUTE) {
            val uiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()
            HomeScreen(
                uiState = uiState,
                onClickSettings = { navController.navigate(SETTINGS_ROUTE) },
                onClickBin = { navController.navigate(BIN_ROUTE) },
                onAction = homeViewModel::onHomeAction
            )
        }
        composable(SETTINGS_ROUTE) {
            val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
            SettingsScreen(
                uiState = uiState,
                onAction = settingsViewModel::onSettingsAction,
                onBackClick = { navController.navigateUp() }
            )
        }
        composable(BIN_ROUTE) {
            val uiState by binViewModel.uiState.collectAsStateWithLifecycle()
            BinScreen(
                uiState = uiState,
                onBackClick = { navController.navigateUp() },
                onAction = binViewModel::onBinAction
            )
        }
    }
}

private const val SETTINGS_ROUTE = "settings"
private const val BIN_ROUTE = "bin"
private const val HOME_ROUTE = "home"

@Suppress("unused")
private const val TAG = "MainActivity"