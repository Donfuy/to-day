package com.donfuy.android.today

import androidx.lifecycle.*
import com.donfuy.android.today.data.TaskItemDao
import com.donfuy.android.today.model.TaskItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class TaskViewModel(
    private val taskItemDao: TaskItemDao
) : ViewModel() {
    val todayItems: Flow<List<TaskItem>> = taskItemDao.getTodayItems()
    val tomorrowItems: Flow<List<TaskItem>> = taskItemDao.getTomorrowItems()
    val binItems: Flow<List<TaskItem>> = taskItemDao.getBinItems()

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

    fun setCheck(taskItem: TaskItem) {
         updateItem(taskItem.copy(checked = !taskItem.checked))
    }

    fun recycleItem(taskItem: TaskItem) {
        updateItem(taskItem = taskItem.copy(deleted = true))
    }

    fun setTomorrow(taskItem: TaskItem) {
        updateItem(taskItem = taskItem.copy(tomorrow = true))
    }

    class TodoViewModelFactory(private val taskItemDao: TaskItemDao) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
                return TaskViewModel(taskItemDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}