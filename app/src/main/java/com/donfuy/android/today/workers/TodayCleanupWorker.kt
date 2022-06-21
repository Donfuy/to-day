package com.donfuy.android.today.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.donfuy.android.today.TodayApplication
import com.donfuy.android.today.data.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject

class TodayCleanupWorker @Inject constructor(
    context: Context,
    parameters: WorkerParameters,
    private val userPreferencesRepository: UserPreferencesRepository
) :
    CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val calendar = Calendar.getInstance()

        val daysToKeepTasks = runBlocking {
            userPreferencesRepository.daysToKeep.first()
        }
        calendar.add(Calendar.DAY_OF_MONTH, daysToKeepTasks)

        (this.applicationContext as TodayApplication).repository.binTodayTasks(calendar.time)
        (this.applicationContext as TodayApplication).repository.moveTomorrowToToday()
        return Result.success()
    }
}