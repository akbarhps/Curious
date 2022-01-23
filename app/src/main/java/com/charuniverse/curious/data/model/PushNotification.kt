package com.charuniverse.curious.data.model

import com.google.gson.annotations.SerializedName

data class PushNotification(
    @SerializedName("data")
    val data: PushNotificationData = PushNotificationData(),

    @SerializedName("to")
    val to: String = "",

    @SerializedName("webpush")
    private val webPush: Map<String, Map<String, String>> = mapOf(
        "notification" to mapOf("clickAction" to ".ui.main.MainActivity")
    )
)
