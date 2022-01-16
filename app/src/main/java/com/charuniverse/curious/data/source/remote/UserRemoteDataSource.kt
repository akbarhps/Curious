package com.charuniverse.curious.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.Result.*
import com.charuniverse.curious.data.entity.User
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRemoteDataSource(
    firebaseDatabase: FirebaseDatabase,
    private val dispatcherContext: CoroutineDispatcher = Dispatchers.IO,
) {

    private val userRef = firebaseDatabase.reference.child("users")

    private var cachedUser = mutableMapOf<String, User>()

    fun refreshCachedUser() {
        cachedUser = mutableMapOf()
    }

    private val observableUser = MutableLiveData<Result<User>>()
    fun observeUser(): LiveData<Result<User>> = observableUser

    suspend fun refreshObservableUser(userId: String) = withContext(dispatcherContext) {
        observableUser.value = findById(userId)!!
    }

    suspend fun save(user: User): Result<Unit> = withContext(dispatcherContext) {
        return@withContext try {
            userRef.child(user.id).setValue(user).await()
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun saveIfNotExist(user: User): Result<Unit> = withContext(dispatcherContext) {
        return@withContext try {
            val doc = userRef.child(user.id).get().await()

            var remoteUser = doc.getValue(User::class.java)
            if (remoteUser == null) {
                userRef.child(user.id).setValue(user).await()
                remoteUser = user
            }

            cachedUser[user.id] = remoteUser
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun findById(userId: String): Result<User> = withContext(dispatcherContext) {
        cachedUser[userId]?.let { return@withContext Success(it) }

        return@withContext try {
            val doc = userRef.child(userId).get().await()

            val user = doc.getValue(User::class.java)
                ?: throw IllegalArgumentException("User not found")

            cachedUser[user.id] = user
            Success(user)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun updateFCMToken(
        userId: String, token: String
    ): Result<Unit> = withContext(dispatcherContext) {
        return@withContext try {
            userRef.child(userId).child("messsagingToken")
                .setValue(token).await()

            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }
}