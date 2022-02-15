package ru.tzkt.lifetime

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.tzkt.lifetime.utils.Utils

class SendNotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        Utils.sendNotification(applicationContext)
        return Result.success()
    }
}