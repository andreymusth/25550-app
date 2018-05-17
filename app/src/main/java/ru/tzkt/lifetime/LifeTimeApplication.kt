package ru.tzkt.lifetime

import android.app.Application
import com.google.android.gms.ads.MobileAds

/**
 * Created by andrey on 28/04/2018.
 */
class LifeTimeApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this, "ca-app-pub-5626828738405382~1990276979")
    }
}