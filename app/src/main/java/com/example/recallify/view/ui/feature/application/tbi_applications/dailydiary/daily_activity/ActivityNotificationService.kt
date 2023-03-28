package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.example.recallify.R
import com.example.recallify.view.ui.feature.application.tbi_applications.accounts.copiedLocation
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.screens.getCurrentDate
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.moment_snap.MomentSnapActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * The ```ActivityNotificationService``` class is a service class that is used to manage the
 * notifications of the daily activities. The notifications are triggered when the user is in a new
 * location, the system then tells them to make a new activity. If the user is just visiting then
 * the system creates the activity for them. Notifies them that their visit was recorded in their
 * diary. This helps to improve the integrity of the user to their whereabouts as well inform the
 * guardian of their location where abouts. The discretion of how the guardian can see their
 * location is set to minimum because we taught of giving the TBI a bit of privacy as to the things
 * the guardian see.
 *
 * The class is fired on its own and monitor by the notification manager. It does not run on the
 * same block as the main thread but on its own thread.
 * @author enoabasi
 * */
@OptIn(DelicateCoroutinesApi::class)
class ActivityNotificationService : Service() {

    /**
     * Handles and cares for all the operations that go through the notifications
     * of the daily activities. Through this manager processes such as creating, processing,
     * deleting and taking action of a notification can be achieved.
     * @author enoabasi
     * */
    private lateinit var notificationManager: NotificationManager

    /**
     * Handles the creation of building a notification, in this context the daily diary
     * activities notification.
     * @author enoabasi
     * */
    private lateinit var notificationBuilder: NotificationCompat.Builder

    /**
     * The unique identification number of a notification process.
     * The value equals **1**
     * @author enoabasi
     * */
    private val notificationID = 1

    /**
     * The unique identification number of a notification channel.
     * The value equals **daily_activity_id**
     * @author
     * */
    private val channelID = "daily_activity_id"

    /**
     * This is a mutable state that holds the truth value of a new location. If the user is in a
     * new location then the value changes to true and the notification is prompted to the user.
     * If the user is not in a new location then the new activity is created and used for a new
     * location.
     * @author enoabasi
     * */
    private val isNewLocation = mutableStateOf(true)

    /**
     * The current time in its raw state.
     * @author enoabasi
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    private val current = LocalDateTime.now()

    /**
     * The time format that we want from the user's phone.
     * @author enoabasi
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm a")

    /**
     * The formatted time in the current time format.
     * @author enoabasi
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    private val timeFormatted = current.format(timeFormatter)

    /**
     * The current time on the user's device.
     * @author enoabasi
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    private val currentTime = timeFormatted.toString()

    /**
     * The image used in the first activity of that location.
     * @author enoabasi
     * */
    private val imageLink = mutableStateOf("")

    /**
     * The location name used in the first activity of that location.
     * @author enoabasi
     * */
    private val locationName = mutableStateOf("")

    /**
     * The location address used in the first activity of that location.
     * @author enoabasi
     * */
    private val locationAddress = mutableStateOf("")

    /**
     * The current user's user Identification code, this is gotten from the firebase database
     * console.
     * @author enoabasi
     * */
    private val userID = FirebaseAuth.getInstance().currentUser?.uid!!

    /**
     * The loading state of a fetch or post to the firebase console.
     * @author enoabasi
     * */
    private var isLoading = mutableStateOf(false)

    init {
        GlobalScope.launch {
            fetchDataFromFirebaseDatabase()
        }
        cancel()
    }

