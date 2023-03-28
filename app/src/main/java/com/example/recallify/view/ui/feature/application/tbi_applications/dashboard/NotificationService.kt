package com.example.recallify.view.ui.feature.application.tbi_applications.dashboard

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.recallify.R

class NotificationService : Service() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val notificationId = 1
    private val channelId = "channel_id"

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Channel Name"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Please Record Conversation")
            .setContentText("This is to remind you to record conversation whenever you have it")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, DashboardActivity::class.java)
        val pendingIntent = PendingIntent
            .getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = notificationBuilder
            .setContentIntent(pendingIntent)
            .build()

        startForeground(notificationId, notification)

        // Schedule the first notification after 10 minutes
        val delay = 1 * 60 * 1000L // 10 minutes in milliseconds
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            sendNotification()
        }, delay)

        // Return START_STICKY to ensure the service is restarted if it is killed by the system
        return START_STICKY
    }

    private fun sendNotification() {
        val notification = notificationBuilder
            .setWhen(System.currentTimeMillis())
            .build()

        notificationManager.notify(notificationId, notification)

        // Schedule the next notification after 10 minutes
        val delay = 2 * 60 * 1000L // 10 minutes in milliseconds
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            sendNotification()
        }, delay)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        @Suppress("DEPRECATION")
        stopForeground(true)
    }
}
