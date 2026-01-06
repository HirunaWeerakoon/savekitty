package com.example.savekitty.data

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // 1. Create the Helper
        val notificationHelper = NotificationHelper(applicationContext)

        // 2. Send the "Meow" Notification
        notificationHelper.showMeowNotification()

        // 3. Tell Android we finished successfully
        return Result.success()
    }
}