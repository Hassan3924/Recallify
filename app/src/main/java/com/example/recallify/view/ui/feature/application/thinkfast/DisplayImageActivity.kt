package com.example.recallify.view.ui.feature.application.thinkfast

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.recallify.databinding.ActivityDisplayImageBinding
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class DisplayImageActivity : AppCompatActivity() {

    private lateinit var displayImageBinding : ActivityDisplayImageBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var timer: CountDownTimer
    private val totalTime = 10000L

    private var leftTime = totalTime
    private var timerContinue = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        displayImageBinding = ActivityDisplayImageBinding.inflate(layoutInflater)
        val view = displayImageBinding.root
        setContentView(view)

        val setChanger1: Int = intent.getIntExtra("setChangerFromConfirm", 0)
        val databaseReference = database.reference.child("questionSets").child(setChanger1.toString()).child("imageName")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val imageUrl = dataSnapshot.value.toString()
                Picasso.get().load(imageUrl).into(displayImageBinding.imageDisplay)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "onCancelledDatabase (RealtimeDatabase): -> ${error.message}")
            }
        })

        launchCountDownTimer()
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val intent = Intent(this@DisplayImageActivity, QuizActivity::class.java)
            intent.putExtra("setChangerFromConfirm", setChanger1)
            startActivity(intent)
            finish()
        }, 9000)
    }

    private fun launchCountDownTimer() {

        timer = object : CountDownTimer(leftTime, 1000) {
            override fun onTick(millisUntilFinish: Long) {
                leftTime =
                    millisUntilFinish
                updateCountDownTimerForText()
            }

            override fun onFinish() {
                resetCountDownTimer()
                updateCountDownTimerForText()
                timerContinue = false
            }
        }.start()
        timerContinue = true
    }

    fun updateCountDownTimerForText() {
        val remainingTime: Int = (leftTime / 1000).toInt()
        displayImageBinding.textViewTime.text = remainingTime.toString()
    }

    private fun pauseCountDownTimer() {
        timer.cancel()
        timerContinue = false
    }

    fun resetCountDownTimer() {
        pauseCountDownTimer()
        leftTime = totalTime
        updateCountDownTimerForText()
    }
}