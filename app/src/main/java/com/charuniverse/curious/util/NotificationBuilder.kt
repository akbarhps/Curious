package com.charuniverse.curious.util

import com.charuniverse.curious.data.model.Notification
import com.charuniverse.curious.data.model.PushNotification
import com.charuniverse.curious.data.model.PushNotificationData
import com.charuniverse.curious.data.source.in_memory.InMemoryPostDataSource
import com.charuniverse.curious.data.source.in_memory.InMemoryUserDataSource

object NotificationBuilder {

    private val inMemoryPost = InMemoryPostDataSource
    private val inMemoryUser = InMemoryUserDataSource

    fun postNotification(eventId: String, eventType: String, body: String? = null): Notification? {
        val post = inMemoryPost.getById(eventId) ?: return null
        val receiver = inMemoryUser.getById(post.createdBy) ?: return null

        return Notification(
            eventId = eventId,
            eventType = eventType,
            eventTitle = post.title,
            eventBody = body,
            recipient = receiver.id,
        )
    }

    fun pushNotification(eventId: String, eventType: String): PushNotification? {
        val post = inMemoryPost.getById(eventId) ?: return null
        val receiver = inMemoryUser.getById(post.createdBy) ?: return null
        val sender = inMemoryUser.getById(Preferences.userId) ?: return null

        if (sender.id == receiver.id || receiver.FCMToken == null) {
            return null
        }

        val data = PushNotificationData(
            senderUsername = sender.username,
            eventTitle = post.title,
            eventId = eventId,
            eventType = eventType,
        )

        return PushNotification(
            data = data,
            to = receiver.FCMToken!!,
        )
    }
}