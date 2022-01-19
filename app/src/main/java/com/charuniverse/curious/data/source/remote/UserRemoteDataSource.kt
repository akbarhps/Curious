package com.charuniverse.curious.data.source.remote

import com.charuniverse.curious.data.model.User
import com.charuniverse.curious.data.source.UserDataSource
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

class UserRemoteDataSource(
    firebaseDatabase: FirebaseDatabase,
    private val context: CoroutineContext = Dispatchers.IO,
) : UserDataSource {

    private val userRef = firebaseDatabase.reference.child("users")

    override suspend fun create(user: User): Flow<Unit> = flow {
        userRef.child(user.id)
            .setValue(user)
            .await()
        emit(Unit)
    }

    override suspend fun getById(userId: String): Flow<User?> = flow {
        val user = userRef.child(userId)
            .get()
            .await()
            .getValue(User::class.java)
        emit(user)
    }

    override suspend fun update(user: User): Flow<Unit> = flow {
        val updates: MutableMap<String, Any> = HashMap()

        updates["${user.id}/username"] = user.username
        updates["${user.id}/displayName"] = user.displayName
        updates["${user.id}/updatedAt"] = user.updatedAt ?: System.currentTimeMillis()

        userRef.updateChildren(updates).await()
        emit(Unit)
    }

    override suspend fun delete(userId: String): Flow<Unit> = flow {
        userRef.child(userId)
            .setValue(null)
            .await()
        emit(Unit)
    }

    override suspend fun updateFCMToken(userId: String, newToken: String): Flow<Unit> = flow {
        val updates: MutableMap<String, Any> = HashMap()

        updates["${userId}/FCMToken"] = newToken
        updates["${userId}/updatedAt"] = System.currentTimeMillis()

        userRef.updateChildren(updates).await()
        emit(Unit)
    }
}