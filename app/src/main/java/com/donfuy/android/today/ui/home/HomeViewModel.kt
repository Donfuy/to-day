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

    /**
     * Updates the home screen UI state by observing task and preference changes.
     *
     * Combines flows for today's tasks, tomorrow's tasks, and user preferences
     * (show completed, move completed to bottom) to create and emit a `HomeUiState`.
     * Task lists are filtered/sorted based on preferences.
     * The tomorrow tab visibility is determined by the presence of tomorrow's tasks.
     */
    private fun updateHomeScreen() {
        viewModelScope.launch {
            combine(
                tasksRepository.todayTasks,
                tasksRepository.tomorrowTasks,
                userPreferencesRepository.showCompleted,
                userPreferencesRepository.completedToBottom
            ) {
                todayTasks, tomorrowTasks, showCompleted, completedToBottom ->
                HomeUiState(
                    todayTasks = todayTasks
                        .showCompleted(showCompleted)
                        .completedToBottom(completedToBottom),
                    tomorrowTasks = tomorrowTasks
                        .showCompleted(showCompleted)
                        .completedToBottom(completedToBottom),
                    showCompleted = showCompleted,
                    completedToBottom = completedToBottom,
                    tabVisible = tomorrowTasks.isNotEmpty()
                )
            }.collect { updatedState ->
                _homeUiState.update { currentState ->
                    updatedState.copy(
                        currentTab = currentState.currentTab
                    )
                }
            }
        }
    }

    fun newTask(task: String, tomorrow: Boolean) {
        val creationDate: Date = Calendar.getInstance().time
        addTask(
            Task(
                task = task,
                createdAt = creationDate,
                lastModifiedAt = null,
                deleteBy = null,
                tomorrow = tomorrow
            )
        )
    }

    fun setCheck(task: Task, checked: Boolean) {
        updateTask(task.copy(checked = checked))
    }

    fun binTask(task: Task) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            // Calculate time 3 days from now
            calendar.add(Calendar.DAY_OF_MONTH, userPreferencesRepository.daysToKeep.first())
            tasksRepository.bin(task, calendar.time)
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
            tasksRepository.insert(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.update(task = task)
            setCurrentEditItemId(-1)
        }
    }

    fun onHomeAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnAddTask -> newTask(action.task, action.tomorrow)
            is HomeAction.OnBinTask -> binTask(action.task)
            is HomeAction.OnUpdateTask -> updateTask(action.task)
            is HomeAction.SetCheck -> setCheck(action.task, action.checked)
            is HomeAction.SetToday -> setToday(action.task)
            is HomeAction.SetTomorrow -> setTomorrow(action.task)
            is HomeAction.OnTabClick -> onTabClick(action.tab)
            is HomeAction.SetTaskEntryVisible -> setTaskEntryVisible(action.visible)
            is HomeAction.OnSwipeLeft -> binTask(action.task)
            is HomeAction.OnSwipeRight -> {
                when {
                    !action.task.tomorrow -> setToday(action.task)
                    action.task.tomorrow -> setTomorrow(action.task)
                }
            }
            is HomeAction.OnTaskClick -> onTaskClick(action.task)
        }
    }

    private fun setCurrentEditItemId(id: Int) {
        viewModelScope.launch { _homeUiState.update { it.copy(currentEditItemId = id) } }
    }

    private fun setTaskEntryVisible(taskEntryVisible: Boolean) {
        viewModelScope.launch { _homeUiState.update { it.copy(taskEntryVisible = taskEntryVisible) } }
    }

    private fun onTabClick(tab: HomeTab) {
        viewModelScope.launch { _homeUiState.update { it.copy(currentTab = tab) } }
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