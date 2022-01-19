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

    suspend fun create(user: User): Flow<Result<Unit>> = flow {
        emit(Result.Loading)

        var findUser: User? = null
        userRemoteDataSource.getById(user.id)
            .catch { throwable -> emit(Result.Error(Exception(throwable.message))) }
            .collect { findUser = it }

        if (findUser != null) {
            inMemoryUser.add(findUser!!)
            return@flow emit(Result.Success(Unit))
        }

        userRemoteDataSource.create(user)
            .catch { throwable -> emit(Result.Error(Exception(throwable.message))) }
            .collect {
                inMemoryUser.add(user)
                emit(Result.Success(Unit))
            }
    }

    suspend fun getById(userId: String, forceRefresh: Boolean): Flow<Result<User>> = flow {
        if (!forceRefresh) {
            inMemoryUser.getById(userId)?.let {
                return@flow emit(Result.Success(it))
            }
        }

        emit(Result.Loading)
        userRemoteDataSource.getById(userId)
            .catch { throwable -> emit(Result.Error(Exception(throwable.message))) }
            .collect {
                if (it == null) {
                    emit(Result.Error(IllegalArgumentException("User not found")))
                } else {
                    inMemoryUser.add(it)
                    emit(Result.Success(it))
                }
            }
    }

    suspend fun update(user: User): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        userRemoteDataSource.update(user)
            .catch { throwable -> emit(Result.Error(Exception(throwable.message))) }
            .collect {
                inMemoryUser.update(user)
                emit(Result.Success(it))
            }
    }

    suspend fun delete(userId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        userRemoteDataSource.delete(userId)
            .catch { throwable -> emit(Result.Error(Exception(throwable.message))) }
            .collect { emit(Result.Success(it)) }
    }

    suspend fun updateFCMToken(userId: String, newToken: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        userRemoteDataSource.updateFCMToken(userId, newToken)
            .catch { throwable -> emit(Result.Error(Exception(throwable.message))) }
            .collect { emit(Result.Success(it)) }
    }

}