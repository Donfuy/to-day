package com.donfuy.android.today

import androidx.lifecycle.*
import com.donfuy.android.today.data.TaskItemDao
import com.donfuy.android.today.data.UserPreferencesRepository
import com.donfuy.android.today.model.TaskItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.IllegalArgumentException
import java.util.Calendar
import java.util.Date

class TaskViewModel(
    private val taskItemDao: TaskItemDao,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val showCompleted: Flow<Boolean> = userPreferencesRepository.showCompletedFlow
    val completedToBottom: Flow<Boolean> = userPreferencesRepository.completedToBottom

    val todayItems: Flow<List<TaskItem>> = taskItemDao.getTodayItems()
    val tomorrowItems: Flow<List<TaskItem>> = taskItemDao.getTomorrowItems()
    val binItems: Flow<List<TaskItem>> = taskItemDao.getBinItems()

    fun newTask(task: String, tomorrow: Boolean) {
        val creationDate: Date = Calendar.getInstance().time
        addItem(
            TaskItem(
                task = task,
                creationDate = creationDate,
                lastModified = null,
                deletionDate = null,
                tomorrow = tomorrow
            )
        )
    }

    fun setCheck(taskItem: TaskItem) {
        updateItem(taskItem.copy(checked = !taskItem.checked))
    }

    fun recycleItem(taskItem: TaskItem) {
        updateItem(
            taskItem = taskItem.copy(
                deleted = true,
                deletionDate = Calendar.getInstance().time
            )
        )
    }

    fun setTomorrow(taskItem: TaskItem) {
        updateItem(taskItem = taskItem.copy(tomorrow = true))
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

    fun addItem(taskItem: TaskItem) {
        viewModelScope.launch {
            taskItemDao.insert(taskItem)
        }
    }

    fun deleteItem(taskItem: TaskItem) {
        viewModelScope.launch {
            taskItemDao.delete(taskItem = taskItem)
        }
    }

    fun updateItem(taskItem: TaskItem) {
        viewModelScope.launch {
            taskItemDao.update(taskItem = taskItem)
        }
    }

    fun deleteBinItems() {
        viewModelScope.launch {
            taskItemDao.deleteBinItems()
        }
    }


    class TodoViewModelFactory(
        private val taskItemDao: TaskItemDao,
        private val userPreferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
                return TaskViewModel(taskItemDao, userPreferencesRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}