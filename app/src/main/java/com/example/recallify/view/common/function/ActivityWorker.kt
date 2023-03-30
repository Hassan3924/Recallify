package com.example.recallify.view.common.function

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.recallify.R
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity.ActivityNotification
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity.DailyActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.screens.getCurrentDate
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.moment_snap.MomentSnapActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ActivityWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        notificationAutoCreateLogin(applicationContext)
        return Result.success()
    }

    private fun notificationAutoCreateLogin(context: Context) {
        val locationNotification = ActivityNotification(
            context = context,
            title = "New location discovered.",
            message = "Let's capture the Moment!🥳",
            desiredDestination = MomentSnapActivity::class.java,
            intentMessage = "Create a Moment",
            intentIcon = R.drawable.moment_snap
        )
        locationNotification.launchNotification()

        val visitedLocationNotification = ActivityNotification(
            context = context,
            title = "Just visiting I see.",
            message = "Let's add it to our Activities!🥳",
            desiredDestination = DailyActivity::class.java,
            intentMessage = "Create an Activity",
            intentIcon = R.drawable.daily_activity,
        )
        visitedLocationNotification.launchNotification()
    }
}