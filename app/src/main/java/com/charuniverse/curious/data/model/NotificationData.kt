package com.charuniverse.curious.data.model

import com.google.gson.annotations.SerializedName

data class NotificationData(
    @SerializedName("id")
    var id: String = "",

    @SerializedName("title")
    var title: String = "",

    @SerializedName("body")
    var body: String = "",

    @SerializedName("created_by")
    var createdBy: String = "",

    @SerializedName("event")
    var event: String = EMPTY,

    @SerializedName("event_value")
    var eventValue: String = "",
) {
    companion object {
        const val EMPTY = ""
        const val EVENT_POST_LIKE = "POST_LIKE"
        const val EVENT_POST_COMMENT = "POST_COMMENT"

        private fun getEventValue(event: String): String = when (event) {
            EVENT_POST_LIKE -> "love your post"
            EVENT_POST_COMMENT -> "comment on your post"
            else -> ""
        }

        fun fromMap(data: Map<String, String>) = NotificationData(
            id = data["id"] ?: EMPTY,
            title = data["title"] ?: EMPTY,
            body = data["body"] ?: EMPTY,
            createdBy = data["created_by"] ?: EMPTY,
            event = data["event"] ?: EMPTY,
            eventValue = getEventValue(data["event"] ?: "")
        )
    }
}

