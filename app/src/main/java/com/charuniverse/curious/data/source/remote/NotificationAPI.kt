package com.charuniverse.curious.data.source.remote

import com.charuniverse.curious.BuildConfig
import com.charuniverse.curious.data.model.Notification
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @POST("/fcm/send")
    @Headers(
        "Content-Type: application/json",
        "Authorization: ${BuildConfig.FIREBASE_SERVER_KEY}"
    )
    suspend fun send(@Body notification: Notification)

}