    /**
     * The fetch block for looping through the location name in the database.
     * The main goal is to get the imageLink, locationName, and locationAddress if the the users
     * has been to the place to automatically created a notification.
     * @author
     * */
    private suspend fun fetchDataFromFirebaseDatabase() =
        withContext(Dispatchers.IO){
            /**
             * Firebase database reference to be used to access the console.
             * @author enoabasi
             * */
            val dB = FirebaseDatabase.getInstance().reference

            /**
             * The fetch block for looping through the location name in the database.
             * The main goal is to get the imageLink, locationName, and locationAddress if the the users
             * has been to the place to automatically created a notification.
             * @author enoabasi
             * */
            val fetchDB = dB
                .child("users")
                .child(userID)
                .child("allActivities")

            // In this block of code, I went with the ideology of pre-fetching my data before it is
            // being used by the system. Obviously this can be risky as I am not commonly familiar
            // with the concept of notification and there is little knowledge about how it works
            // properly but that is why we are programmers. We solve problems.
            // So, what I did is that of the copied location Name is the same as the location Name in
            // the database I save the data in the mutable states, else I do nothing. The initial plan
            fetchDB.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (copiedLocation.value == snapshot.child("locationName").value.toString()) {
                            isNewLocation.value = true
                            imageLink.value = snapshot.child("imageLink").value.toString()
                            locationName.value = snapshot.child("locationName").value.toString()
                            locationAddress.value = snapshot.child("locationAddress").value.toString()
                        } else {
                            isLoading.value = false
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    isLoading.value = false
                }
            })
    }

    /**
     * Cancels all the occurrences of the fetch procedure
     * @author enoabasi
     * */
    private fun cancel() {
        CoroutineScope(Dispatchers.IO).coroutineContext.cancelChildren()
    }
    override fun onCreate() {
        super.onCreate()

        // Assigning the get value of the notification service to notification manager variable
        // as an alias of the NotificationManager.
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Checking if the build version of our sdk is higher than the version code for "O" os
        // This will allow the notification to create when needed by the system for daily activity.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /**
             * The name of the notification channel. The value is **daily_activity_channel**.
             * @author enoabasi
             */
            val channelName = "daily_activity_channel"

            /**
             * The importance level of our notification, the value is set to be high as this is an
             * important criteria for the app to function properly. So unless it is completed the
             * notification remains as it is. The value equals **IMPORTANCE_DEFAULT**
             * @author enoabasi
             * */
            val importanceLevel = NotificationManager.IMPORTANCE_DEFAULT

            /**
             * The notification channel of Daily activity.
             * @author enoabasi
             * */
            val channel = NotificationChannel(channelID, channelName, importanceLevel)

            // Create the notification channel of the daily diary activities
            // collects all the values from the parent channel.
            notificationManager.createNotificationChannel(channel)
        }

        // Building the basic notification for an activity.
        // The notification is a small text notification with no actions attached to it.
        notificationBuilder = NotificationCompat
            .Builder(this, channelID)
            .setSmallIcon(R.drawable.moment_snap)
            .setContentTitle("New location discovered! 🥳")
            .setContentText("Seems like you are in a new location. Create a moment of this!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        /**
         * The application page to be taken to once the notification is acknowledge.
         * @author enoabasi
         * */
        val pendingIntent = PendingIntent
            .getActivity(
                this,
                0,
                Intent(this, MomentSnapActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )

        /**
         * The notification of an activity with the action on tap.
         * @author enoabasi
         * */
        val activityNotification = notificationBuilder
            .setContentIntent(pendingIntent)
            .build()

        // Fire the service and monitor its state changes.
        // Also checks in the background for the versioning of the device and also if the
        // necessary permissions have been given to that device to receive push notifications.
        startForeground(notificationID, activityNotification)

        /**
         * The initial delay for a notification. This is so to allow the firebase call fetch the
         * location information from the firebase database. The value has been set to **2 minutes**.
         * @author enoabasi
         * */
        val delay = 1 * 60 * 1000L

        /**
         * A delay looper for the notification display handler
         * @author enoabasi
         * */
        val handler = Handler(Looper.getMainLooper())

        // Handles the delay of a notification to the user's device.
        // The notification is delayed by the specified timing.
        handler.postDelayed({
            if (!isNewLocation.value) {
                automaticCreateActivity()
            } else {
                launchActivityNotification()
            }
        }, delay)

        // The killed service of the activity notification.
        return START_NOT_STICKY
    }

    /**
     * Fetches the data of the previous record from the database and creates an activity
     * based on the information retrieved.
     * @author enoabasi
     */
    private fun automaticCreateActivity() {
        /**
         * Reference to the database in the firebase console
         * @author enoabasi
         * */
        val database = FirebaseDatabase.getInstance().reference

        /**
         * The current user in using the application at that instance..
         * @author enoabasi
         * */
        val userID = FirebaseAuth.getInstance().currentUser?.uid!!

        /**
         * The reference path of the activities
         * @author enoabasi
         * */
        val activityRef = database
            .child("users")
            .child(userID)
            .child("dailyDiaryDummy")
            .child(getCurrentDate())

        val key = activityRef.push().key!!

        activityRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                activityRef.child(key)
                    .child("userId")
                    .setValue(userID)
                activityRef.child(key)
                    .child("activityId")
                    .setValue(key)
                activityRef.child(key)
                    .child("imageLink")
                    .setValue(imageLink.value)
                activityRef.child(key)
                    .child("locationName")
                    .child(locationName.value)
                activityRef.child(key)
                    .child("locationAddress")
                    .setValue(locationAddress.value)
                activityRef.child(key)
                    .child("date")
                    .child(getCurrentDate())
                activityRef.child(key)
                    .child("time")
                    .setValue(currentTime)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("notificationOnCancelled()", "ErrMessage${error.message}")
            }

        })
    }

    /**
     * The daily diary activity launcher.
     * @author enoabasi
     * */
    private fun launchActivityNotification() {
        /**
         * The notification of an activity with the action on tap.
         * @author enoabasi
         * */
        val notification = notificationBuilder
            .setWhen(System.currentTimeMillis())
            .build()

        notificationManager.notify(notificationID, notification)

        /**
         * The initial delay for a notification. This is so to allow the firebase call fetch the
         * location information from the firebase database. The value has been set to **2 minutes**.
         * @author enoabasi
         * */
        val delay = 2 * 60 * 1000L

        /**
         * A delay looper for the notification display handler
         * @author enoabasi
         * */
        val handler = Handler(Looper.getMainLooper())

        // Handles the delay of a notification to the user's device.
        // The notification is delayed by the specified timing.
        handler.postDelayed({
            launchActivityNotification()
        }, delay)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}