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
    lateinit var mainBinding: ActivitySideQuestQuizBinding
    val database = FirebaseDatabase.getInstance()
    val scoreRef = database.reference //this reference will reach the main database , We will create new child when we send the data
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser // in current user, u can reach info such as email and UID of user who logs into app using the user object

    val uid1 = FirebaseAuth.getInstance().currentUser?.uid
    //  val database1 = FirebaseDatabase.getInstance().reference.child("Users").child(uid1!!).child("dailyDairyDummy").child("2023-03-24")
    // var nherkIndex = 0 // initialize the index variable to 0
    // var currentChildSnapshot: DataSnapshot? = null // initialize the current child snapshot variable to null

    @RequiresApi(Build.VERSION_CODES.O)
    val current = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @RequiresApi(Build.VERSION_CODES.O)
    val formatted = current.format(formatter)

     @RequiresApi(Build.VERSION_CODES.O)
   //  var currentDate:String = formatted.toString()
    //var currentDate = "2023-02-25"
    var currentDate = "2023-03-28"
    var imageLink = ""
    var locationName = ""
    var questionNumber = 1
    var questionCount = 0 //number of questions in database, initial valu set to 0
    var question = "Which place is this? "
    var correctAnswer = ""
    var userAnswer = ""
    var userCorrect = 0
    var userWrong = 0
    lateinit var timer: CountDownTimer
    private val totalTime =
        25000L //25 seconds, this is defined in milliseconds; 1 sec equals 1000 milliseconds; L is long data type
    var timerContinue =
        false //this variable will show as false when timer is not running and true when it is running
    var leftTime =
        totalTime //left time value will take diff vaues when creating the timer, so initially left time variable will start equal to the totasl time
    var checker = 0
   // val currentDate: String = intent.getStringExtra("currentDate").toString()
    var dailyDairySideQuestRef = database.reference
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivitySideQuestQuizBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        DairyQuestionCreator()
        gameLogic()
