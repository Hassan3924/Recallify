package com.example.recallify.view.common.function

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.recallify.view.ui.feature.application.tbi_applications.accounts.copiedLatitude
import com.example.recallify.view.ui.feature.application.tbi_applications.accounts.copiedLongitude
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity.ActivityNotification
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.screens.getCurrentDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val firebaseImageLink = mutableStateOf("")
val firebaseLocationName = mutableStateOf("")
val firebaseLocationAddress = mutableStateOf("")
val firebaseLongitude = mutableStateOf("")
val firebaseLatitude = mutableStateOf("")

class FirebaseHandlers(
    private val context: Context,
    private val workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    private val database =
        FirebaseDatabase.getInstance().reference.child("users")

    private val userId = FirebaseAuth.getInstance().currentUser?.uid!!

    @RequiresApi(Build.VERSION_CODES.O)
    private val current = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm a")

    @RequiresApi(Build.VERSION_CODES.O)
    private val timeFormatted = current.format(timeFormatter)

    @RequiresApi(Build.VERSION_CODES.O)
    private val currentTime = timeFormatted.toString()

    suspend fun notificationAutoCreateLogin(context: Context) {
        if (
            firebaseLongitude.value.toBigDecimal() == copiedLongitude.value.toBigDecimal() &&
            firebaseLatitude.value.toBigDecimal() == copiedLatitude.value.toBigDecimal()
        ) {
            val locationNotification = ActivityNotification(
                context = context,
                "New location discovered.",
                "Let's capture the Moment!🥳",
            )
            locationNotification.launchNotification()
        } else {
            autoCreateActivity()
        }
    }

    suspend fun fetchDataFromFirebase() {
        val userLocation = database
            .child(userId)
            .child("allActivities")

        userLocation.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        firebaseImageLink.value = snapshot.child("imageLink").value.toString()
                        firebaseLocationName.value = snapshot.child("locationName").value.toString()
                        firebaseLocationAddress.value =
                            snapshot.child("locationAddress").value.toString()
                        firebaseLongitude.value =
                            snapshot.child("locationLongitude").value.toString()
                        firebaseLatitude.value = snapshot.child("locationLatitude").value.toString()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private suspend fun autoCreateActivity() = withContext(Dispatchers.IO) {
        val autoActivity = database
            .child(userId)
            .child("dailDairyDummy")
            .child(getCurrentDate())

        val key = autoActivity.push().key!!

        autoActivity.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    autoActivity.child(key)
                        .child("imageLink")
                        .setValue(firebaseImageLink.value)
                    autoActivity.child(key)
                        .child("locationName")
                        .setValue(firebaseLocationName.value)
                    autoActivity.child(key)
                        .child("locationAddress")
                        .setValue(firebaseLocationAddress.value)
                    autoActivity.child(key)
                        .child("locationLongitude")
                        .setValue(firebaseLongitude.value)
                    autoActivity.child(key)
                        .child("locationLatitude")
                        .setValue(firebaseLatitude.value)
                    autoActivity
                        .child(key).child("date")
                        .setValue(getCurrentDate())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        autoActivity.child(key)
                            .child("time")
                            .setValue(currentTime)
                    }
                    autoActivity.child(key)
                        .child("activityId")
                        .setValue(key)
                    autoActivity.child(key)
                        .child("userId")
                        .setValue(userId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override suspend fun doWork(): Result {
        return Result.success()
    }
}