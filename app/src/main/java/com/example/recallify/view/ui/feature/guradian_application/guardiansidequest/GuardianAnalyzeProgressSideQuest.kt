package com.example.recallify.view.ui.feature.guradian_application.guardiansidequest

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.recallify.databinding.ActivityAnalyzeProgressSideQuestBinding
import com.example.recallify.view.common.resources.Constants.USER_ROOT_PATH
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GuardianAnalyzeProgressSideQuest : AppCompatActivity() {
    lateinit var analyzeProgressBinding: ActivityAnalyzeProgressSideQuestBinding
    var totalCorrect = 0
    var maxTotalCorrect = 100
    var percentCorrect = 0
    var incorrect = 0
    var correct = 0
    var total = 0
    var difference = 0
    var tbiUserID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyzeProgressBinding = ActivityAnalyzeProgressSideQuestBinding.inflate(layoutInflater)
        val view = analyzeProgressBinding.root
        setContentView(view)
        val currentDate: String = intent.getStringExtra("currentDate").toString()

        getTbiUid(currentDate)

        analyzeProgressBinding.buttonDone.setOnClickListener {
            val intent = Intent(this, GuardianSideQuestActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun analyzeProgress(currentDate: String) {
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val database = Firebase.database.reference.child("users").child(tbiUserID)
                .child("viewScoresTableSideQuest").child(currentDate)
            database.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val questionCount = snapshot.childrenCount.toInt()
                    if (questionCount >= 1) {
                        totalCorrect =
                            snapshot.child("1").child("percentage").value.toString().toInt()
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
                            percentCorrect = 100
                            analyzeProgressBinding.percentShow.text =
                                "$percentCorrect%"
                            analyzeProgressBinding.todayScoreFeedbackViewValue.text =
                                "Congrats, User has achieved their daily target \uD83D\uDC4F"
                        } else {

                            analyzeProgressBinding.percentShow.text =
                                "$totalCorrect%"

                            var answer1 = "scores"
                            if (difference == 1) {
                                answer1 = "score"
                            }
                            analyzeProgressBinding.todayScoreFeedbackViewValue.text =
                                "User needs to get $difference + $answer1 to reach their daily target \uD83D\uDE0A"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("onCancelled Message", "The error returned is: ${error.message}")
                }
            })
        }
    }

    private fun getTbiUid(currentDate: String) {
        val userId = Firebase.auth.currentUser?.uid!!

        val database =
            Firebase.database.reference
                .child(USER_ROOT_PATH)
                .child("GuardiansLinkTable")
                .child(userId)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val questionCount = snapshot.childrenCount.toInt()
                if (questionCount >= 1) {
                    tbiUserID = snapshot.child("TBI_ID").value.toString()

                    analyzeProgress(currentDate)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}