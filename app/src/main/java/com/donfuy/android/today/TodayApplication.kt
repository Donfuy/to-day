package com.donfuy.android.today

import android.app.Application
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.donfuy.android.today.data.TaskDatabase
import com.donfuy.android.today.data.TasksRepository
import com.donfuy.android.today.data.UserPreferencesRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TodayApplication : Application(), Configuration.Provider {
    val database: TaskDatabase by lazy { TaskDatabase.getDatabase(this) }
    val repository: TasksRepository by lazy { TasksRepository(database.taskItemDao()) }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}