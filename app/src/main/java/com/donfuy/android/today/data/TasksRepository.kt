package com.donfuy.android.today.data

import com.donfuy.android.today.model.TaskItem
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class TasksRepository(private val taskItemDao: TaskItemDao) {

    val todayTasks: Flow<List<TaskItem>> = taskItemDao.getTodayItems()

    val tomorrowTasks: Flow<List<TaskItem>> = taskItemDao.getTomorrowItems()

    val binTasks: Flow<List<TaskItem>> = taskItemDao.getBinItems()

    suspend fun insert(taskItem: TaskItem) {
        taskItemDao.insert(taskItem)
    }

    suspend fun delete(taskItem: TaskItem) {
        taskItemDao.delete(taskItem)
    }

    suspend fun update(taskItem: TaskItem) {
        taskItemDao.update(taskItem)
    }

    suspend fun binTodayTasks() {
        taskItemDao.binTodayItems()
        taskItemDao.tomorrowToToday()
    }

    suspend fun deleteBinTasks() {
        taskItemDao.deleteBinItems(Calendar.getInstance().time)
    }

}