//        userAnswer= mainBinding.editTextUserAnswer.text.toString() //this is user answer
//        Log.d("userAnswer",userAnswer)
        mainBinding.buttonFinish.setOnClickListener {
            pauseTimer()
            userAnswer =
                mainBinding.editTextUserAnswer.text.toString().toLowerCase().trim() //this is user answer
            if (userAnswer == "") {
                userAnswer = "Not answered"
                mainBinding.textViewFeedback.visibility = View.VISIBLE
                mainBinding.textViewFeedback.text = "Feedback: You haven't answered the question"
                mainBinding.editTextUserAnswer.setEnabled(false)
                userWrong++
                mainBinding.textViewWrong.text = userWrong.toString()
                mainBinding.buttonFinish.setEnabled(false) //to make sure the ok button doesnt get clicked twice
                checker = 1

            } else if (userAnswer == correctAnswer) {
                mainBinding.buttonFinish.setEnabled(false) //to make sure the ok button doesnt get clicked twice
                // Log.d("userAnswerCorrect",userAnswer)
                mainBinding.editTextUserAnswer.setEnabled(false)
                mainBinding.textViewFeedback.visibility = View.VISIBLE
                userCorrect++ //user correct increases
                mainBinding.textViewCorrect.text = userCorrect.toString()
                mainBinding.textViewFeedback.text = "Feedback: Correct answer"
                //also we need to add the scores the db
                checker = 1

            } else {
                // Log.d("userAnswerWrong",userAnswer)
                mainBinding.buttonFinish.setEnabled(false) //to make sure the ok button doesnt get clicked twice
                mainBinding.editTextUserAnswer.setEnabled(false)
                mainBinding.textViewFeedback.visibility = View.VISIBLE
                userWrong++
                mainBinding.textViewWrong.text = userWrong.toString()
                mainBinding.textViewFeedback.text =
                    "Feedback: Wrong answer, correct answer is $correctAnswer, your answer is $userAnswer"
                checker = 1
            }

        } //buttonFinish

        mainBinding.buttonNext.setOnClickListener {
            if (checker == 0) {
                mainBinding.textViewFeedback.visibility = View.VISIBLE
                mainBinding.textViewFeedback.text = "Feedback: Please click ok button"
            } else {
                checker = 0
                resetTimer()
                mainBinding.editTextUserAnswer.setText(null)
                mainBinding.textViewFeedback.setText(null)
                mainBinding.editTextUserAnswer.setEnabled(true)
                mainBinding.textViewFeedback.setEnabled(false)
                mainBinding.buttonFinish.setEnabled(true)
                gameLogic()
                if(questionNumber<=questionCount+1) { //new logic added

                    var questionNumber1 = questionNumber - 1
                    Log.d("questionNumber: ",questionNumber1.toString())
                    SendScore(questionNumber1)
                }
            }
        } //button Next
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun gameLogic() {
        val uid = Firebase.auth.currentUser?.uid
        var dataRef = database.reference
        if (uid != null) {

            val database = Firebase.database.reference.child("users").child(uid).child("dailyDairySideQuest").child(currentDate)
            //     val key = Firebase.database.reference.child("users").child(uid).child("dailyDairyDummy").child(currentDate).key
            //      var key_integer = key!!.toInt()
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        questionCount =
                            snapshot.childrenCount.toInt() //this will give child number of question parent
                        if (questionNumber <= questionCount) {
//                            val fetchKey = database.getKey() // No push this time because we already created the key
//                            val fetchActivity = database.child(fetchKey!!)
//                            Log.d("TestingKeyFetch1",fetchKey)
//                            fetchActivity.addValueEventListener(object: ValueEventListener {
//                                override fun  onDataChange(snaphot: DataSnapshot) {
//                                    snapshot.getValue(String::class.java)
//                                    val correctAnswerDemo = snaphot.child("location").getValue(String::class.java)
//                                    Log.d("TestingKeyFetch1",correctAnswerDemo.toString())
//                                }
//
//                                override fun onCancelled(error: DatabaseError) {
//                                    TODO("Not yet implemented")
//                                }
//
//                            })


                            correctAnswer = snapshot.child(questionNumber.toString()).child("locationName").value.toString().toLowerCase()
//                            correctAnswer = snapshot.child(key!!)
//                                .child("locationName").value.toString().toLowerCase()

                            //   imageLink = snapshot.child(questionNumber.toString())
                            imageLink = snapshot.child(questionNumber.toString())
                                .child("imageLink").value.toString()

                            mainBinding.textViewQuestion.text = question

                            startTimer()
                            Picasso.get().load(imageLink).into(mainBinding.imageDisplay)
//                           userAnswer= mainBinding.editTextUserAnswer.text.toString() //this is user answer
//                            Log.d("userAnswerAfter",userAnswer)


                        } //if for questionNumber < questionCount
                        else {//when all the questions are finished

//                                mainBinding.textViewFeedback.visibility = View.VISIBLE
//                                mainBinding.textViewFeedback.text="Feedback: Please click ok"


                            Toast.makeText(
                                applicationContext, "You answered all the questions",
                                Toast.LENGTH_SHORT
                            ).show()


                            val dialogMessage = AlertDialog.Builder(this@SideQuestQuizActivity)
                            dialogMessage.setTitle("Quiz Game")
                            dialogMessage.setMessage("Congratulations!!!\nYou have answered all the questions. Do you want to see the result?")
                            dialogMessage.setCancelable(false) //if user clicks anywhere on screen, it should not be closed, so false is set

                            dialogMessage.setPositiveButton("See Result") { dialogWindow, position ->
// if user clicks see result button, first users score should have been saved in db and result activity should be open
                                // sendScore(setChanger)
                                //put a new method here for analyze progress
                                //  analyzeProgress()
                                questionCount = snapshot.childrenCount.toInt()
                                sendScore2(questionCount)
                                val intent = Intent(this@SideQuestQuizActivity, SideQuestDisplayResultActivity::class.java)
                                intent.putExtra("currentDate",currentDate)
                                startActivity(intent)
                                finish()
                            }


                            dialogMessage.create()
                                .show() //this is needed for dialog message to appear

                        }//else
                        questionNumber++
//                        key_integer++
                    } else { //else if the currentDate does not exists
                        Toast.makeText(
                            applicationContext,
                            "No daily Dairy entry",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@SideQuestQuizActivity, SideQuestActivity::class.java)
                        //intent.putExtra("currentDate",currentDate)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        } else {

        }
    }

    private fun startTimer() {

        timer = object : CountDownTimer(leftTime, 1000) {
            override fun onTick(millisUntilFinish: Long) { //this is what we want timer to do every second, millisuntillFinish means remaining time in milliseconds untill it finish

                leftTime =
                    millisUntilFinish // this means ontick method works untill 25 sec are complete
                updateCountDownText() // it should update the time text every second like 25,24,23 ... and so on

            }

            //
            override fun onFinish() { // u will write what u want to do after timer finishes, so example in one minute timer, u can write what will happen one min later

                //       disableClickableOfOptions() //click feature should be disabled when time is up
                resetTimer()
                updateCountDownText()
                //    mainBinding.textViewQuestion.text = "Sorry, Time is up! Continue with next question."
                mainBinding.textViewFeedback.visibility = View.VISIBLE
                mainBinding.textViewFeedback.text = "Feedback: Time is up"
                userAnswer ="not answered"
                checker = 1
                mainBinding.editTextUserAnswer.setEnabled(false) //making it non editable
                userWrong++
                mainBinding.textViewWrong.text = userWrong.toString()
//                yourCorrectAnswer = "Choose option, to view answer"
//                yourAnswer = "None selected"
                timerContinue = false
                mainBinding.buttonFinish.setEnabled(false)
            }

        }.start()

        timerContinue = true

    }

    fun updateCountDownText() {

        val remainingTime: Int =
            (leftTime / 1000).toInt() // this willl give us value of remaining time in seconds
        mainBinding.textViewTime.text = remainingTime.toString()

    }

    fun pauseTimer() {

        timer.cancel()
        timerContinue = false //if this value is true, timer works, if false when not working

    }

    fun resetTimer() {

        pauseTimer() //when timer is reset first it will pause the timer
        leftTime = totalTime //this will set the timer 25 seconds again
        updateCountDownText() //also, text should be updated

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun SendScore(questionNumber1: Int) {
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
    fun sendScore2(questionCount2: Int) {
        user?.let {
            val userUID = it.uid
            val total = userCorrect + userWrong
            var percent = 0.0
            percent = (userCorrect.toDouble() / total.toDouble()) * 100.0
            for (counter in 1..questionCount2) { //before 1 till 3
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