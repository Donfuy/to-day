package com.donfuy.android.today.data

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.donfuy.android.today.model.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {
    @Query("SELECT * FROM todo_item ORDER BY position")
    fun getTodoItems(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_item WHERE id = :id")
    fun getTodoItem(id: Long): Flow<TodoItem>

    @Query("SELECT * FROM todo_item WHERE task = :task")
    fun getTodoItem(task: String): Flow<TodoItem>

    @Query("SELECT * FROM todo_item WHERE editing = 1")
    fun getEditingTodoItems(): Flow<List<TodoItem>>

    @Insert(onConflict = REPLACE)
    suspend fun insert(todoItem: TodoItem)

    @Update
    suspend fun update(todoItem: TodoItem)

    @Delete
    suspend fun delete(todoItem: TodoItem)
}