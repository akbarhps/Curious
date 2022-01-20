package com.charuniverse.curious.data.source

import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.model.Notification
import com.charuniverse.curious.data.source.remote.NotificationAPI
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class NotificationRepository(
    private val firebaseMessaging: FirebaseMessaging,
    private val notificationAPI: NotificationAPI,
) {

    suspend fun getToken(): Flow<Result<String>> = flow {
        emit(Result.Loading)

        try {
            val token = firebaseMessaging.token.await()
            emit(Result.Success(token))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun sendNotification(notification: Notification): Flow<Result<Unit>> = flow {
        emit(Result.Loading)

        try {
            notificationAPI.send(notification)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}