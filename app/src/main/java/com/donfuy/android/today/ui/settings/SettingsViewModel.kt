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

}

data class SettingsUiState(
    val showCompleted: Boolean = true,
    val completedToBottom: Boolean = true,
    val useDynamicTheme: Boolean = false,
    val hourToDeleteTasks: Int = 3,
    val minToDeleteTasks: Int = 0
)