package com.example.recallify.view.ui.feature.application.tbi_applications.sidequest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recallify.databinding.ActivitySideQuestProgressBinding
import java.util.*
import kotlin.math.abs
import kotlin.math.log10

class SideQuestProgressActivity : AppCompatActivity() {

    private lateinit var sideQuestProgressBinding: ActivitySideQuestProgressBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sideQuestProgressBinding = ActivitySideQuestProgressBinding.inflate(layoutInflater)
        val view = sideQuestProgressBinding.root
        setContentView(view)
        val datePicker = sideQuestProgressBinding.datePicker
        val today = Calendar.getInstance()
        datePicker.init(
            today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)

        ) { _, dateYear, dateMonth, dateDay ->
            val month = dateMonth + 1

            val selectedDate: String
            val digitCounter = month.length()
            val digitCounterDay = dateDay.length()

            selectedDate = if (digitCounter != 2) {
                if (digitCounterDay != 2) {
                    "$dateYear-0$month-0$dateDay"
                } else {
                    "$dateYear-0$month-$dateDay"
                }
            } else {
                if (digitCounterDay != 2) {
                    "$dateYear-$month-0$dateDay"
                } else {
                    "$dateYear-$month-$dateDay"
                }
            }

            sideQuestProgressBinding.SelectDateTextViewValue.text = selectedDate

            sideQuestProgressBinding.buttonAnalyzeP.setOnClickListener {
                val intent = Intent(this, SideQuestDisplayResultActivity::class.java)
                intent.putExtra("currentDate", selectedDate)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun Int.length() = when (this) {
        0 -> 1
        else -> log10(abs(toDouble())).toInt() + 1
    }
}