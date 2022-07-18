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
        val calendar = Calendar.getInstance()

//        val blockingDaysToKeepTasks = runBlocking {
//            userPreferencesRepository.daysToKeep.first()
//        }

        userPreferencesRepository.daysToKeep.collect { daysToKeepTasks ->
            calendar.add(Calendar.DAY_OF_MONTH, daysToKeepTasks)

            (this.applicationContext as TodayApplication).repository.binTodayTasks(calendar.time)
            (this.applicationContext as TodayApplication).repository.moveTomorrowToToday()
        }

        return Result.success()
    }
}