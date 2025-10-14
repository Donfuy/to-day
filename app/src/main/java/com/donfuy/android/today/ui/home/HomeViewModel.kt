package com.donfuy.android.today.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donfuy.android.today.data.TasksRepository
import com.donfuy.android.today.data.UserPreferencesRepository
import com.donfuy.android.today.model.Task
import com.donfuy.android.today.ui.HomeTab
import com.donfuy.android.today.ui.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

// Helper data class for updateHomeScreen
private data class HomeScreenFlowInputs(
    val todayTasks: List<Task>,
    val tomorrowTasks: List<Task>,
    val showCompleted: Boolean,
    val completedToBottom: Boolean
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState>
        get() = _homeUiState
    
    init {
        updateHomeScreen()
    }

    private fun updateHomeScreen() {
        viewModelScope.launch {
            combine(
                tasksRepository.todayTasks,
                tasksRepository.tomorrowTasks,
                userPreferencesRepository.showCompleted,
                userPreferencesRepository.completedToBottom
            ) { today, tomorrow, show, completedBottom ->
                HomeScreenFlowInputs(today, tomorrow, show, completedBottom)
            }.collect { inputs -> 
                _homeUiState.update { currentState ->
                    val newTodayTasks = inputs.todayTasks
                        .showCompleted(inputs.showCompleted)
                        .completedToBottom(inputs.completedToBottom)
                    val newTomorrowTasks = inputs.tomorrowTasks
                        .showCompleted(inputs.showCompleted)
                        .completedToBottom(inputs.completedToBottom)
                    val newTabVisible = newTomorrowTasks.isNotEmpty()

                    currentState.copy(
                        todayTasks = newTodayTasks,
                        tomorrowTasks = newTomorrowTasks,
                        showCompleted = inputs.showCompleted,
                        completedToBottom = inputs.completedToBottom,
                        tabVisible = newTabVisible,
                        currentTab = if (newTabVisible) currentState.currentTab else HomeTab.TODAY
                    )
                }
            }
        }
    }

    fun newTask() {
        val creationDate: Date = Calendar.getInstance().time
        addTask(
            Task(
                task = "",
                createdAt = creationDate,
                lastModifiedAt = null,
                deleteBy = null,
                tomorrow = _homeUiState.value.currentTab == HomeTab.TOMORROW
            )
        )
    }

    fun setCheck(task: Task, checked: Boolean) {
        updateTask(task.copy(checked = checked))
    }

    fun binTask(task: Task) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, userPreferencesRepository.daysToKeep.first())
            tasksRepository.bin(task, calendar.time)
            setCurrentEditItemId(-1)
        }
    }

    fun setTomorrow(task: Task) {
        updateTask(task = task.copy(tomorrow = true))
    }

    fun setToday(task: Task) {
        updateTask(task = task.copy(tomorrow = false))
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            val id = tasksRepository.insert(task)
            setCurrentEditItemId(id.toInt())
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.update(task = task)
        }
    }

    private fun onSubmitTask(task: Task) {
        updateTask(task)
        newTask()
    }

    fun onHomeAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnAddTask -> newTask()
            is HomeAction.OnBinTask -> binTask(action.task) 
            is HomeAction.OnSubmitTask -> onSubmitTask(action.task)
            is HomeAction.SetCheck -> setCheck(action.task, action.checked)
            is HomeAction.SetToday -> setToday(action.task)
            is HomeAction.SetTomorrow -> setTomorrow(action.task)
            is HomeAction.OnTabClick -> onTabClick(action.tab)
            is HomeAction.OnSwipeLeft -> binTask(action.task)
            is HomeAction.OnSwipeRight -> {
                when {
                    !action.task.tomorrow -> setTomorrow(action.task)
                    action.task.tomorrow -> setToday(action.task)
                }
            }
            is HomeAction.OnTaskClick -> onTaskClick(action.task)
        }
    }

    private fun setCurrentEditItemId(id: Int) {
        viewModelScope.launch { _homeUiState.update { it.copy(currentEditItemId = id) } }
    }

    private fun onTabClick(tab: HomeTab) {
        viewModelScope.launch { _homeUiState.update { it.copy(currentTab = tab, currentEditItemId = -1) } }
    }

    private fun onTaskClick(task: Task) {
        setCurrentEditItemId(task.id.toInt())
    }
}

private fun List<Task>.showCompleted(showCompleted: Boolean): List<Task> {
    return if (!showCompleted) {
        this.filter { !it.checked }
    } else {
        this
    }
}

private fun List<Task>.completedToBottom(completedToBottom: Boolean): List<Task> {
    return if (completedToBottom) {
        this.sortedBy { it.checked }
    } else {
        this
    }
}

@Suppress("unused")
private const val TAG = "TaskViewModel"