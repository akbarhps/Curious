package com.charuniverse.curious.data.model

data class Notification(
    val data: NotificationData = NotificationData(),
    val to: String = "",
    val webpush: Map<String, Map<String, String>> = mapOf(
        "notification" to mapOf(
            "clickAction" to ".ui.main.MainActivity"
        )
    )
)
