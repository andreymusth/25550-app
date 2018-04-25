package ru.tzkt.lifetime

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ResultActivity : AppCompatActivity() {


    companion object {

        const val SELECTED_DATE_ARG = "SELECTED_DATE_ARG"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        if (intent.hasExtra(SELECTED_DATE_ARG)) {

        }
    }

}
