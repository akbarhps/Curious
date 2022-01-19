package com.charuniverse.curious.data.source

import com.charuniverse.curious.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    suspend fun create(user: User): Flow<Unit>

    suspend fun getById(userId: String): Flow<User?>

    suspend fun update(user: User): Flow<Unit>

    suspend fun updateFCMToken(userId: String, newToken: String): Flow<Unit>

    suspend fun delete(userId: String): Flow<Unit>

}