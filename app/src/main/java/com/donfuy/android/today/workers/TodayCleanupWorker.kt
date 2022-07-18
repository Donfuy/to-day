package com.donfuy.android.today.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.donfuy.android.today.TodayApplication
import com.donfuy.android.today.data.UserPreferencesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.*

@HiltWorker
class TodayCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters,
    private val userPreferencesRepository: UserPreferencesRepository
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        // Get calendar instance with current date & time
        val calendar = Calendar.getInstance()

        // Calculate task bin removal time from the user preferences.
        userPreferencesRepository.daysToKeep.collect { daysToKeepTasksInBin ->
            // Add number of days that tasks should remain in the bin before being permanently
            // deleted.
            calendar.add(Calendar.DAY_OF_MONTH, daysToKeepTasksInBin)

            (this.applicationContext as TodayApplication).repository.binTodayTasks(calendar.time)
            (this.applicationContext as TodayApplication).repository.moveTomorrowTasksToToday()
        }

        return Result.success()
    }
}