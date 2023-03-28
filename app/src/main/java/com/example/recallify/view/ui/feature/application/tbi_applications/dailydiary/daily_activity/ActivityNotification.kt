package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.app.NotificationCompat
import com.example.recallify.R
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.moment_snap.MomentSnapActivity
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class ActivityNotification(
    var context: Context,
    var title: String,
    var message: String
) {
    private val channelId: String = "FCM100"
    private val channelName: String = "FCMMessage"
    private val notificationManager: NotificationManager = context
        .applicationContext
        .getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var notificationBuilder: NotificationCompat.Builder

    fun launchNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val intent = Intent(context, MomentSnapActivity::class.java)

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

        notificationBuilder = NotificationCompat.Builder(context, channelId)
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_recallify_foreground)
        notificationBuilder.addAction(R.drawable.moment_snap,"Create a Moment", pendingIntent)
        notificationBuilder.setContentTitle(title)
        notificationBuilder.setContentText(message)
        notificationBuilder.setAutoCancel(true)
        notificationManager.notify(100, notificationBuilder.build())

    }

}