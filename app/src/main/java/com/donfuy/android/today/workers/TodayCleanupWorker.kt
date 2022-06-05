package com.donfuy.android.today.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.donfuy.android.today.BaseApplication

class TodayCleanupWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        (this.applicationContext as BaseApplication).repository.binTodayTasks()
        (this.applicationContext as BaseApplication).repository.moveTomorrowToToday()
        return Result.success()
    }
}