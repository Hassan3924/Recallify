package com.example.recallify.view.ui.feature.application.tbi_applications.thinkfast

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.recallify.databinding.ActivityQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class QuizActivity : AppCompatActivity() {

    private lateinit var quizActivityBinding: ActivityQuizBinding

    private val database = FirebaseDatabase.getInstance()

    private var question = ""
    private var answerA = ""
    private var answerB = ""
    private var answerC = ""
    private var answerD = ""
    private var correctAnswer = ""
    private var questionCount = 0
    private var questionNumber = 1

    private var userAnswer = ""
    private var userCorrect = 0
    private var userWrong = 0

    var yourAnswer = ""

    private lateinit var timer: CountDownTimer
    private val totalTime = 25000L
    var timerContinue = false
    var leftTime = totalTime

    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser

    private val scoreRef = database.reference

    private var questionNumber1 = 0
    private var yourCorrectAnswer = ""
    private var imageName = ""
    private var percent = 0.0

    @RequiresApi(Build.VERSION_CODES.O)
    private val current = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatted = current.format(formatter)

    @RequiresApi(Build.VERSION_CODES.O)
    var currentDate: String = formatted.toString()

    var checker = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        quizActivityBinding = ActivityQuizBinding.inflate(layoutInflater)
        val view = quizActivityBinding.root
        setContentView(view)

        val setChanger1: Int = intent.getIntExtra("setChangerFromConfirm", 0)
        val databaseReference = database.reference.child("questionSets").child(setChanger1.toString())

        gameLogic(setChanger1, databaseReference)

        quizActivityBinding.textViewA.setOnClickListener {
            quizActivityBinding.buttonNext.isEnabled = true
            checker = 1
            pauseCountDownTimer()
            userAnswer = "a"
            if (correctAnswer == userAnswer) {

                quizActivityBinding.textViewA.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizActivityBinding.textViewCorrect.text = userCorrect.toString()
                yourAnswer = answerA
                yourCorrectAnswer = answerA

            } else {

                quizActivityBinding.textViewA.setBackgroundColor(Color.RED)
                userWrong++
                quizActivityBinding.textViewWrong.text = userWrong.toString()
                queryAnswerFromDatabase()
                yourAnswer = answerA

            }

            disableClickableOfOptions()
        }
        quizActivityBinding.textViewB.setOnClickListener {
            quizActivityBinding.buttonNext.isEnabled = true
            checker = 1
            pauseCountDownTimer()

            userAnswer = "b"
            if (correctAnswer == userAnswer) {

                quizActivityBinding.textViewB.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizActivityBinding.textViewCorrect.text = userCorrect.toString()
                yourAnswer = answerB
                yourCorrectAnswer = answerB

            } else {

                quizActivityBinding.textViewB.setBackgroundColor(Color.RED)
                userWrong++
                quizActivityBinding.textViewWrong.text = userWrong.toString()
                queryAnswerFromDatabase()
                yourAnswer = answerB

            }

            disableClickableOfOptions()

        }
        quizActivityBinding.textViewC.setOnClickListener {
            quizActivityBinding.buttonNext.isEnabled = true
            checker = 1
            pauseCountDownTimer()

            userAnswer = "c"
            if (correctAnswer == userAnswer) {

                quizActivityBinding.textViewC.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizActivityBinding.textViewCorrect.text = userCorrect.toString()
                yourCorrectAnswer = answerC
                yourAnswer = answerC

            } else {

                quizActivityBinding.textViewC.setBackgroundColor(Color.RED)
                userWrong++
                quizActivityBinding.textViewWrong.text = userWrong.toString()
                queryAnswerFromDatabase()
                yourAnswer = answerC
            }
            disableClickableOfOptions()

        }
        quizActivityBinding.textViewD.setOnClickListener {
            quizActivityBinding.buttonNext.isEnabled = true
            checker = 1
            pauseCountDownTimer()

            userAnswer = "d"
            if (correctAnswer == userAnswer) {

                quizActivityBinding.textViewD.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizActivityBinding.textViewCorrect.text = userCorrect.toString()
                yourCorrectAnswer = answerD
                yourAnswer = answerD

            } else {

                quizActivityBinding.textViewD.setBackgroundColor(Color.RED)
                userWrong++
                quizActivityBinding.textViewWrong.text = userWrong.toString()
                queryAnswerFromDatabase()
                yourAnswer = answerD
            }

            disableClickableOfOptions()

        }

        quizActivityBinding.buttonNext.setOnClickListener {
            if (checker == 1) {
                quizActivityBinding.buttonNext.isEnabled = true
                checker = 0
                resetCountDownTimer()
                gameLogic(setChanger1, databaseReference)
                if(questionNumber<=questionCount+1) { //new logic added
                    questionNumber1 = questionNumber - 1
                    Log.d("questionNumber: ",questionNumber1.toString())
                    viewScoreFromDatabase()
                }
            } else {
                quizActivityBinding.buttonNext.isEnabled = false
            }
        }
    }

    private fun gameLogic(setChanger: Int, databaseReference: DatabaseReference) {
        restoreOptions()
        databaseReference.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                imageName = snapshot.child("imageName").value.toString()
                questionCount = snapshot.childrenCount.toInt()
                if (questionNumber < questionCount) {

                    question = snapshot.child(questionNumber.toString()).child("q").value.toString()
                    answerA = snapshot.child(questionNumber.toString()).child("a").value.toString()
                    answerB = snapshot.child(questionNumber.toString()).child("b").value.toString()
                    answerC = snapshot.child(questionNumber.toString()).child("c").value.toString()
                    answerD = snapshot.child(questionNumber.toString()).child("d").value.toString()
                    correctAnswer = snapshot.child(questionNumber.toString()).child("answer").value.toString()

                    quizActivityBinding.textViewQuestion.text = question
                    quizActivityBinding.textViewA.text = answerA
                    quizActivityBinding.textViewB.text = answerB
                    quizActivityBinding.textViewC.text = answerC
                    quizActivityBinding.textViewD.text = answerD

                    launchCountDownTimer()

                } else {
                    Toast.makeText(applicationContext, "You answered all the questions", Toast.LENGTH_SHORT).show()


                    val dialogMessage = AlertDialog.Builder(this@QuizActivity)
                    dialogMessage.setTitle("Quiz Game")
                    dialogMessage.setMessage("Congratulations!!!\nYou have answered all the questions. Do you want to see the result?")
                    dialogMessage.setCancelable(false)

                    dialogMessage.setPositiveButton("See Result") { _, _ ->
                        sendScoreToDatabase(setChanger)
                        analyzeProgress()

                    }
                    dialogMessage.create().show()
                }
                questionNumber++
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun analyzeProgress() {
        val uid = Firebase.auth.currentUser?.uid
        val dataRef = database.reference
        if (uid != null) {
            val database = Firebase.database.reference.child("analyzeProgressTable").child(uid).child(currentDate)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (percent >= 80) {
                            val totalPlay = snapshot.child("totalPlay").value.toString().toInt() + 1
                            val totalCorrect = snapshot.child("totalCorrect").value.toString().toInt() + userCorrect
                            dataRef.child("analyzeProgressTable").child(uid).child(currentDate).child("totalPlay").setValue(totalPlay)
                            dataRef.child("analyzeProgressTable").child(uid).child(currentDate).child("totalCorrect").setValue(totalCorrect)
                        }
                    } else {
                        if (percent >= 80) {
                            dataRef.child("analyzeProgressTable").child(uid).child(currentDate).child("totalPlay").setValue(1)
                            dataRef.child("analyzeProgressTable").child(uid).child(currentDate).child("totalCorrect").setValue(userCorrect)
                        } else {
                            dataRef.child("analyzeProgressTable").child(uid).child(currentDate).child("totalPlay").setValue(0)
                            dataRef.child("analyzeProgressTable").child(uid).child(currentDate).child("totalCorrect").setValue(0)
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "onCancelled: (error in database) -> ${error.message}")
                }
            })
        }
    }

    private fun queryAnswerFromDatabase() { //so when there is correct answer, it should turn green
        //  yourCorrectAnswer = correctAnswer

        if (correctAnswer == "a") {
            yourCorrectAnswer = answerA
        }
        if (correctAnswer == "b") {
            yourCorrectAnswer = answerB
        }
        if (correctAnswer == "c") {
            yourCorrectAnswer = answerC
        }
        if (correctAnswer == "d") {
            yourCorrectAnswer = answerD
        }

        when (correctAnswer) {

            "a" -> quizActivityBinding.textViewA.setBackgroundColor(Color.GREEN)
            "b" -> quizActivityBinding.textViewB.setBackgroundColor(Color.GREEN)
            "c" -> quizActivityBinding.textViewC.setBackgroundColor(Color.GREEN)
            "d" -> quizActivityBinding.textViewD.setBackgroundColor(Color.GREEN)
        }
    }

    private fun disableClickableOfOptions() { //when user clicks the option, other options have to be disabled

        quizActivityBinding.textViewA.isClickable = false
        quizActivityBinding.textViewB.isClickable = false
        quizActivityBinding.textViewC.isClickable = false
        quizActivityBinding.textViewD.isClickable = false

    }

    private fun restoreOptions() {
        quizActivityBinding.textViewA.setBackgroundColor(Color.WHITE)
        quizActivityBinding.textViewB.setBackgroundColor(Color.WHITE)
        quizActivityBinding.textViewC.setBackgroundColor(Color.WHITE)
        quizActivityBinding.textViewD.setBackgroundColor(Color.WHITE)

        quizActivityBinding.textViewA.isClickable = true
        quizActivityBinding.textViewB.isClickable = true
        quizActivityBinding.textViewC.isClickable = true
        quizActivityBinding.textViewD.isClickable = true

    }

    private fun launchCountDownTimer() {
        timer = object : CountDownTimer(leftTime, 1000) {
            override fun onTick(millisUntilFinish: Long) {
                leftTime = millisUntilFinish
                updateCountDownTimerForText()
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                quizActivityBinding.buttonNext.isEnabled = true
                checker = 1
                disableClickableOfOptions()
                resetCountDownTimer()
                updateCountDownTimerForText()
                quizActivityBinding.textViewQuestion.text = "Sorry, Time is up! Continue with next question."
                userWrong++
                quizActivityBinding.textViewWrong.text = userWrong.toString()
                yourCorrectAnswer = "Choose option, to view answer"
                yourAnswer = "None selected"
                timerContinue = false
            }
        }.start()
        timerContinue = true
    }

    private fun updateCountDownTimerForText() {
        val remainingTime: Int = (leftTime / 1000).toInt()
        quizActivityBinding.textViewTime.text = remainingTime.toString()
    }

    private fun pauseCountDownTimer() {
        timer.cancel()
        timerContinue = false
    }

    private fun resetCountDownTimer() {

        pauseCountDownTimer()
        leftTime = totalTime
        updateCountDownTimerForText()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendScoreToDatabase(setChanger: Int) {

        user?.let {
            val userUID = it.uid
            val total = userCorrect + userWrong
            percent = (userCorrect.toDouble() / total.toDouble()) * 100.0
            scoreRef.child("scoresTable2").child(userUID).child("correct")
                .setValue(userCorrect)
            scoreRef.child("scoresTable2").child(userUID).child("setChanger")
                .setValue(setChanger)
            scoreRef.child("scoresTable2").child(userUID).child("ScorePercentage")
                .setValue(percent.toInt())
            scoreRef.child("scoresTable2").child(userUID).child("wrong").setValue(userWrong)
                .addOnSuccessListener {
                    val intent = Intent(this@QuizActivity, DisplayResultsActivity::class.java)
                    intent.putExtra("currentDate", currentDate)
                    startActivity(intent)
                    finish()

                }
            for (counter in 1..5) {
                scoreRef.child("viewScoresTable").child(userUID).child(currentDate).child(counter.toString()).child("percentage").setValue(percent)
                scoreRef.child("viewScoresTable").child(userUID).child(currentDate).child(counter.toString()).child("wrong").setValue(userWrong)
                scoreRef.child("viewScoresTable").child(userUID).child(currentDate).child(counter.toString()).child("correct").setValue(userCorrect)
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun viewScoreFromDatabase() {
        user?.let {
            val userUID = it.uid

            scoreRef.child("viewScoresTable").child(userUID).child(currentDate).child(questionNumber1.toString()).child("correctAnswer").setValue(yourCorrectAnswer)
            scoreRef.child("viewScoresTable").child(userUID).child(currentDate).child(questionNumber1.toString()).child("YourAnswer").setValue(yourAnswer)
            scoreRef.child("viewScoresTable").child(userUID).child(currentDate).child(questionNumber1.toString()).child("imageName").setValue(imageName)
            scoreRef.child("viewScoresTable").child(userUID).child(currentDate).child(questionNumber1.toString()).child("currentDate").setValue(currentDate)
            scoreRef.child("viewScoresTable").child(userUID).child(currentDate).child(questionNumber1.toString()).child("question").setValue(question)


        }
    }

}