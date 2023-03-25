package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.screens.getCurrentDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ActivityViewModel : ViewModel() {

    val response: MutableState<DataState> = mutableStateOf(DataState.Empty)

    init {
        fetchActivity()
    }

    private fun fetchActivity() {
        val tempList = mutableListOf<ActivityInformation>()
        response.value = DataState.Loading

        val database = FirebaseDatabase.getInstance().reference
        val userID = FirebaseAuth.getInstance().currentUser?.uid!!

        val activityRef = database
            .child("users")
            .child(userID)
            .child("dailyDairyDummy")
            .child(getCurrentDate())

        activityRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnap in snapshot.children) {
                    val key = dataSnap.key
                    val item = dataSnap.child(key!!).getValue(ActivityInformation::class.java)
                    tempList.add(item!!)
                }

                response.value = DataState.Success(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                response.value = DataState.Failed(error.message)
            }


        })
    }
}