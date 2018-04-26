package ru.tzkt.lifetime

import android.content.Context
import android.content.Context.MODE_PRIVATE
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
            val stringDate = preferences.getString(SAVED_DATE_PREF, "0.0.0")

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
    }
}