package com.example.recallify.view.ui.feature.application.tbi_applications.sidequest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.recallify.R

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.recallify.databinding.ActivityAnalyzeProgressSideQuestBinding

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
class AnalyzeProgressSideQuest : AppCompatActivity() {
    lateinit var analyzeProgressBinding:ActivityAnalyzeProgressSideQuestBinding

    var totelCorrect = 0

    var maxTotalCorrect = 100

    var percent_correct=0

    var incorrect = 0
    var correct = 0
    var total =0
    var difference = 0

    @RequiresApi(Build.VERSION_CODES.O)
    val current = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @RequiresApi(Build.VERSION_CODES.O)
    val formatted = current.format(formatter)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyzeProgressBinding = ActivityAnalyzeProgressSideQuestBinding.inflate(layoutInflater)
        val view = analyzeProgressBinding.root
        setContentView(view)
        var currentDate:String = intent.getStringExtra("currentDate").toString()

        analyzeProgress(currentDate)

        analyzeProgressBinding.buttonDone.setOnClickListener {
            val intent = Intent(this,SideQuestActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun analyzeProgress(currentDate:String) {
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val database = Firebase.database.reference.child("users").child(uid).child("viewScoresTableSideQuest").child(currentDate)
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var questionCount = snapshot.childrenCount.toInt()
                    if(questionCount>=1) {
                        totelCorrect = snapshot.child("1").child("percentage").value.toString().toInt()
                        correct = snapshot.child("1").child("correct").value.toString().toInt()
                        incorrect = snapshot.child("1").child("wrong").value.toString().toInt()
                        total = correct+incorrect
                        difference = total-correct
                        analyzeProgressBinding.totalScoreTextViewValue.text = totelCorrect.toString()+"%"
                        analyzeProgressBinding.dateTextViewValue.text = currentDate

                        analyzeProgressBinding.progressBar.max = maxTotalCorrect
                        val currentProgress = totelCorrect

                        ObjectAnimator.ofInt(analyzeProgressBinding.progressBar,
                            "progress", currentProgress).setDuration(2000).start()
                        if (totelCorrect >= maxTotalCorrect) {
                            percent_correct = 100
                            analyzeProgressBinding.percentShow.text =
                                percent_correct.toString() + "%"
                            analyzeProgressBinding.todayScoreFeedbackViewValue.text =
                                "Congrats, for achieving your daily target \uD83D\uDC4F"
                        } else {

                            analyzeProgressBinding.percentShow.text =
                                totelCorrect.toString() + "%"

                      //      var minCorrect = maxTotalCorrect - totelCorrect
                            var answer1 = "scores"
                            if(difference == 1){
                                answer1="score"
                            }
                          //  analyzeProgressBinding.todayScoreFeedbackViewValue.text = "You need to get ${difference} + correct answer to reach daily target"
                            analyzeProgressBinding.todayScoreFeedbackViewValue.text = "${difference} + $answer1 needed to reach your daily target \uD83D\uDE0A"

                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }//if uid ! = null
    }
}