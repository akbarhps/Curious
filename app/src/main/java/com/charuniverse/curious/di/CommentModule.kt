package com.charuniverse.curious.di

import com.charuniverse.curious.data.source.CommentRepository
import com.charuniverse.curious.data.source.remote.CommentRemoteDataSource
import com.charuniverse.curious.data.source.remote.NotificationRemoteDataSource
import com.charuniverse.curious.data.source.remote.PushNotificationAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommentModule {

    @Singleton
    @Provides
    fun provideCommentRepository(
        @AppModule.RemoteCommentDataSource commentRemoteDataSource: CommentRemoteDataSource,
        @AppModule.RemoteNotificationDataSource notificationRemoteDataSource: NotificationRemoteDataSource,
    ): CommentRepository {
        return CommentRepository(commentRemoteDataSource, notificationRemoteDataSource)
    }

}