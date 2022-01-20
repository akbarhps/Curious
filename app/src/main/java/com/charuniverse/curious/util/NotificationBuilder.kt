package com.charuniverse.curious.util

import com.charuniverse.curious.data.model.Notification
import com.charuniverse.curious.data.model.NotificationData
import com.charuniverse.curious.data.source.in_memory.InMemoryPostDataSource
import com.charuniverse.curious.data.source.in_memory.InMemoryUserDataSource

object NotificationBuilder {

    private val inMemoryPost = InMemoryPostDataSource
    private val inMemoryUser = InMemoryUserDataSource

    fun build(postId: String, event: String, body: String? = null): Notification? {
        val post = inMemoryPost.getById(postId) ?: return null
        val user = inMemoryUser.getById(post.createdBy) ?: return null
        if (user.FCMToken == null) return null

        val notificationData = NotificationData(
            id = post.id,
            title = post.title,
            createdBy = user.username,
            event = event,
        )

        when (event) {
            NotificationData.EVENT_POST_LIKE -> Unit
            NotificationData.EVENT_POST_COMMENT -> {
                notificationData.body = body ?: ""
            }
            else -> return null
        }

        return Notification(data = notificationData, to = user.FCMToken!!)
    }
}