package com.charuniverse.curious.data.source

import android.util.Log
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.dto.NotificationDetail
import com.charuniverse.curious.data.source.in_memory.InMemoryNotificationDataSource
import com.charuniverse.curious.data.source.remote.NotificationRemoteDataSource
import com.charuniverse.curious.util.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotificationRepository(
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
) {

    private val inMemoryNotification = InMemoryNotificationDataSource

    suspend fun getByUserId(
        forceRefresh: Boolean = false
    ): Flow<Result<List<NotificationDetail>>> = flow {
        try {
            if (forceRefresh || inMemoryNotification.isEmpty()) {
                notificationRemoteDataSource
                    .getByUserId(Preferences.userId)
                    .map { inMemoryNotification.create(it) }
            }

            val notifications = inMemoryNotification.getAll()
            emit(Result.Success(notifications))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}