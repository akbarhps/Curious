package com.charuniverse.curious.data.source

import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.dto.NotificationDetail
import com.charuniverse.curious.data.source.in_memory.InMemoryUserDataSource
import com.charuniverse.curious.data.source.remote.NotificationRemoteDataSource
import com.charuniverse.curious.util.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotificationRepository(
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
) {

    private val inMemoryUser = InMemoryUserDataSource

    suspend fun getByUserId(): Flow<Result<List<NotificationDetail>>> = flow {
        try {
            //TODO: refactor so it not depends on in memory user
            val notifications = notificationRemoteDataSource
                .getByUserId(Preferences.userId)
                .map {
                    val detail = NotificationDetail.fromDomainNotification(it)
                    detail.author = inMemoryUser.getById(detail.createdBy)
                    return@map detail
                }
            emit(Result.Success(notifications))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}