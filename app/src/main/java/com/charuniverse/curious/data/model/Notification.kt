package com.charuniverse.curious.data.model

import com.charuniverse.curious.util.NotificationEvent
import com.charuniverse.curious.util.Preferences
import java.util.*

data class Notification(
    var id: String = UUID.randomUUID().toString(),
    var eventId: String = "",
    var eventType: String = NotificationEvent.POST_LOVE,
    var eventTitle: String = "",
    var eventBody: String? = null,
    var recipient: String = "",
    var createdBy: String = Preferences.userId,
    var createdAt: Long = System.currentTimeMillis()
)