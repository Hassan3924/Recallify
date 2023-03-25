package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary

import android.util.Log
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
        val tempList = mutableListOf<Information>()

        response.value = DataState.Loading

        val database = FirebaseDatabase.getInstance().reference
        val userID = FirebaseAuth.getInstance().currentUser?.uid!!

        val activityRef = database
            .child("users")
            .child(userID)
            .child("dailyDairyDummy")
            .child(getCurrentDate())

        activityRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (DataSnap in snapshot.children) {
                    val activity = DataSnap.getValue(Information::class.java)
                    Log.d("item_path_activity", "The item data is: { $activity }")

                    if (activity != null) {
                        tempList.add(activity)
                    }

                    response.value = DataState.Success(tempList)
                    Log.d("item_path_response_value", "The response value: { ${response.value} }")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                response.value = DataState.Failed(error.message)
            }
        })
    }
}