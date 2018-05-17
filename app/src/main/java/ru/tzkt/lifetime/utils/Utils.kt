package ru.tzkt.lifetime.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import ru.tzkt.lifetime.R
import ru.tzkt.lifetime.ResultActivity
import ru.tzkt.lifetime.service.NotificationService
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by user on 26.04.2018.
 */
class Utils {

    companion object {

        const val SAVED_DATE_PREF = "SAVED_DATE_PREF"

        fun saveDate(ctx: Context, date: Calendar) {
            val preferences = ctx.getSharedPreferences(SAVED_DATE_PREF, MODE_PRIVATE)
            val editor = preferences.edit()

            val day = formatDate(date.get(Calendar.DAY_OF_MONTH).toString())
            val month = formatDate(date.get(Calendar.MONTH).toString())

            editor.putString(SAVED_DATE_PREF, "$day.$month.${date.get(Calendar.YEAR)}")
            editor.apply()
        }

        private fun formatDate(date: String): String {

            if (date.length == 1) {
                return "0$date"
            }
            return date
        }

        fun getDate(ctx: Context): Calendar {
            val preferences = ctx.getSharedPreferences(SAVED_DATE_PREF, MODE_PRIVATE)
            val stringDate = preferences.getString(SAVED_DATE_PREF, "01.01.1812")

            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
            cal.time = sdf.parse(stringDate)
            return cal
        }

        fun getLivedDays(thatDay: Calendar): Int {
            val today = Calendar.getInstance()
            val diff = today.timeInMillis - thatDay.timeInMillis
            return (diff / (24 * 60 * 60 * 1000)).toInt()
        }

        fun sendNotification(ctx: Context) {
            val builder = NotificationCompat.Builder(ctx)
                    .setSmallIcon(R.drawable.app_icon)
                    .setAutoCancel(true)
                    .setContentTitle(ctx.getString(R.string.app_name))
                    .setContentText(ctx.getString(R.string.minus_one))


            val intent = Intent(ctx, ResultActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0)
            builder.setContentIntent(pendingIntent)

            val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(1488, builder.build())
        }

        fun scheduleJob(ctx: Context) {
            //Create JobScheduler
            val jobScheduler = ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val jobService = ComponentName(ctx.packageName, NotificationService::class.java.name)
            val jobInfo = JobInfo.Builder(1209, jobService)

            val refreshInterval = 24 * 60 * 60 * 1000L

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                jobInfo.setMinimumLatency(refreshInterval)
            } else {
                jobInfo.setPeriodic(refreshInterval)
            }

            val resultCode = jobScheduler.schedule(jobInfo.build())
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d("TaskActivity", "Job scheduled!")
            } else {
                Log.d("TaskActivity", "Job not scheduled")
            }
        }
    }
}