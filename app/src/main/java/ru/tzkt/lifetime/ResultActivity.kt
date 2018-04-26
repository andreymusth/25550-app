package ru.tzkt.lifetime

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val date = Utils.getDate(this)
        val daysLived = Utils.getLivedDays(date)

        val tf =  Typeface.createFromAsset(assets,"fonts/Montserrat-Medium.ttf")

        tvLivedDays.typeface = tf
        tvLivedPercent.typeface = tf
        tvLeftDays.typeface = tf
        tvLeftPercent.typeface = tf


        tvLivedDays.text = getLivedDaysString(daysLived)
        tvLivedPercent.text = getLivedPercentString(daysLived)
        tvLeftDays.text = getLeftDaysString(daysLived)
        tvLeftPercent.text = getLeftPercentString(daysLived)
    }

    private fun getLeftPercentString(daysLived: Int): String {
        val percent = "%.3f".format(100 - daysLived.toFloat() / 25550 * 100)
        return "you have $percent % and"
    }

    private fun getLeftDaysString(daysLived: Int): String {
        return "$daysLived days more"
    }

    private fun getLivedDaysString(daysLived: Int): String {
        return "and $daysLived days"

    }

    private fun getLivedPercentString(daysLived: Int): String {
        val percent = "%.3f".format(daysLived.toFloat() / 25550 * 100)
        return "$percent %"
    }

}
