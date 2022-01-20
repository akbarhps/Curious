package com.charuniverse.curious.data.source.remote

import com.charuniverse.curious.data.model.User
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class UserRemoteDataSource(
    firebaseDatabase: FirebaseDatabase,
) {

    private val userRef = firebaseDatabase.reference.child("users")

    suspend fun create(user: User) {
        userRef.child(user.id)
            .setValue(user)
            .await()
    }

    suspend fun getById(userId: String): User? {
        return userRef.child(userId)
            .get()
            .await()
            .getValue(User::class.java)
    }

    suspend fun update(user: User) {
        val updates: MutableMap<String, Any> = HashMap()

        updates["${user.id}/username"] = user.username
        updates["${user.id}/displayName"] = user.displayName
        updates["${user.id}/updatedAt"] = user.updatedAt ?: System.currentTimeMillis()

        userRef.updateChildren(updates).await()
    }

    suspend fun delete(userId: String) {
        userRef.child(userId)
            .setValue(null)
            .await()
    }

    suspend fun updateFCMToken(userId: String, newToken: String) {
        val updates: MutableMap<String, Any> = HashMap()

        updates["${userId}/FCMToken"] = newToken
        updates["${userId}/updatedAt"] = System.currentTimeMillis()

        userRef.updateChildren(updates).await()
    }
}