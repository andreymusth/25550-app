package ru.tzkt.lifetime

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import ru.tzkt.lifetime.utils.Utils
import java.util.*

class ResultActivity : AppCompatActivity() {

    private val tvLivedDays: TextView by lazy { findViewById(R.id.tvLivedDays) }
    private val tvLivedPercent: TextView by lazy { findViewById(R.id.tvLivedPercent) }
    private val tvLeftDays: TextView by lazy { findViewById(R.id.tvLeftDays) }
    private val tvLeftPercent: TextView by lazy { findViewById(R.id.tvLeftPercent) }
    private val adView: AdView by lazy { findViewById(R.id.adView) }
    private val ivMarker: ImageView by lazy { findViewById(R.id.ivMarker) }
    private val ivLifeLine: ImageView by lazy { findViewById(R.id.ivLifeLine) }
    private val flReset: FrameLayout by lazy { findViewById(R.id.flReset) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        Utils.scheduleJob(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NETWORK_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions()
        }

        val date = Utils.getDate(this)

        if (date.get(Calendar.YEAR) == 1812) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            return
        }

        val daysLived = Utils.getLivedDays(date)

        val tf = Typeface.createFromAsset(assets, "fonts/Montserrat-Medium.ttf")

        tvLivedDays.typeface = tf
        tvLivedPercent.typeface = tf
        tvLeftDays.typeface = tf
        tvLeftPercent.typeface = tf


        tvLivedDays.text = getLivedDaysString(daysLived)
        tvLivedPercent.text = getLivedPercentString(daysLived)
        tvLeftDays.text = getLeftDaysString(daysLived)
        tvLeftPercent.text = getLeftPercentString(daysLived)

        val anim1 = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        tvLivedDays.startAnimation(anim1)
        tvLivedPercent.startAnimation(anim1)

        val anim2 = AnimationUtils.loadAnimation(this, R.anim.fade_in_1)
        tvLeftDays.startAnimation(anim2)
        tvLeftPercent.startAnimation(anim2)

        anim2.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {

                val startX = ivLifeLine.x
                val xOnLifeLine = ivLifeLine.width * daysLived / 25550
                val endX = startX + xOnLifeLine - ivMarker.width / 2

                ivMarker
                    .animate()
                    .x(endX)
                    .setInterpolator(AnticipateOvershootInterpolator())
                    .setDuration(1000)
                    .start()

            }

            override fun onAnimationStart(p0: Animation?) {
            }

        })

        flReset.setOnClickListener {
            startActivity(Intent(this, WelcomeActivity::class.java))
        }

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE),
            1
        )
    }

    private fun getLeftPercentString(daysLived: Int): String {
        val percent = "%.3f".format(100 - daysLived.toFloat() / 25550 * 100)
        return String.format(getString(R.string.days_left_percent), percent) + "%"
    }

    private fun getLeftDaysString(daysLived: Int): String {
        return String.format(getString(R.string.days_left), 25550 - daysLived)
    }

    private fun getLivedDaysString(daysLived: Int): String {
        return String.format(getString(R.string.days_lived), daysLived)

    }

    private fun getLivedPercentString(daysLived: Int): String {
        val percent = "%.3f".format(daysLived.toFloat() / 25550 * 100)
        return String.format(getString(R.string.days_lived_percent), percent) + "%"
    }

}
