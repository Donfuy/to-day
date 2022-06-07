package com.donfuy.android.today.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.donfuy.android.today.TodayApplication

class BinCleanupWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        (this.applicationContext as TodayApplication).repository.deleteBinTasks()
        return Result.success()
    }
}