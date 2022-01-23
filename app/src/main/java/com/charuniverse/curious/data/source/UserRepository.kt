package com.charuniverse.curious.data.source

import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.model.User
import com.charuniverse.curious.data.source.in_memory.InMemoryUserDataSource
import com.charuniverse.curious.data.source.remote.UserRemoteDataSource
import kotlinx.coroutines.flow.*

class UserRepository(
    private val userRemoteDataSource: UserRemoteDataSource,
) {

    private val inMemoryUser = InMemoryUserDataSource

    suspend fun getById(userId: String, forceRefresh: Boolean): Flow<Result<User>> = flow {
        if (!forceRefresh) {
            inMemoryUser.getById(userId)?.let {
                return@flow emit(Result.Success(it))
            }
        }

        try {
            emit(Result.Loading)
            val user = userRemoteDataSource.getById(userId)
                ?: throw IllegalArgumentException("User not found")

            inMemoryUser.add(user)
            emit(Result.Success(user))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun update(user: User): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)
            userRemoteDataSource.update(user)
            inMemoryUser.update(user)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun delete(userId: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)
            userRemoteDataSource.delete(userId)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}