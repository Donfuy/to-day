package com.donfuy.android.today

import android.app.Application
import com.donfuy.android.today.data.TodoItemDatabase

class BaseApplication : Application() {
    val database: TodoItemDatabase by lazy { TodoItemDatabase.getDatabase(this) }
}