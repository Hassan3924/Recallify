package com.example.recallify.view.ui.feature.guradian_application.guardiansidequest

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.recallify.databinding.ActivityGuardianDisplayResultsSideQuestBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class GuardianDisplayResultsSideQuest : AppCompatActivity() {
    lateinit var displayResultsBinding: ActivityGuardianDisplayResultsSideQuestBinding

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var questionCount = 0
    var questionNumber = 1
    var yourAnswer = ""
    var correct = 0
    var wrong = 0
    var correctAnswer = ""
    var imageName = ""
    var percentage = 0
    var question = ""
    var tbiUserId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        displayResultsBinding =
            ActivityGuardianDisplayResultsSideQuestBinding.inflate(layoutInflater)
        val view = displayResultsBinding.root
        setContentView(view)
        val currentDate: String = intent.getStringExtra("currentDate").toString()
        getTbiUid(currentDate)
        displayResultsBinding.buttonNext.setOnClickListener {
            getTbiUid(currentDate)
        }

        displayResultsBinding.buttonFinish.setOnClickListener {
            val intent = Intent(this, GuardianSideQuestActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun viewScoreTable(currentDate: String) {
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val database = Firebase.database.reference.child("users").child(tbiUserId)
                .child("viewScoresTableSideQuest").child(currentDate)
            var currentDate1: String
            database.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    questionCount =
                        snapshot.childrenCount.toInt() //this will give child number of question parent
                    if (questionNumber <= questionCount) {
                        question = snapshot.child(questionNumber.toString())
                            .child("question").value.toString()
                        yourAnswer = snapshot.child(questionNumber.toString())
                            .child("YourAnswer").value.toString()
                        correct = snapshot.child(questionNumber.toString())
                            .child("correct").value.toString().toInt()
                        correctAnswer = snapshot.child(questionNumber.toString())
                            .child("correctAnswer").value.toString()
                        currentDate1 = snapshot.child(questionNumber.toString())
                            .child("currentDate").value.toString()
                        imageName = snapshot.child(questionNumber.toString())
                            .child("imageName").value.toString()
                        percentage = snapshot.child(questionNumber.toString())
                            .child("percentage").value.toString().toDouble().toInt()
                        question = snapshot.child(questionNumber.toString())
                            .child("question").value.toString()
                        wrong = snapshot.child(questionNumber.toString())
                            .child("wrong").value.toString().toInt()

                        displayResultsBinding.questionTextViewValue.text = question
                        displayResultsBinding.correctTextViewValue.text = correctAnswer
                        displayResultsBinding.dateTextViewValue.text = currentDate1
                        displayResultsBinding.correctNoTextViewValue.text = correct.toString()
                        displayResultsBinding.incorrectNoTextViewValue.text = wrong.toString()
                        displayResultsBinding.percentageTextViewValue.text =
                            "$percentage %"
                        displayResultsBinding.wrongTextViewValue.text = yourAnswer

                        displayResultsBinding.correctTextViewValue.setTextColor(0xFF006400.toInt())
                        if (correctAnswer == yourAnswer) {
                            displayResultsBinding.wrongTextViewValue.setTextColor(0xFF006400.toInt())
                        } else {
                            displayResultsBinding.wrongTextViewValue.setTextColor(Color.RED)
                        }
                        Picasso.get().load(imageName).into(displayResultsBinding.imageDisplay)
                    } else {
                        displayResultsBinding.buttonNext.visibility = View.GONE
                    }
                    questionNumber++
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("onCancelled", "The value: ${error.message}")
                }
            })
        }
    }

    private fun getTbiUid(currentDate: String) {
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val database =
                Firebase.database.reference.child("users").child("GuardiansLinkTable").child(uid)

            database.addValueEventListener(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    val questionCount = snapshot.childrenCount.toInt()
                    if (questionCount >= 1) {
                        tbiUserId = snapshot.child("TBI_ID").value.toString()
                        viewScoreTable(currentDate)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("onCancelled", "The value: ${error.message}")
                }
            })
        }
    }
}