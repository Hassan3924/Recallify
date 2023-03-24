package com.example.recallify.view.ui.feature.application.tbi_applications.thinkfast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.recallify.R

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.recallify.databinding.ActivityAnalyzeProgressBinding

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AnalyzeProgress : AppCompatActivity() {
    lateinit var analyzeProgressBinding: ActivityAnalyzeProgressBinding
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

    @RequiresApi(Build.VERSION_CODES.O)
    //  var currentDate: String = formatted.toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyzeProgressBinding = ActivityAnalyzeProgressBinding.inflate(layoutInflater)
        val view = analyzeProgressBinding.root
        setContentView(view)
        this.setTitle("AnalyzeProgress")
        var currentDate:String = intent.getStringExtra("currentDate").toString()

        analyzeProgress(currentDate)

        analyzeProgressBinding.buttonDone.setOnClickListener {
            val intent = Intent(this,ThinkFastActivity::class.java)
            startActivity(intent)
            finish()
        }



    }
    //To DO: check if current date exists or not and then procees with analyze table
    //TO DO: check if marks more than max, if yes than make it equal for percent purposes
    fun analyzeProgress(currentDate:String) {
        // currentDate="2023-02-19"
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val database =
                Firebase.database.reference.child("analyzeProgressTable").child(uid)
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
                                "Congrats, you have achieved your daily target"
                        } else {

                            analyzeProgressBinding.percentShow.text =
                                percent_correct.toInt().toString() + "%"

                            var minCorrect = maxTotalCorrect - totelCorrect
                            analyzeProgressBinding.todayScoreFeedbackViewValue.text =
                                "You need to get ${minCorrect} more scores to reach daily target"
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
                                "Congrats, you have achieved your daily target"
                        } else {

                            analyzeProgressBinding.percentShowTotalPlay.text =
                                percent_play.toInt().toString() + "%"

                            var minCorrect = maxTotalPlay - totalPlay
                            analyzeProgressBinding.todayPlayFeedbackViewValue.text =
                                "You need to play ${minCorrect} more games to reach daily target"
                        }
                    }//if

                }//onDataChange

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        }//if uid != null
    }
}