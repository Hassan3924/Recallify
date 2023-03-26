package com.example.recallify.view.ui.feature.guradian_application.guardianthinkfast

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.recallify.databinding.ActivityGuardianDisplayResultsThinkFastBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class GuardianDisplayResultsThinkFast : AppCompatActivity() {

    lateinit var displayResultsBinding:ActivityGuardianDisplayResultsThinkFastBinding
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var questionCount = 0 //number of questions in database, initial valu set to 0
    var questionNumber = 1 //if random number then its 0
    var yourAnswer =""
    var correct = 0
    var wrong = 0
    var correctAnswer =""
    var imageName = ""
    var percentage = 0
    var question = ""
var tbi_uid=""
            override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

                displayResultsBinding =ActivityGuardianDisplayResultsThinkFastBinding.inflate(layoutInflater)
                val view = displayResultsBinding.root
                setContentView(view)
                var currentDate:String = intent.getStringExtra("currentDate").toString()
//                viewScoreTable(currentDate)
                getTbiUid(currentDate)
                displayResultsBinding.buttonNext.setOnClickListener {
                   // viewScoreTable(currentDate)
                    getTbiUid(currentDate)
                }

                displayResultsBinding.buttonFinish.setOnClickListener{
                    val intent = Intent(this,GuardianThinkFastActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
    fun viewScoreTable(currentDate:String) {
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val database = Firebase.database.reference.child("viewScoresTable").child(tbi_uid).child(currentDate)
            var currentDate1=""
            database.addValueEventListener(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    questionCount = snapshot.childrenCount.toInt() //this will give child number of question parent
                    if (questionNumber <= questionCount){
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
                        Picasso.get().load(imageName).into(displayResultsBinding.imageDisplay)


                    }
                    else{

                    }
                    questionNumber++

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    fun getTbiUid(currentDate:String){
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            val database = Firebase.database.reference.child("users").child("GuardiansLinkTable").child(uid)

            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var questionCount = snapshot.childrenCount.toInt()
                    if(questionCount==1) {
                        tbi_uid = snapshot.child("TBI_ID").value.toString()

                        viewScoreTable(currentDate)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        }

    }
}