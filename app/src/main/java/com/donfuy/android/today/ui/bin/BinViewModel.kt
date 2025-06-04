package com.donfuy.android.today.ui.bin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donfuy.android.today.data.TasksRepository
import com.donfuy.android.today.data.UserPreferencesRepository
import com.donfuy.android.today.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BinViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(BinUiState())
    val uiState: StateFlow<BinUiState>
        get() = _uiState

    init {
        viewModelScope.launch {
            tasksRepository.binTasks.collect {
                _uiState.value = BinUiState(binTasks = it)
            }
        }
    }


}

data class BinUiState(
    val binTasks: List<Task> = listOf()
)
