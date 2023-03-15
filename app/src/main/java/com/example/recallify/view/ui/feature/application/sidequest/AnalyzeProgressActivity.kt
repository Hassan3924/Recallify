package com.example.recallify.view.ui.feature.application.sidequest

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.recallify.R
import com.example.recallify.databinding.ActivityAnalyzeProgressBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AnalyzeProgressActivity : AppCompatActivity() {

    private lateinit var analyzeProgressBinding: ActivityAnalyzeProgressBinding

    var totalCorrect = 0
    var maxTotalCorrect = 100
    var correctPercentage = 0
    var incorrect = 0
    var correct = 0
    var total = 0
    var difference = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        analyzeProgressBinding = ActivityAnalyzeProgressBinding.inflate(layoutInflater)
        val view = analyzeProgressBinding.root
        setContentView(view)

        val currentDate: String = intent.getStringExtra("currentDate").toString()
        analyzeProgress(currentDate)

        analyzeProgressBinding.buttonDone.setOnClickListener {
            val intent = Intent(this, SideQuestActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun analyzeProgress(currentDate: String) {
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val database = Firebase.database.reference.child("users").child(uid).child("viewScoresTableSideQuest").child(currentDate)
            database.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val questionCount = snapshot.childrenCount.toInt()
                    if (questionCount >= 1) {
                        totalCorrect = snapshot.child("1").child("percentage").value.toString().toInt()
                        correct = snapshot.child("1").child("correct").value.toString().toInt()
                        incorrect = snapshot.child("1").child("wrong").value.toString().toInt()
                        total = correct + incorrect
                        difference = total - correct
                        analyzeProgressBinding.totalScoreTextViewValue.text = "$totalCorrect%"
                        analyzeProgressBinding.dateTextViewValue.text = currentDate

                        analyzeProgressBinding.progressBar.max = maxTotalCorrect
                        val currentProgress = totalCorrect

                        ObjectAnimator.ofInt(
                            analyzeProgressBinding.progressBar,
                            "progress", currentProgress
                        ).setDuration(2000).start()
                        if (totalCorrect >= maxTotalCorrect) {
                            correctPercentage = 100
                            analyzeProgressBinding.percentShow.text =
                                "$correctPercentage%"
                            analyzeProgressBinding.todayScoreFeedbackViewValue.text =
                                "Congrats, you have achieved your daily target"
                        } else {

                            analyzeProgressBinding.percentShow.text =
                                "$totalCorrect%"

                            analyzeProgressBinding.todayScoreFeedbackViewValue.text =
                                "You need to get $difference + correct answer to reach daily target"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(ContentValues.TAG, "onCancelled (DatabaseError) :-> ${error.message}")
                }
            })
        }
    }
}