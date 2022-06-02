package com.donfuy.android.today

import android.app.Application
import com.donfuy.android.today.data.TaskItemDatabase
import com.donfuy.android.today.data.TasksRepository

class BaseApplication : Application() {
    val database: TaskItemDatabase by lazy { TaskItemDatabase.getDatabase(this) }
    val repository: TasksRepository by lazy { TasksRepository(database.taskItemDao()) }
}