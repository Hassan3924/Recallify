package com.example.recallify.view.ui.feature.application.tbi_applications.sidequest

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.recallify.databinding.ActivitySideQuestQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SideQuestQuizActivity : AppCompatActivity() {

    private lateinit var sideQuestQuizBinding: ActivitySideQuestQuizBinding
    private lateinit var timer: CountDownTimer

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val totalTime = 25000L


    private val scoreRef: DatabaseReference = database.reference
    private val user: FirebaseUser? = auth.currentUser

   // private var currentDate = "2023-02-25"
    private var imageLink = ""
    private var questionNumber = 1
    private var questionCount = 0
    private var question = "Which place is this? "
    private var correctAnswer = ""
    private var userAnswer = ""
    private var userCorrect = 0
    private var userWrong = 0
    private var timerContinue = false
    private var leftTime = totalTime
    private var checker = 0

    var dailyDairySideQuestRef = database.reference
    @RequiresApi(Build.VERSION_CODES.O)
    val current = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @RequiresApi(Build.VERSION_CODES.O)
    val formatted = current.format(formatter)

    @RequiresApi(Build.VERSION_CODES.O)
    var currentDate:String = formatted.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sideQuestQuizBinding = ActivitySideQuestQuizBinding.inflate(layoutInflater)
        DairyQuestionCreator()
        val view = sideQuestQuizBinding.root
        setContentView(view)
        gameLogic()
        sideQuestQuizBinding.buttonFinish.setOnClickListener {
            pauseTimer()
            userAnswer =
                sideQuestQuizBinding.editTextUserAnswer.text.toString().lowercase(Locale.ROOT).trim()
            when (userAnswer) {
                "" -> {
                    userAnswer = "Not answered"
                    sideQuestQuizBinding.textViewFeedback.visibility = View.VISIBLE
                    sideQuestQuizBinding.textViewFeedback.text = "Feedback: You haven't answered the question"
                    sideQuestQuizBinding.editTextUserAnswer.isEnabled = false
                    userWrong++
                    sideQuestQuizBinding.textViewWrong.text = userWrong.toString()
                    sideQuestQuizBinding.buttonFinish.isEnabled = false
                    checker = 1
                }

                correctAnswer -> {
                    sideQuestQuizBinding.buttonFinish.isEnabled = false
                    sideQuestQuizBinding.editTextUserAnswer.isEnabled = false
                    sideQuestQuizBinding.textViewFeedback.visibility = View.VISIBLE
                    userCorrect++
                    sideQuestQuizBinding.textViewCorrect.text = userCorrect.toString()
                    sideQuestQuizBinding.textViewFeedback.text = "Feedback: Correct answer"
                    checker = 1
                }

                else -> {
                    sideQuestQuizBinding.buttonFinish.isEnabled = false
                    sideQuestQuizBinding.editTextUserAnswer.isEnabled = false
                    sideQuestQuizBinding.textViewFeedback.visibility = View.VISIBLE
                    userWrong++
                    sideQuestQuizBinding.textViewWrong.text = userWrong.toString()
                    sideQuestQuizBinding.textViewFeedback.text = "Feedback: Wrong answer, correct answer is $correctAnswer, your answer is $userAnswer"
                    checker = 1
                }
            }
        }

        sideQuestQuizBinding.buttonNext.setOnClickListener {
            if (checker == 0) {
                sideQuestQuizBinding.textViewFeedback.visibility = View.VISIBLE
                sideQuestQuizBinding.textViewFeedback.text = "Feedback: Please click ok button"
            } else {
                checker = 0
                resetTimer()
                sideQuestQuizBinding.editTextUserAnswer.text = null
                sideQuestQuizBinding.textViewFeedback.text = null
                sideQuestQuizBinding.editTextUserAnswer.isEnabled = true
                sideQuestQuizBinding.textViewFeedback.isEnabled = false
                sideQuestQuizBinding.buttonFinish.isEnabled = true
                gameLogic()
                val questionNumber1 = questionNumber - 1
                sendScore(questionNumber1)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun gameLogic() {
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val database = Firebase.database.reference.child("users").child(uid).child("dailyDairySideQuest").child(currentDate)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        questionCount =
                            snapshot.childrenCount.toInt()
                        if (questionNumber <= questionCount) {
                            correctAnswer = snapshot.child(questionNumber.toString()).child("locationName").value.toString().lowercase(Locale.ROOT)
                            imageLink = snapshot.child(questionNumber.toString()).child("imageLink").value.toString()

                            sideQuestQuizBinding.textViewQuestion.text = question

                            startTimer()
                            Picasso.get().load(imageLink).into(sideQuestQuizBinding.imageDisplay)
                         //   Picasso.get().load(imageLink).fit().centerCrop().into(sideQuestQuizBinding.imageDisplay)

                        } else {
                            Toast.makeText(
                                applicationContext, "You answered all the questions",
                                Toast.LENGTH_SHORT
                            ).show()

                            val dialogMessage = AlertDialog.Builder(this@SideQuestQuizActivity)
                            dialogMessage.setTitle("Quiz Game")
                            dialogMessage.setMessage("Congratulations!!!\nYou have answered all the questions. Do you want to see the result?")
                            dialogMessage.setCancelable(false)
                            dialogMessage.setPositiveButton("See Result") { _, _ ->
                                questionCount = snapshot.childrenCount.toInt()
                                sendSideQuestScore(questionCount)
                                val intent = Intent(this@SideQuestQuizActivity, SideQuestDisplayResultActivity::class.java)
                                intent.putExtra("currentDate", currentDate)
                                startActivity(intent)
                                finish()
                            }
                            dialogMessage.create()
                                .show()

                        }
                        questionNumber++
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "No daily Dairy entry",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@SideQuestQuizActivity, SideQuestActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i(ContentValues.TAG, "onCancelled (UserData_Side_quest): -> ${error.message}")
                }
            })
        } else {
            Log.i(ContentValues.TAG, "The user is null and cannot be found! Please try again")
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(leftTime, 1000) {
            override fun onTick(millisUntilFinish: Long) {
                leftTime = millisUntilFinish
                updateCountDownText()
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                resetTimer()
                updateCountDownText()
                sideQuestQuizBinding.textViewFeedback.visibility = View.VISIBLE
                sideQuestQuizBinding.textViewFeedback.text = "Feedback: Time is up"
                userAnswer = "not answered"
                checker = 1
                sideQuestQuizBinding.editTextUserAnswer.isEnabled = false
                userWrong++
                sideQuestQuizBinding.textViewWrong.text = userWrong.toString()
                timerContinue = false
                sideQuestQuizBinding.buttonFinish.isEnabled = false
            }
        }.start()
        timerContinue = true
    }

    private fun updateCountDownText() {
        val remainingTime: Int = (leftTime / 1000).toInt()
        sideQuestQuizBinding.textViewTime.text = remainingTime.toString()
    }

    private fun pauseTimer() {
        timer.cancel()
        timerContinue = false
    }

    private fun resetTimer() {
        pauseTimer()
        leftTime = totalTime
        updateCountDownText()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendScore(questionNumber1: Int) {
        user?.let {
            val userUID = it.uid
            scoreRef.child("users").child(userUID).child("viewScoresTableSideQuest").child(currentDate).child(questionNumber1.toString()).child("correctAnswer").setValue(correctAnswer)
            scoreRef.child("users").child(userUID).child("viewScoresTableSideQuest").child(currentDate).child(questionNumber1.toString()).child("YourAnswer").setValue(userAnswer)
            scoreRef.child("users").child(userUID).child("viewScoresTableSideQuest").child(currentDate).child(questionNumber1.toString()).child("imageName").setValue(imageLink)
            scoreRef.child("users").child(userUID).child("viewScoresTableSideQuest").child(currentDate).child(questionNumber1.toString()).child("currentDate").setValue(currentDate)
            scoreRef.child("users").child(userUID).child("viewScoresTableSideQuest").child(currentDate).child(questionNumber1.toString()).child("question").setValue(question)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendSideQuestScore(sideQuestQuestionCount: Int) {
        user?.let {
            val userUID = it.uid
            val total = userCorrect + userWrong
            val percent: Double = (userCorrect.toDouble() / total.toDouble()) * 100.0
            for (counter in 1..sideQuestQuestionCount) { //before 1 till 3
                scoreRef.child("users").child(userUID).child("viewScoresTableSideQuest").child(currentDate).child(counter.toString()).child("percentage").setValue(percent.toInt())
                scoreRef.child("users").child(userUID).child("viewScoresTableSideQuest").child(currentDate).child(counter.toString()).child("wrong").setValue(userWrong)
                scoreRef.child("users").child(userUID).child("viewScoresTableSideQuest").child(currentDate).child(counter.toString()).child("correct").setValue(userCorrect)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun DairyQuestionCreator(){
        val uid = Firebase.auth.currentUser?.uid
        // val currentDate = "2023-03-24" // example date
        var counterGen=0
        if (uid != null) {
            val dailyDairyDummyRef =
                Firebase.database.reference.child("users").child(uid).child("dailyDairyDummy")
                    .child(currentDate)

            dailyDairyDummyRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
                            counterGen++
                            val nrgwKey = childSnapshot.key // e.g. "NRGw7"
                            val imageLink = childSnapshot.child("imageLink").value // retrieve imageLink value
                            val location = childSnapshot.child("locationName").value // retrieve location value
                            // do something with the retrieved values, e.g. display them in UI
                            Log.d("nrgkey_checker", "nrgwKey: $nrgwKey, imageLink: $imageLink, location: $location")
                            dailyDairySideQuestRef.child("users").child(uid).child("dailyDairySideQuest").child(currentDate).child(counterGen.toString()).child("imageLink").setValue(imageLink)
                            dailyDairySideQuestRef.child("users").child(uid).child("dailyDairySideQuest").child(currentDate).child(counterGen.toString()).child("locationName").setValue(location)

                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d(ContentValues.TAG, "onCancelled: $error")
                }
            })
        }
    }
}