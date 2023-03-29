package com.example.recallify.view.ui.feature.application.tbi_applications.sidequest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.recallify.R
import com.example.recallify.databinding.ActivitySideQuestGameSelectorDateBinding
import java.util.*

class SideQuestGameSelectorDate : AppCompatActivity() {
    lateinit var mainBinding: ActivitySideQuestGameSelectorDateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding =  ActivitySideQuestGameSelectorDateBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        val datePicker = mainBinding.datePicker
        val today = Calendar.getInstance()
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)

        ) { view, year, month, day ->
            var month = month + 1

            var selectedDate =""
            val digitCounter = month.length()
            var digitcounterDay = day.length()

            if(digitCounter!=2){
                if(digitcounterDay!=2){
                    selectedDate = "$year-0$month-0$day"
                }
                else{
                    selectedDate = "$year-0$month-$day"
                }

            }
            else{
                if(digitcounterDay!=2){
                    selectedDate = "$year-$month-0$day"
                }
                else{
                    selectedDate = "$year-$month-$day"
                }

            }
            mainBinding.SelectDateTextViewValue.text = selectedDate


            mainBinding.buttonAnalyzeP.setOnClickListener {


                val intent = Intent(this,SideQuestQuizActivity::class.java)
                intent.putExtra("currentDate",selectedDate)
                startActivity(intent)
                finish()
            }

        }
    }

    fun Int.length() = when(this) {
        0 -> 1
        else -> Math.log10(Math.abs(toDouble())).toInt() + 1
    }
}