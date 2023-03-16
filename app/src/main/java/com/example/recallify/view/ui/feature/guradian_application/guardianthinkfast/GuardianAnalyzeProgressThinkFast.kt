package com.example.recallify.view.ui.feature.guradian_application.guardianthinkfast

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.recallify.databinding.ActivityGuardianAnalyzeProgressThinkFastBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GuardianAnalyzeProgressThinkFast : AppCompatActivity() {

    lateinit var analyzeProgressBinding:ActivityGuardianAnalyzeProgressThinkFastBinding
    var totelCorrect = 0
    var totalPlay = 0
    var maxTotalCorrect = 10 //before 6
    var maxTotalPlay= 2
    @RequiresApi(Build.VERSION_CODES.O)
    val current = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @RequiresApi(Build.VERSION_CODES.O)
    val formatted = current.format(formatter)

    var tbi_uid=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyzeProgressBinding =ActivityGuardianAnalyzeProgressThinkFastBinding.inflate(layoutInflater)
        val view = analyzeProgressBinding.root
        setContentView(view)
        var currentDate:String = intent.getStringExtra("currentDate").toString()

        getTbiUid(currentDate)

      //  analyzeProgress(currentDate)

        analyzeProgressBinding.buttonDone.setOnClickListener {
            val intent = Intent(this,GuardianThinkFastActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun analyzeProgress(currentDate:String) {
        // currentDate="2023-02-19"
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val database =
                Firebase.database.reference.child("analyzeProgressTable").child(tbi_uid)
                    .child(currentDate)

            database.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    var questionCount = snapshot.childrenCount.toInt()
                    if(questionCount==2) {
                        totalPlay = snapshot.child("totalPlay").value.toString().toInt()
                        totelCorrect = snapshot.child("totalCorrect").value.toString().toInt()

                        analyzeProgressBinding.totalPLayTextViewValue.text = totalPlay.toString()
                        analyzeProgressBinding.totalScoreTextViewValue.text =
                            totelCorrect.toString()
                        analyzeProgressBinding.dateTextViewValue.text = currentDate



                        analyzeProgressBinding.progressBar.max = maxTotalCorrect
//Total Score related code
                        val currentProgress = totelCorrect

                        ObjectAnimator.ofInt(
                            analyzeProgressBinding.progressBar,
                            "progress",
                            currentProgress
                        ).setDuration(2000).start()

                        var percent_correct: Double =
                            (totelCorrect.toDouble() / maxTotalCorrect.toDouble()) * 100.0

                        if (totelCorrect >= maxTotalCorrect) {
                            percent_correct = 100.0
                            analyzeProgressBinding.percentShow.text =
                                percent_correct.toInt().toString() + "%"
                            analyzeProgressBinding.todayScoreFeedbackViewValue.text =
                                "Congrats,User has achieved the daily target"
                        } else {

                            analyzeProgressBinding.percentShow.text =
                                percent_correct.toInt().toString() + "%"

                            var minCorrect = maxTotalCorrect - totelCorrect
                            analyzeProgressBinding.todayScoreFeedbackViewValue.text =
                                "User need to get ${minCorrect} + scores to reach daily target"
                        }

                        //Total Play related code

                        var currentProgressTotalPlay = totalPlay


                        //for Total play progress Bar
                        analyzeProgressBinding.progressBarTotalPlay.max = maxTotalPlay
                        ObjectAnimator.ofInt(
                            analyzeProgressBinding.progressBarTotalPlay,
                            "progress",
                            currentProgressTotalPlay
                        ).setDuration(2000).start()

                        var percent_play = (totalPlay.toDouble() / maxTotalPlay.toDouble()) * 100.0
                        Log.d("Percent: ", percent_play.toInt().toString())
                        if (totalPlay >= maxTotalPlay) {
                            percent_play = 100.0
                            analyzeProgressBinding.percentShowTotalPlay.text =
                                percent_play.toInt().toString() + "%"
                            analyzeProgressBinding.todayPlayFeedbackViewValue.text =
                                "Congrats,User has achieved the daily target"
                        } else {

                            analyzeProgressBinding.percentShowTotalPlay.text =
                                percent_play.toInt().toString() + "%"

                            var minCorrect = maxTotalPlay - totalPlay
                            analyzeProgressBinding.todayPlayFeedbackViewValue.text =
                                "User need to play ${minCorrect} more games to reach daily target"
                        }
                    }//if

                }//onDataChange

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        }//if uid != null

    }//analyzeProgress

    fun getTbiUid(currentDate:String){
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val database = Firebase.database.reference.child("users").child("GuardiansLinkTable").child(uid)

            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var questionCount = snapshot.childrenCount.toInt()
                    if(questionCount==1) {
                        tbi_uid = snapshot.child("TBIID").value.toString()

                    analyzeProgress(currentDate)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        }

    }
}//class