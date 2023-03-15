package com.example.recallify.view.ui.feature.application.thinkfast

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.example.recallify.R
import com.example.recallify.view.common.components.LoadingAnimation
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ThinkFastLoadingActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private var setChanger = 1
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_think_fast_loading)
        val thinkfastLoadingCompose: ComposeView = findViewById(R.id.activity_think_fast_loading_screen)
        thinkfastLoadingCompose.setContent {
            RecallifyTheme {
                LoadingAnimation(
                    loadingText = "Just fetching game stage"
                )
            }
        }


        userExists()
        checkUserStatus()
        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
            val intent = Intent(this@ThinkFastLoadingActivity, DisplayImageActivity::class.java)
            intent.putExtra("setChangerFromConfirm", setChanger)

            startActivity(intent)
            finish()
        }, 9000)
    }

    private fun checkUserStatus() {
        val database = FirebaseDatabase.getInstance()
        val reference = database.reference.child("scoresTable2")
        val scoreReference = database.reference.child("scoresTable2")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    scoreReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            user?.let {
                                val userUID = it.uid

                                val percentDBString = snapshot.child(userUID).child("ScorePercentage").value.toString()
                                val setChangerDBString = snapshot.child(userUID).child("setChanger").value.toString()
                                val setChangerDBInt = setChangerDBString.toInt()
                                val percentInt = percentDBString.toInt()

                                setChanger = if (percentInt >= 80) {
                                    setChangerDBInt + 1
                                } else {
                                    setChangerDBInt
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.w(TAG, "onCancelledDatabase (RealtimeDatabase): -> ${error.message}")
                        }
                    })
                } else {
                    setChanger = 1
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ThinkFastLoadingActivity, "Error, issues", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun userExists() {
        val uid = Firebase.auth.currentUser?.uid
        val scoreRef = database.reference
        if (uid != null) {
            val database = Firebase.database.reference.child("scoresTable2").child(uid)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        scoreRef.child("scoresTable2").child(uid).child("correct").setValue(1)
                        scoreRef.child("scoresTable2").child(uid).child("setChanger").setValue(1)
                        scoreRef.child("scoresTable2").child(uid).child("ScorePercentage").setValue(1)
                        scoreRef.child("scoresTable2").child(uid).child("wrong").setValue(1).addOnSuccessListener {}
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "onCancelledDatabase (RealtimeDatabase): -> ${error.message}")
                }
            })
        }
    }

}