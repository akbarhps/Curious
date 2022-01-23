package com.charuniverse.curious.data.model

import com.google.gson.annotations.SerializedName

data class PushNotificationData(
    @SerializedName("event_id")
    var eventId: String = "",

    @SerializedName("event_type")
    var eventType: String = NotificationEvent.POST_LOVE,

    @SerializedName("event_title")
    var eventTitle: String = "",

    @SerializedName("sender_username")
    var senderUsername: String = "",
) {
    companion object {
        fun fromMessagingData(data: Map<String, String>) = PushNotificationData(
            eventId = data["event_id"] ?: "",
            eventType = data["event_type"] ?: "",
            eventTitle = data["event_title"] ?: "",
            senderUsername = data["sender_username"] ?: "",
        )
    }
}