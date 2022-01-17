package com.charuniverse.curious.data.repository

import androidx.lifecycle.LiveData
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.User
import com.charuniverse.curious.data.source.remote.UserRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class UserRepository(
    private val remoteDataSource: UserRemoteDataSource,
    private val context: CoroutineDispatcher = Dispatchers.IO,
) {

    fun observeUser(): LiveData<Result<User>> {
        return remoteDataSource.observeUser()
    }

    suspend fun refreshObservableUser(userId: String) {
        remoteDataSource.refreshObservableUser(userId)
    }

    suspend fun save(user: User): Result<Unit> {
        return remoteDataSource.save(user)
    }

    suspend fun saveIfNotExist(user: User): Result<Unit> {
        return remoteDataSource.saveIfNotExist(user)
    }

    suspend fun update(user: User): Result<Unit> {
        return remoteDataSource.update(user)
    }

    suspend fun findById(userId: String): Result<User> {
        return remoteDataSource.findById(userId)
    }

    suspend fun updateFCMToken(userId: String, token: String): Result<Unit> {
        return remoteDataSource.updateFCMToken(userId, token)
    }

}