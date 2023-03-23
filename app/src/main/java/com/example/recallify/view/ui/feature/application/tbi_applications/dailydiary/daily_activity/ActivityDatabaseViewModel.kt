package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recallify.data.application.ActivityFromDatabase
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.screens.getCurrentDate
import com.example.recallify.view.ui.resource.modules.DatabaseActivityState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ActivityDatabaseViewModel : ViewModel() {

    private var activityId = 1
    private var activityCount = 0

    var response: MutableState<DatabaseActivityState> = mutableStateOf(DatabaseActivityState.Empty)

    private val database = FirebaseDatabase
        .getInstance()
        .reference
        .child("users")

    private val userIdentification = FirebaseAuth.getInstance().currentUser?.uid!!

    init {
        fetchDataFromDatabase()
    }

    private fun fetchDataFromDatabase() = viewModelScope.launch {
        try {
            val tempList = mutableListOf<ActivityFromDatabase>()

            response.value = DatabaseActivityState.Loading

            val fetchRef = FirebaseDatabase.getInstance().reference

            val activityDatabase = database
                .child(userIdentification)
                .child("dailyDairyDummy")
                .child(getCurrentDate())

            activityDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            activityCount = snapshot.childrenCount.toInt()
                            if (activityId <= activityCount) {

                    for (datum in snapshot.children) {
                        for (item in datum.children) {
                            val datumItem = item.getValue(ActivityFromDatabase::class.java)
                            if (datumItem != null) {
                                tempList.add(datumItem)
                            }
                        }
                    }
                    response.value = DatabaseActivityState.Success(tempList)
                            } else {
                                response.value =
                                    DatabaseActivityState.Success(emptyList<ActivityFromDatabase>().toMutableList())
                            }
                        }

                }

                override fun onCancelled(error: DatabaseError) {
                    response.value =
                        DatabaseActivityState.Failure("Error_on_Retrieving_activity_information: ${error.message}")
                }

            })
        } catch (e: Exception) {
            Log.e("Error: ", "Failed to send message at: ${e.message}")
        }
    }
}