package com.donfuy.android.today.data

import com.donfuy.android.today.model.Task
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class TasksRepository @Inject constructor(private val taskDao: TaskDao) {

    val todayTasks: Flow<List<Task>> = taskDao.getTodayItems()
    val todayTasksHideComplete: Flow<List<Task>> = taskDao.getTodayItemsHideComplete()

    val tomorrowTasks: Flow<List<Task>> = taskDao.getTomorrowItems()

    val binTasks: Flow<List<Task>> = taskDao.getBinItems()

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    suspend fun bin(task: Task, deleteBy: Date) {
        update(
            task = task.copy(
                binned = true,
                deleteBy = deleteBy
            )
        )
    }

    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    /**
     * Moves all today tasks to the bin, while setting their deletion time to the Date provided to
     * deleteBy.
     *
     * @param deleteBy: Date at which the today tasks should be permanently deleted.
     */
    suspend fun binTodayTasks(deleteBy: Date) {
        taskDao.binTodayItems(deleteBy)
    }

    suspend fun moveTomorrowTasksToToday() {
        taskDao.tomorrowToToday()
    }

    suspend fun deleteBinTasks() {
        taskDao.deleteBinItems(Calendar.getInstance().time)
    }

    suspend fun deleteAllBinnedTasks() {
        taskDao.deleteAllBinnedTasks()
    }

}