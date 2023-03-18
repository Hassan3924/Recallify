package com.example.recallify.view.ui.feature.application.tbi_applications.sidequest

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.recallify.databinding.ActivitySideQuestDisplayResultBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class SideQuestDisplayResultActivity : AppCompatActivity() {

    private lateinit var sideQuestDisplayResultBinding: ActivitySideQuestDisplayResultBinding
    private var questionCount = 0
    private var questionNumber = 1
    private var yourAnswer = ""
    private var correct = 0
    private var wrong = 0
    private var correctAnswer = ""
    private var imageName = ""
    private var percentage = 0
    private var question = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sideQuestDisplayResultBinding = ActivitySideQuestDisplayResultBinding.inflate(layoutInflater)
        val view = sideQuestDisplayResultBinding.root
        setContentView(view)

        val currentDate: String = intent.getStringExtra("currentDate").toString()
        viewScoreTable(currentDate)

        sideQuestDisplayResultBinding.buttonNext.setOnClickListener {
            viewScoreTable(currentDate)
        }

        sideQuestDisplayResultBinding.buttonFinish.setOnClickListener {
            val intent = Intent(this@SideQuestDisplayResultActivity, SideQuestActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun viewScoreTable(currentDate: String) {
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val database = Firebase.database.reference.child("users").child(uid).child("viewScoresTableSideQuest").child(currentDate)
            var currentDate1: String
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    questionCount = snapshot.childrenCount.toInt()
                    if (questionNumber <= questionCount) {
                        question = snapshot.child(questionNumber.toString()).child("question").value.toString()
                        yourAnswer = snapshot.child(questionNumber.toString()).child("YourAnswer").value.toString()
                        correct = snapshot.child(questionNumber.toString()).child("correct").value.toString().toInt()
                        correctAnswer = snapshot.child(questionNumber.toString()).child("correctAnswer").value.toString()
                        currentDate1 = snapshot.child(questionNumber.toString()).child("currentDate").value.toString()
                        imageName = snapshot.child(questionNumber.toString()).child("imageName").value.toString()
                        percentage = snapshot.child(questionNumber.toString()).child("percentage").value.toString().toDouble().toInt()
                        question = snapshot.child(questionNumber.toString()).child("question").value.toString()
                        wrong = snapshot.child(questionNumber.toString()).child("wrong").value.toString().toInt()

                        sideQuestDisplayResultBinding.questionTextViewValue.text = question
                        sideQuestDisplayResultBinding.correctTextViewValue.text = correctAnswer
                        sideQuestDisplayResultBinding.dateTextViewValue.text = currentDate1
                        sideQuestDisplayResultBinding.correctNoTextViewValue.text = correct.toString()
                        sideQuestDisplayResultBinding.incorrectNoTextViewValue.text = wrong.toString()
                        sideQuestDisplayResultBinding.percentageTextViewValue.text = percentage.toString()
                        sideQuestDisplayResultBinding.wrongTextViewValue.text = yourAnswer
                        Picasso.get().load(imageName).into(sideQuestDisplayResultBinding.imageDisplay)


                    } else {
                        Log.w(ContentValues.TAG, "SideQuest_QuestionCount (ErrorOnValidation) :-> please check the count of the question from the database")
                    }
                    questionNumber++
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(ContentValues.TAG, "SideQuest_QuestionCount (ErrorOnValidation) :-> ${error.message}")
                }
            })
        }
    }
}