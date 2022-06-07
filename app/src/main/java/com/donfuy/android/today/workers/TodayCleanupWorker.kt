package com.donfuy.android.today.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.donfuy.android.today.TodayApplication

class TodayCleanupWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        (this.applicationContext as TodayApplication).repository.binTodayTasks()
        (this.applicationContext as TodayApplication).repository.moveTomorrowToToday()
        return Result.success()
    }
}