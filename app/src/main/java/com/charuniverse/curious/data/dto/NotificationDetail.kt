package com.charuniverse.curious.data.dto

import com.charuniverse.curious.data.model.Notification
import com.charuniverse.curious.data.model.User
import com.charuniverse.curious.util.NotificationEvent
import com.charuniverse.curious.util.Preferences
import java.util.*

data class NotificationDetail(
    var id: String = UUID.randomUUID().toString(),
    var eventId: String = "",
    var eventType: String = NotificationEvent.POST_LOVE,
    var eventTitle: String = "",
    var eventBody: String? = null,
    var recipient: String = "",
    var createdBy: String = Preferences.userId,
    var createdAt: Long = System.currentTimeMillis(),
    var author: User? = null,
) {
    companion object {
        fun fromDomainNotification(data: Notification) = NotificationDetail(
            id = data.id,
            eventId = data.eventId,
            eventType = data.eventType,
            eventTitle = data.eventTitle,
            eventBody = data.eventBody,
            recipient = data.recipient,
            createdBy = data.createdBy,
            createdAt = data.createdAt,
            author = null,
        )
    }
}
