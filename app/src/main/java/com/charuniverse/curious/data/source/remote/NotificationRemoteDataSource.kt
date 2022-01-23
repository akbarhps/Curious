package com.charuniverse.curious.data.source.remote

import com.charuniverse.curious.data.model.Notification
import com.charuniverse.curious.util.NotificationBuilder
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class NotificationRemoteDataSource(
    firebaseDatabase: FirebaseDatabase,
    private val firebaseMessaging: FirebaseMessaging,
    private val notificationAPI: PushNotificationAPI,
) {

    private val userRef = firebaseDatabase.reference.child("users")

    suspend fun getFCMToken(): String {
        return firebaseMessaging.token.await()
    }

    suspend fun create(data: Notification) {
        val notificationRef = userRef
            .child("${data.recipient}/notifications/${data.id}")

        val push = NotificationBuilder
            .pushNotification(eventId = data.eventId, eventType = data.eventType)
            ?: return

        notificationRef
            .setValue(data)
            .await()

        notificationAPI.send(push)
    }

}