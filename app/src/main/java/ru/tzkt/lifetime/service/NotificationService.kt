package ru.tzkt.lifetime.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import ru.tzkt.lifetime.utils.Utils.Companion.sendNotification
import java.util.*

class NotificationService : JobService() {

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d("NotificationJobService", "On start command")

        val calendar = Calendar.getInstance()

        if (calendar.get(Calendar.HOUR_OF_DAY) in (7..23)) {
            sendNotification(this)
        }

        jobFinished(params, true)
        return false
    }
}
