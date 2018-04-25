package ru.tzkt.lifetime

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import kotlinx.android.synthetic.main.activity_welcome.*
import java.util.*
import kotlin.collections.ArrayList

class WelcomeActivity : AppCompatActivity() {

    companion object {
        const val VIBRATION_DURATION = 10L
    }


    private val months by lazy { getArrayListFromArray(R.array.months) }

    private val listener = object : PickerView.OnScrollChangedListener {
        override fun onScrollChanged(curIndex: Int) {
            vibrate(this@WelcomeActivity)
        }

        override fun onScrollFinished(curIndex: Int) {
            vibrate(this@WelcomeActivity)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        pvDay.setDataList(getArrayListFromArray(R.array.days_of_month_31))
        pvMonth.setDataList(months)
        pvYear.setDataList(getYearsArrayList())

        pvDay.setOnScrollChangedListener(listener)
        pvMonth.setOnScrollChangedListener(listener)
        pvYear.setOnScrollChangedListener(listener)


        val tfMedium = Typeface.createFromAsset(assets,"fonts/Montserrat-Medium.ttf")
        val tvLight = Typeface.createFromAsset(assets,"fonts/Montserrat-Light.ttf")

        tvWelcomeText.typeface = tfMedium
        tvAppName.typeface = tfMedium
        tvEnterDateOfBirth.typeface = tvLight

        btnLetsGo.typeface = tfMedium
        btnLetsGo.setOnClickListener {

            val i = Intent(this, ResultActivity::class.java)
            i.putExtra(ResultActivity.SELECTED_DATE_ARG, 9000)
            startActivity(i) }

    }

    fun getLivedDays(): Int {
        val thatDay = Calendar.getInstance()
        thatDay.set(Calendar.DAY_OF_MONTH, 25)
        thatDay.set(Calendar.MONTH, 7) // 0-11 so 1 less
        thatDay.set(Calendar.YEAR, 1985)

        val today = Calendar.getInstance()

        val diff = today.timeInMillis - thatDay.timeInMillis

        return (diff / (24 * 60 * 60 * 1000)).toInt()
    }

    private fun getArrayListFromArray(id: Int): ArrayList<String> {
        return resources.getStringArray(id).toCollection(ArrayList())
    }

    private fun getYearsArrayList(): ArrayList<String> {

        val years = ArrayList<String>()

        val year = Calendar.getInstance().get(Calendar.YEAR)
        for (i in (year downTo (year - 75))) {
            years.add(i.toString())
        }

        return years

    }

    fun vibrate(c: Context) {
        if (Build.VERSION.SDK_INT >= 26) {
            (c.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            (c.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(VIBRATION_DURATION)
        }
    }
}
