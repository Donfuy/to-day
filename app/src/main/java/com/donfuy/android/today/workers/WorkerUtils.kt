package com.donfuy.android.today.workers

import android.content.Context
import androidx.work.*
import com.donfuy.android.today.KEY_KEEP_TASKS_FOR
import java.util.*
import java.util.concurrent.TimeUnit

private fun getInitialDelay(hour: Int, minute: Int): Long {
    val calendar = Calendar.getInstance()
    val now = calendar.timeInMillis

    // Check if target time is on the current day or the next one
    // if current time is past target time, schedule for tomorrow
    if (calendar.get(Calendar.HOUR_OF_DAY) > hour ||
        (calendar.get(Calendar.HOUR_OF_DAY) == hour && calendar.get(Calendar.MINUTE)+1 >= minute)) {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.timeInMillis - now
}

fun scheduleTodayCleanup(context: Context) {
//    val daysToKeepTasksData: Data = workDataOf(KEY_KEEP_TASKS_FOR to daysToKeepTasks)
    val binTodayTasks =
        PeriodicWorkRequestBuilder<TodayCleanupWorker>(1, TimeUnit.DAYS)
//            .setInputData(daysToKeepTasksData)
            .setInitialDelay(getInitialDelay(3, 0), TimeUnit.MILLISECONDS)
            .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "binToday",
            ExistingPeriodicWorkPolicy.KEEP,
            binTodayTasks
        )
}

fun scheduleBinCleanup(context: Context) {
    val deleteBinnedTasks =
        PeriodicWorkRequestBuilder<BinCleanupWorker>(3, TimeUnit.DAYS)
            .setInitialDelay(getInitialDelay(3, 0), TimeUnit.MILLISECONDS)
            .build()
    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "binCleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            deleteBinnedTasks
        )
}