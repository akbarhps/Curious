package com.charuniverse.curious.data.repository

import androidx.lifecycle.LiveData
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.remote.UserRemoteDataSource
import com.charuniverse.curious.data.entity.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class UserRepository(
    private val remoteDataSource: UserRemoteDataSource,
    private val dispatcherContext: CoroutineDispatcher = Dispatchers.IO,
) {

    fun observeUser(): LiveData<Result<User>> {
        return remoteDataSource.observeUser()
    }

    suspend fun refreshUser(userId: String) {
        remoteDataSource.refreshUser(userId)
    }

    suspend fun save(user: User): Result<Unit> {
        return remoteDataSource.save(user)
    }

    suspend fun saveIfNotFound(user: User): Result<Unit> {
        return remoteDataSource.saveIfNotFound(user)
    }

    suspend fun findById(userId: String): Result<User> {
        return remoteDataSource.findById(userId)
    }

    suspend fun updateFCMToken(userId: String, token: String): Result<Unit> {
        return remoteDataSource.updateFCMToken(userId, token)
    }

}