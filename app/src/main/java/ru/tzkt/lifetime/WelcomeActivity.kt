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
    private val years by lazy {

        val years = ArrayList<String>()

        val year = Calendar.getInstance().get(Calendar.YEAR)
        for (i in (year downTo (year - 75))) {
            years.add(i.toString())
        }

        return@lazy years
    }

    private val days by lazy { getArrayListFromArray(R.array.days_of_month_31) }


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

        pvDay.setDataList(days)
        pvMonth.setDataList(months)
        pvYear.setDataList(years)

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

            saveDate()

            val i = Intent(this, ResultActivity::class.java)
            startActivity(i) }

    }


    private fun saveDate() {
        val dateOfBirth = Calendar.getInstance()
        dateOfBirth.set(Calendar.DAY_OF_MONTH, Integer.parseInt(days[pvDay.curIndex]))
        dateOfBirth.set(Calendar.MONTH, pvMonth.curIndex + 1)
        dateOfBirth.set(Calendar.YEAR, Integer.parseInt(years[pvYear.curIndex]))

        Utils.saveDate(this, dateOfBirth)

    }

    private fun getArrayListFromArray(id: Int): ArrayList<String> {
        return resources.getStringArray(id).toCollection(ArrayList())
    }

    fun vibrate(c: Context) {
        if (Build.VERSION.SDK_INT >= 26) {
            (c.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            (c.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(VIBRATION_DURATION)
        }
    }
}
