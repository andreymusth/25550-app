package ru.tzkt.lifetime.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import ru.tzkt.lifetime.R
import ru.tzkt.lifetime.ResultActivity
import ru.tzkt.lifetime.SendNotificationWorker
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by user on 26.04.2018.
 */
class Utils {

    companion object {

        private const val SAVED_DATE_PREF = "SAVED_DATE_PREF"

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

        fun sendNotification(context: Context) {
            val builder = NotificationCompat.Builder(context, "main")
                .setSmallIcon(R.drawable.app_icon)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.minus_one))

            val intent = Intent(context, ResultActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE)
            builder.setContentIntent(pendingIntent)

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(15994, builder.build())
        }

        fun scheduleJob(context: Context) {
            val request =
                PeriodicWorkRequestBuilder<SendNotificationWorker>(1, TimeUnit.DAYS)
                    .build()

            WorkManager
                .getInstance(context)
                .enqueue(request)
        }
    }
}