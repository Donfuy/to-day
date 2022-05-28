package com.donfuy.android.today.data

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.donfuy.android.today.model.TaskItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskItemDao {
    @Query("SELECT * FROM todo_item WHERE deleted = 0 AND tomorrow = 0")
    fun getTodayItems(): Flow<List<TaskItem>>

    @Query("SELECT * FROM todo_item WHERE deleted = 1")
    fun getBinItems(): Flow<List<TaskItem>>

    @Query("SELECT * FROM todo_item WHERE tomorrow = 1")
    fun getTomorrowItems(): Flow<List<TaskItem>>

    @Query("SELECT * FROM todo_item WHERE id = :id")
    fun getTodoItem(id: Long): Flow<TaskItem>

    @Query("SELECT * FROM todo_item WHERE task = :task")
    fun getTodoItem(task: String): Flow<TaskItem>

    @Query("DELETE FROM todo_item WHERE deleted = 1")
    suspend fun deleteBinItems()

    @Insert(onConflict = REPLACE)
    suspend fun insert(taskItem: TaskItem)

    @Update
    suspend fun update(taskItem: TaskItem)

    @Delete
    suspend fun delete(taskItem: TaskItem)
}