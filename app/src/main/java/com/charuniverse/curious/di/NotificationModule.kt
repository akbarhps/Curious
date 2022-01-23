package com.charuniverse.curious.di

import com.charuniverse.curious.data.source.NotificationRepository
import com.charuniverse.curious.data.source.remote.NotificationRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Singleton
    @Provides
    fun provideNotificationRepository(
        @AppModule.RemoteNotificationDataSource notificationRemoteDataSource: NotificationRemoteDataSource,
    ): NotificationRepository {
        return NotificationRepository(notificationRemoteDataSource)
    }

}
