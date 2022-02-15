package ru.tzkt.lifetime

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.android.gms.ads.MobileAds

/**
 * Created by andrey on 28/04/2018.
 */
class LifeTimeApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        MobileAds.initialize(this)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("main", "Уведомления", importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}