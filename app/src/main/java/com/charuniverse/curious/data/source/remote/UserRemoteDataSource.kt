package com.charuniverse.curious.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.Result.*
import com.charuniverse.curious.data.entity.Post
import com.charuniverse.curious.data.entity.User
import com.charuniverse.curious.exception.NotFound
import com.charuniverse.curious.util.Cache
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRemoteDataSource(
    firebaseDatabase: FirebaseDatabase,
    private val context: CoroutineDispatcher = Dispatchers.IO,
) {

    private val userRef = firebaseDatabase.reference.child("users")

    private val observableUser = MutableLiveData<Result<User>>()
    fun observeUser(): LiveData<Result<User>> = observableUser

    suspend fun refreshObservableUser(userId: String) = withContext(Dispatchers.Main) {
        observableUser.value = findById(userId)!!
    }

    suspend fun save(user: User): Result<Unit> = withContext(context) {
        return@withContext try {
            userRef.child(user.id).setValue(user).await()

            Cache.users[user.id] = user
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun saveIfNotExist(user: User): Result<Unit> = withContext(context) {
        return@withContext try {
            val doc = userRef.child(user.id).get().await()

            var remoteUser = doc.getValue(User::class.java)
            if (remoteUser == null) {
                userRef.child(user.id).setValue(user).await()
                remoteUser = user
            }

            Cache.users[remoteUser.id] = remoteUser
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun update(user: User): Result<Unit> = withContext(context) {
        return@withContext try {
            val updates: MutableMap<String, Any> = HashMap()

            userRef.child(user.id).get().await().getValue(Post::class.java)
                ?: return@withContext Error(NotFound("User not found"))

            updates["${user.id}/username"] = user.username
            updates["${user.id}/displayName"] = user.displayName
            updates["${user.id}/updatedAt"] = System.currentTimeMillis()

            userRef.updateChildren(updates)
            Cache.users[user.id] = user

            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun findById(userId: String): Result<User> = withContext(context) {
        return@withContext try {
            val doc = userRef.child(userId).get().await()

            val user = doc.getValue(User::class.java)
                ?: throw IllegalArgumentException("User not found")

            Cache.users[user.id] = user
            Success(user)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun updateFCMToken(
        userId: String, token: String
    ): Result<Unit> = withContext(context) {
        return@withContext try {
            userRef.child(userId).child("messsagingToken")
                .setValue(token).await()

            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }
}