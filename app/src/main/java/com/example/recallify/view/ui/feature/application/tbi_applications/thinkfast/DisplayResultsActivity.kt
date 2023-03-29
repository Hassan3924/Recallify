package com.example.recallify.view.ui.feature.application.tbi_applications.thinkfast

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import com.example.recallify.databinding.ActivityDisplayResultsBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class DisplayResultsActivity : AppCompatActivity() {
    private lateinit var displayResultsBinding: ActivityDisplayResultsBinding
    private var questionCount = 0
    private var questionNumber = 1
    private var yourAnswer = ""
    private var correct = 0
    private var wrong = 0
    private var correctAnswer = ""
    private var imageName = ""
    private var percentage = 0
    private var question = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        displayResultsBinding = ActivityDisplayResultsBinding.inflate(layoutInflater)
        val view = displayResultsBinding.root
        setContentView(view)

        val currentDate: String = intent.getStringExtra("currentDate").toString()
        viewScoreTable(currentDate)

        displayResultsBinding.buttonNext.setOnClickListener {
            viewScoreTable(currentDate)
        }

        displayResultsBinding.buttonFinish.setOnClickListener {
            val intent = Intent(this@DisplayResultsActivity, ThinkFastActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun viewScoreTable(currentDate: String) {
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val database = Firebase.database.reference.child("viewScoresTable").child(uid).child(currentDate)
            var currentDate1: String
            database.addValueEventListener(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
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

                        displayResultsBinding.questionTextViewValue.text = question
                        displayResultsBinding.correctTextViewValue.text = correctAnswer
                        displayResultsBinding.dateTextViewValue.text = currentDate1
                        displayResultsBinding.correctNoTextViewValue.text = correct.toString()
                        displayResultsBinding.incorrectNoTextViewValue.text = wrong.toString()
                        displayResultsBinding.percentageTextViewValue.text = percentage.toString()
                        displayResultsBinding.wrongTextViewValue.text = yourAnswer

                        displayResultsBinding.correctTextViewValue.setTextColor(0xFF006400.toInt())
                        //displayResultsBinding.correctTextViewValue.setTextColor(0xFF023020.toInt())
                        if(correctAnswer==yourAnswer){
                            displayResultsBinding.wrongTextViewValue.setTextColor(0xFF006400.toInt())


                        }
                        else{
                            displayResultsBinding.wrongTextViewValue.setTextColor(Color.RED)
                        }
                        Picasso.get().load(imageName).into(displayResultsBinding.imageDisplay)
                    }
                    else{
                        //Toast.makeText(this@DisplayResultsActivity,"Thats the end! ",Toast.LENGTH_SHORT).show()
                       // displayResultsBinding.buttonNext.setVisibility(View.INVISIBLE)
                       // displayResultsBinding.buttonNext.setBackgroundColor(0xFF808080.toInt())
                        displayResultsBinding.buttonNext.setVisibility(View.GONE)
                    }
                    questionNumber++

                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}