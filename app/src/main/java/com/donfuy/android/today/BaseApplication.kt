package com.donfuy.android.today

import android.app.Application
import com.donfuy.android.today.data.TaskItemDatabase

class BaseApplication : Application() {
    val database: TaskItemDatabase by lazy { TaskItemDatabase.getDatabase(this) }
}