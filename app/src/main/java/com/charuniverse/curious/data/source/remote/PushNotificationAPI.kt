package com.charuniverse.curious.data.source.remote

import com.charuniverse.curious.BuildConfig
import com.charuniverse.curious.data.dto.PushNotification
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PushNotificationAPI {

    @POST("/fcm/send")
    @Headers(
        "Content-Type: application/json",
        "Authorization: ${BuildConfig.FIREBASE_SERVER_KEY}"
    )
    suspend fun send(@Body pushNotification: PushNotification)

}