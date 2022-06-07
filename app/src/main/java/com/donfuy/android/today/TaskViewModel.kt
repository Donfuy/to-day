package com.donfuy.android.today

import android.util.Log
import androidx.lifecycle.*
import com.donfuy.android.today.data.TasksRepository
import com.donfuy.android.today.data.UserPreferencesRepository
import com.donfuy.android.today.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

private const val TAG = "TaskViewModel"

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val showCompleted: Flow<Boolean> = userPreferencesRepository.showCompletedFlow
    val completedToBottom: Flow<Boolean> = userPreferencesRepository.completedToBottom

    val daysToKeepTasks = userPreferencesRepository.daysToKeepSync

    val todayTasks: Flow<List<Task>> = tasksRepository.todayTasks
    val tomorrowTasks: Flow<List<Task>> = tasksRepository.tomorrowTasks
    val binTasks: Flow<List<Task>> = tasksRepository.binTasks


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
        val calendar = Calendar.getInstance()
        Log.d(TAG, "Days to keep tasks: $daysToKeepTasks")
        // Calculate time 3 days from now
        calendar.add(Calendar.DAY_OF_MONTH, daysToKeepTasks)

        updateTask(
            task = task.copy(
                binned = true,
                deleteBy = calendar.time
            )
        )
    }

    fun restoreTask(task: Task) {
        updateTask(task = task.copy(binned = false))
    }

    fun setTomorrow(task: Task) {
        updateTask(task = task.copy(tomorrow = true))
    }

    fun setToday(task: Task) {
        updateTask(task = task.copy(tomorrow = false))
    }

    fun updateShowCompleted(showCompleted: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateShowCompleted(showCompleted)
        }
    }

    fun updateCompletedToBottom(completedToBottom: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateCompletedToBottom(completedToBottom)
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.insert(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.delete(task = task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.update(task = task)
        }
    }

    class TaskViewModelFactory(
        private val tasksRepository: TasksRepository,
        private val userPreferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
                return TaskViewModel(tasksRepository, userPreferencesRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}