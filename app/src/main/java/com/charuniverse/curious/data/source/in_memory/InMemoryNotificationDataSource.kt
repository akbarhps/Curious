package com.charuniverse.curious.data.source.in_memory

import com.charuniverse.curious.data.dto.NotificationDetail
import com.charuniverse.curious.data.model.Notification

object InMemoryNotificationDataSource {

    private val notifications = mutableMapOf<String, NotificationDetail>()

    private val inMemoryUser = InMemoryUserDataSource

    fun getAll(): List<NotificationDetail> {
        return notifications.values.toList()
    }

    fun isEmpty(): Boolean = notifications.isEmpty()

    fun create(data: Notification) {
        val notification = NotificationDetail.fromDomainNotification(data)
        notification.author = inMemoryUser.getById(data.createdBy)
        notifications[notification.id] = notification
    }

    fun clear() {
        notifications.clear()
    }
}