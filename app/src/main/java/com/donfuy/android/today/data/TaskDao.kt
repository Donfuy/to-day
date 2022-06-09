package com.donfuy.android.today.data

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.donfuy.android.today.model.Task
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TaskDao {
    @Query("SELECT * FROM task WHERE binned = 0 AND tomorrow = 0")
    fun getTodayItems(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE binned = 0 AND tomorrow = 0 ORDER BY checked ASC")
    fun getTodayItemsSortByComplete(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE binned = 0 AND tomorrow = 0 AND checked = 0")
    fun getTodayItemsHideComplete(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE binned = 1")
    fun getBinItems(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE tomorrow = 1 AND binned = 0")
    fun getTomorrowItems(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTodoItem(id: Long): Flow<Task>

    @Query("SELECT * FROM task WHERE task = :task")
    fun getTodoItem(task: String): Flow<Task>

    @Query("DELETE FROM task WHERE binned = 1 AND deleteBy < :currentDate")
    suspend fun deleteBinItems(currentDate: Date)

    @Query("UPDATE task SET binned = 1 , deleteBy = :deleteBy WHERE binned = 0 AND tomorrow = 0")
    suspend fun binTodayItems(deleteBy: Date)

    @Query("UPDATE task SET tomorrow = 0 WHERE tomorrow = 1")
    suspend fun tomorrowToToday()

    @Insert(onConflict = REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}