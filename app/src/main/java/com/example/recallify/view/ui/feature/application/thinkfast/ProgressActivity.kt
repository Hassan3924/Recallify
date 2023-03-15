package com.example.recallify.view.ui.feature.application.thinkfast

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.recallify.databinding.ActivityProgressBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProgressActivity : AppCompatActivity() {

    var totalCorrect = 0
    var totalPlay = 0
    var maxTotalCorrect = 10
    var maxTotalPlay = 2

    private lateinit var progressActivityBinding: ActivityProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressActivityBinding = ActivityProgressBinding.inflate(layoutInflater)
        val progressView = progressActivityBinding.root

        setContentView(progressView)

        this.title = "Progress"
        val currentDate: String = intent.getStringExtra("currentDate").toString()
        progressAnalysis(currentDate)

        progressActivityBinding.buttonDone.setOnClickListener {
            val intent = Intent(this, ThinkFastActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun progressAnalysis(currentDate: String) {
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            val database =
                Firebase.database.reference.child("analyzeProgressTable").child(userId)
                    .child(currentDate)

            database.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {

                    val questionCount = snapshot.childrenCount.toInt()
                    if (questionCount == 2) {
                        totalPlay = snapshot.child("totalPlay").value.toString().toInt()
                        totalCorrect = snapshot.child("totalCorrect").value.toString().toInt()

                        progressActivityBinding.totalPLayTextViewValue.text = totalPlay.toString()
                        progressActivityBinding.totalScoreTextViewValue.text = totalCorrect.toString()
                        progressActivityBinding.dateTextViewValue.text = currentDate
                        progressActivityBinding.progressBar.max = maxTotalCorrect

                        val currentProgress = totalCorrect

                        ObjectAnimator.ofInt(
                            progressActivityBinding.progressBar,
                            "progress",
                            currentProgress
                        ).setDuration(2000).start()

                        var percentageCorrection: Double =
                            (totalCorrect.toDouble() / maxTotalCorrect.toDouble()) * 100.0

                        if (totalCorrect >= maxTotalCorrect) {
                            percentageCorrection = 100.0
                            progressActivityBinding.percentShow.text = percentageCorrection.toInt().toString() + "%"
                            progressActivityBinding.todayScoreFeedbackViewValue.text = "Congrats, you have achieved your daily target"
                        } else {
                            progressActivityBinding.percentShow.text = percentageCorrection.toInt().toString() + "%"

                            val minCorrect = maxTotalCorrect - totalCorrect
                            progressActivityBinding.todayScoreFeedbackViewValue.text = "You need to get $minCorrect more scores to reach daily target"
                        }

                        val currentProgressTotalPlay = totalPlay

                        progressActivityBinding.progressBarTotalPlay.max = maxTotalPlay
                        ObjectAnimator.ofInt(
                            progressActivityBinding.progressBarTotalPlay,
                            "progress",
                            currentProgressTotalPlay
                        ).setDuration(2000).start()

                        var percentagePlay = (totalPlay.toDouble() / maxTotalPlay.toDouble()) * 100.0
                        Log.d("Percent: ", percentagePlay.toInt().toString())
                        if (totalPlay >= maxTotalPlay) {
                            percentagePlay = 100.0
                            progressActivityBinding.percentShowTotalPlay.text = percentagePlay.toInt().toString() + "%"
                            progressActivityBinding.todayPlayFeedbackViewValue.text = "Congrats, you have achieved your daily target"
                        } else {
                            progressActivityBinding.percentShowTotalPlay.text = percentagePlay.toInt().toString() + "%"
                            val minCorrect = maxTotalPlay - totalPlay
                            progressActivityBinding.todayPlayFeedbackViewValue.text = "You need to play $minCorrect more games to reach daily target"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }
}