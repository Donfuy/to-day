package com.donfuy.android.today.data

import com.donfuy.android.today.model.Task
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class TasksRepository(private val taskDao: TaskDao) {

    val todayTasks: Flow<List<Task>> = taskDao.getTodayItems()

    val tomorrowTasks: Flow<List<Task>> = taskDao.getTomorrowItems()

    val binTasks: Flow<List<Task>> = taskDao.getBinItems()

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    suspend fun binTodayTasks() {
        taskDao.binTodayItems()
    }

    suspend fun moveTomorrowToToday() {
        taskDao.tomorrowToToday()
    }

    suspend fun deleteBinTasks() {
        taskDao.deleteBinItems(Calendar.getInstance().time)
    }

}