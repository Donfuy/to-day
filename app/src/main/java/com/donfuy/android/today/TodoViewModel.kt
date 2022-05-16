package com.donfuy.android.today

import androidx.lifecycle.*
import com.donfuy.android.today.data.TodoItemDao
import com.donfuy.android.today.model.TodoItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class TodoViewModel(
    private val todoItemDao: TodoItemDao
) : ViewModel() {
    val todoItems: Flow<List<TodoItem>> = todoItemDao.getTodoItems()

    val editingTodoItems: Flow<List<TodoItem>> = todoItemDao.getEditingTodoItems()
    var isEditing: Boolean = false
    var currentlyEditingItem: TodoItem? = null

    init {
        viewModelScope.launch {
            editingTodoItems.collect {
                    isEditing = it.isNotEmpty()
            }
        }
    }

    fun addItem(todoItem: TodoItem) {
        viewModelScope.launch {
            todoItemDao.insert(todoItem)
        }
    }

    fun deleteItem(todoItem: TodoItem) {
        viewModelScope.launch {
            todoItemDao.delete(todoItem = todoItem)
        }
    }

    fun updateItem(todoItem: TodoItem) {
        viewModelScope.launch {
            todoItemDao.update(todoItem = todoItem)
        }
    }

    fun setCheck(todoItem: TodoItem) {
         updateItem(todoItem.copy(checked = !todoItem.checked))
    }

    class TodoViewModelFactory(private val todoItemDao: TodoItemDao) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
                return TodoViewModel(todoItemDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}