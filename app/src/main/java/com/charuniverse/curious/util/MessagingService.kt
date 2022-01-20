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
import com.charuniverse.curious.data.model.NotificationData
import com.charuniverse.curious.ui.MainActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val data = NotificationData.fromMap(message.data)

        val pendingIntent = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.main_nav_graph)
            .setDestination(R.id.postDetailFragment)
            .setArguments(bundleOf("postId" to data.id))
            .createPendingIntent()

        val notificationBuilder = NotificationCompat
            .Builder(this, "Curious")
            .setContentTitle("${data.createdBy} ${data.eventValue} ${data.title}")
            .setContentText(data.body)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat
                    .BigTextStyle()
                    .bigText(data.body)
            )
            .setContentIntent(pendingIntent)

        if (data.event == NotificationData.EVENT_POST_LIKE) {
            notificationBuilder.setSmallIcon(R.drawable.ic_baseline_favorite_24)
        } else {
            notificationBuilder.setSmallIcon(R.drawable.ic_baseline_comment_24)
        }

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
        FirebaseDatabase.getInstance()
            .reference.child("users/${Preferences.userId}/FCMToken")
            .setValue(newToken)
            .addOnFailureListener {
                Log.e("MessagingService", "onNewToken: ${it.message}", it)
            }
            .addOnSuccessListener {
                Log.e("MessagingService", "onNewToken: Successfully updated")
            }
    }
}