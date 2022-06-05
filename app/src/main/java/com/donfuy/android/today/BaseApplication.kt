package com.donfuy.android.today

import android.app.Application
import com.donfuy.android.today.data.TaskDatabase
import com.donfuy.android.today.data.TasksRepository

class BaseApplication : Application() {
    val database: TaskDatabase by lazy { TaskDatabase.getDatabase(this) }
    val repository: TasksRepository by lazy { TasksRepository(database.taskItemDao()) }
}