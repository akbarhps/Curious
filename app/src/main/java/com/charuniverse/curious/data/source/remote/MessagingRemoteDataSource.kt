package com.charuniverse.curious.data.source.remote

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class MessagingRemoteDataSource(private val firebaseMessaging: FirebaseMessaging) {

    suspend fun getToken(): String? {
        return firebaseMessaging.token.await()
    }

}