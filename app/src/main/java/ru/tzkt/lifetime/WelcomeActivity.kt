package ru.tzkt.lifetime

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.animation.AnimationUtils
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
        for (i in ((year - 75)..year)) {
            years.add(i.toString())
        }

        return@lazy years
    }

    private val days31 by lazy { getArrayListFromArray(R.array.days_of_month_31) }
    private val days30 by lazy { getArrayListFromArray(R.array.days_of_month_30) }
    private val days29 by lazy { getArrayListFromArray(R.array.days_of_month_29) }


    private var currentDay = "1"
    private var currentArrayList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        pvDay.setDataList(days31)
        pvMonth.setDataList(months)
        pvYear.setDataList(years)

        currentArrayList = days31

        pvDay.setOnScrollChangedListener(object : PickerView.OnScrollChangedListener {
            override fun onScrollChanged(curIndex: Int) {

            }

            override fun onScrollFinished(curIndex: Int) {
                currentDay = currentArrayList!![curIndex]
            }

        })

        pvYear.moveTo(years.indexOf("2000"))
        pvMonth.setOnScrollChangedListener(object : PickerView.OnScrollChangedListener {
            override fun onScrollChanged(curIndex: Int) {
                vibrate(this@WelcomeActivity)
            }

            override fun onScrollFinished(curIndex: Int) {
                vibrate(this@WelcomeActivity)

                when (curIndex) {
                    0,2,4,6,7,9,11 -> {
                        pvDay.setDataList(days31)
                        currentArrayList = days31
                        if (currentArrayList!!.contains(currentDay)) {
                            pvDay.moveTo(currentArrayList!!.indexOf(currentDay))
                        }
                    }
                    1 -> {
                        pvDay.setDataList(days29)
                        currentArrayList = days29
                        if (currentArrayList!!.contains(currentDay)) {
                            pvDay.moveTo(currentArrayList!!.indexOf(currentDay))
                        }
                    }
                    3,5,8,10 -> {
                        pvDay.setDataList(days30)
                        currentArrayList = days30
                        if (currentArrayList!!.contains(currentDay)) {
                            pvDay.moveTo(currentArrayList!!.indexOf(currentDay))
                        }
                    }
                }

            }

        })


        val tfMedium = Typeface.createFromAsset(assets,"fonts/Montserrat-Medium.ttf")
        val tvLight = Typeface.createFromAsset(assets,"fonts/Montserrat-Light.ttf")

        tvWelcomeText.typeface = tfMedium
        tvAppName.typeface = tfMedium
        tvEnterDateOfBirth.typeface = tvLight

        btnLetsGo.typeface = tfMedium
        btnLetsGo.setOnClickListener {

            saveDate()

            val i = Intent(this, ResultActivity::class.java)
            startActivity(i)
        }

    }


    override fun onResume() {
        super.onResume()

        val animWithoutOffset = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        tvWelcomeText.startAnimation(animWithoutOffset)
        tvAppName.startAnimation(animWithoutOffset)


        val anim1 = AnimationUtils.loadAnimation(this, R.anim.fade_in_1)
        tvEnterDateOfBirth.startAnimation(anim1)
        llPicker.startAnimation(anim1)

        val anim2 = AnimationUtils.loadAnimation(this, R.anim.fade_in_2)
        btnLetsGo.startAnimation(anim2)
    }

    private fun saveDate() {
        val dateOfBirth = Calendar.getInstance()
        dateOfBirth.set(Calendar.DAY_OF_MONTH, Integer.parseInt(days31[pvDay.curIndex]))
        dateOfBirth.set(Calendar.MONTH, pvMonth.curIndex + 1)
        dateOfBirth.set(Calendar.YEAR, Integer.parseInt(years[pvYear.curIndex]))

        Utils.saveDate(this, dateOfBirth)

    }

    private fun getArrayListFromArray(id: Int): ArrayList<String> {
        return resources.getStringArray(id).toCollection(ArrayList())
    }

    fun vibrate(c: Context) {
//        if (Build.VERSION.SDK_INT >= 26) {
//            (c.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION, VibrationEffect.DEFAULT_AMPLITUDE))
//        } else {
//            (c.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(VIBRATION_DURATION)
//        }
    }
}
