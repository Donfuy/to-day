package com.donfuy.android.today.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.donfuy.android.today.BaseApplication
import com.donfuy.android.today.data.TaskItemDatabase

class BinTodayItemsWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        (this.applicationContext as BaseApplication).repository.binTasks
        return Result.success()
    }
}