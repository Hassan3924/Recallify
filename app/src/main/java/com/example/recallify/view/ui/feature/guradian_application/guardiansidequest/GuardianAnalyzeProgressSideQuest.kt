package com.example.recallify.view.ui.feature.guradian_application.guardiansidequest

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.recallify.R
import com.example.recallify.databinding.ActivityAnalyzeProgressSideQuestBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GuardianAnalyzeProgressSideQuest : AppCompatActivity() {
    lateinit var analyzeProgressBinding:ActivityAnalyzeProgressSideQuestBinding
    var totelCorrect = 0

    var maxTotalCorrect = 100

    var percent_correct=0

    var incorrect = 0
    var correct = 0
    var total =0
    var difference = 0
    var tbi_uid=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyzeProgressBinding = ActivityAnalyzeProgressSideQuestBinding.inflate(layoutInflater)
        val view = analyzeProgressBinding.root
        setContentView(view)
        var currentDate:String = intent.getStringExtra("currentDate").toString()

        getTbiUid(currentDate)

        analyzeProgressBinding.buttonDone.setOnClickListener {
            val intent = Intent(this,GuardianSideQuestActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun analyzeProgress(currentDate:String) {
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val database = Firebase.database.reference.child("users").child(tbi_uid).child("viewScoresTableSideQuest").child(currentDate)
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
                                "Congrats, User has achieved the daily target"
                        } else {

                            analyzeProgressBinding.percentShow.text =
                                totelCorrect.toString() + "%"

                            var minCorrect = maxTotalCorrect - totelCorrect
                            analyzeProgressBinding.todayScoreFeedbackViewValue.text =
                                "User need to get ${difference} + correct answer to reach daily target"
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }//if uid ! = null
    }
    fun getTbiUid(currentDate:String){
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val database = Firebase.database.reference.child("users").child("GuardiansLinkTable").child(uid)

            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var questionCount = snapshot.childrenCount.toInt()
                    if(questionCount>=1) {
                        tbi_uid = snapshot.child("TBI_ID").value.toString()

                        analyzeProgress(currentDate)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        }

    }
}