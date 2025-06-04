package com.donfuy.android.today.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donfuy.android.today.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                userPreferencesRepository.showCompleted,
                userPreferencesRepository.completedToBottom,
                userPreferencesRepository.useDynamicTheme,
                userPreferencesRepository.hourToDeleteTasks,
                userPreferencesRepository.minToDeleteTasks

            ) { showCompleted, completedToBottom, useDynamicTheme, hourToDeleteTasks, minToDeleteTasks ->
                SettingsUiState(
                    showCompleted = showCompleted,
                    completedToBottom = completedToBottom,
                    useDynamicTheme = useDynamicTheme,
                    hourToDeleteTasks = hourToDeleteTasks,
                    minToDeleteTasks = minToDeleteTasks
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun updateCompletedToBottom(completedToBottom: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateCompletedToBottom(completedToBottom)
        }
    }

    fun updateUseDynamicTheme(useDynamicTheme: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateUseDynamicTheme(useDynamicTheme)
        }
    }

    fun updateHourToDeleteTasks(hourToDeleteTasks: Int) {
        viewModelScope.launch {
            userPreferencesRepository.updateHourToDeleteTasks(hourToDeleteTasks)
        }
    }

    fun updateMinToDeleteTasks(minToDeleteTasks: Int) {
        viewModelScope.launch {
            userPreferencesRepository.updateMinToDeleteTasks(minToDeleteTasks)
        }
    }

    fun updateShowCompleted(showCompleted: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateShowCompleted(showCompleted)
        }
    }

    fun onSettingsAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnToggleShowCompleted -> updateShowCompleted(action.showCompleted)
            is SettingsAction.OnToggleCompletedToBottom -> updateCompletedToBottom(action.completedToBottom)
            is SettingsAction.OnToggleDynamicTheme -> updateUseDynamicTheme(action.useDynamicTheme)
            is SettingsAction.OnUpdateHourToDeleteTasks -> updateHourToDeleteTasks(action.hourToDeleteTasks)
            is SettingsAction.OnUpdateMinToDeleteTasks -> updateMinToDeleteTasks(action.minToDeleteTasks)
        }
    }

}

data class SettingsUiState(
    val showCompleted: Boolean = true,
    val completedToBottom: Boolean = true,
    val useDynamicTheme: Boolean = false,
    val hourToDeleteTasks: Int = 3,
    val minToDeleteTasks: Int = 0
)