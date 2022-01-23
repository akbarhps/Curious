package com.charuniverse.curious.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.charuniverse.curious.R
import com.charuniverse.curious.data.dto.PushNotificationData
import com.charuniverse.curious.ui.MainActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val data = PushNotificationData.fromMessagingData(message.data)

        val eventDestination = NotificationEvent.getEventDestination(data.eventType)
        val pendingIntent = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.main_nav_graph)
            .setDestination(eventDestination)
            .setArguments(bundleOf("id" to data.eventId))
            .createPendingIntent()

        val eventText = NotificationEvent.getEventValue(data.eventType)
        val notificationBuilder = NotificationCompat
            .Builder(this, "Curious")
            .setContentTitle("@${data.senderUsername} $eventText")
            .setContentText(data.eventTitle)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat
                    .BigTextStyle()
                    .bigText(data.eventTitle)
            )
            .setContentIntent(pendingIntent)

        val eventIcon = NotificationEvent.getEventIcon(data.eventType)
        notificationBuilder.setSmallIcon(eventIcon)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Curious",
                "Curious",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(
            Random.nextInt() * 999999 /* ID of notification */,
            notificationBuilder.build()
        )
    }

    override fun onNewToken(newToken: String) {
        FirebaseDatabase.getInstance().reference
            .child("users/${Preferences.userId}/FCMToken")
            .setValue(newToken)
            .addOnFailureListener {
                Log.e("MessagingService", "onNewToken: ${it.message}", it)
            }
            .addOnSuccessListener {
                Log.e("MessagingService", "onNewToken: Successfully updated")
            }
    }
}