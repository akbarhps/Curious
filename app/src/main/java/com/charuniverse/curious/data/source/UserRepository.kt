package com.charuniverse.curious.data.source

import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.model.User
import com.charuniverse.curious.data.source.in_memory.InMemoryUserDataSource
import com.charuniverse.curious.data.source.remote.MessagingRemoteDataSource
import com.charuniverse.curious.data.source.remote.UserRemoteDataSource
import kotlinx.coroutines.flow.*

class UserRepository(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val messagingRemoteDataSource: MessagingRemoteDataSource,
) {

    private val inMemoryUser = InMemoryUserDataSource

    suspend fun login(loginUser: User): Flow<Result<Unit>> = flow {
        emit(Result.Loading)

        try {
            val remoteUser = userRemoteDataSource.getById(loginUser.id)
            if (remoteUser == null) userRemoteDataSource.create(loginUser)

            val token = messagingRemoteDataSource.getToken()
                ?: throw Exception("User token not found")

            userRemoteDataSource.updateFCMToken(loginUser.id, token)

            inMemoryUser.add(loginUser)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun getById(userId: String, forceRefresh: Boolean): Flow<Result<User>> = flow {
        if (!forceRefresh) {
            inMemoryUser.getById(userId)?.let {
                return@flow emit(Result.Success(it))
            }
        }

        emit(Result.Loading)
        try {
            val user = userRemoteDataSource.getById(userId)
                ?: throw IllegalArgumentException("User not found")

            inMemoryUser.add(user)
            emit(Result.Success(user))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun update(user: User): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            userRemoteDataSource.update(user)
            inMemoryUser.update(user)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun delete(userId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            userRemoteDataSource.delete(userId)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}