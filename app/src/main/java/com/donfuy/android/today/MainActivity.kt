package com.donfuy.android.today

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.donfuy.android.today.ui.theme.ToDayTheme
import com.donfuy.android.today.ui.todo.TodoScreen
import androidx.compose.material.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

class MainActivity : ComponentActivity() {

    private val todoViewModel by viewModels<TodoViewModel> {
        TodoViewModel.TodoViewModelFactory(
            (this.applicationContext as BaseApplication).database.todoItemDao()
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ToDayTheme {
                Surface {
                    MainActivityScreen(todoViewModel = todoViewModel)
                }
            }
        }
    }
}

@Composable
fun MainActivityScreen(todoViewModel: TodoViewModel) {
    TodoScreen(
        items = todoViewModel.todoItems,
        editingItems = todoViewModel.editingTodoItems,
        onAddItem = todoViewModel::addItem,
        onUpdateItem = todoViewModel::updateItem,
        onDeleteItem = todoViewModel::deleteItem,
        currentlyEditingItem = todoViewModel.currentlyEditingItem,
        setCheck = todoViewModel::setCheck
    )
}