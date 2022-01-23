package com.charuniverse.curious.di

import com.charuniverse.curious.data.source.PostRepository
import com.charuniverse.curious.data.source.remote.NotificationRemoteDataSource
import com.charuniverse.curious.data.source.remote.PostRemoteDataSource
import com.charuniverse.curious.data.source.remote.UserRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PostModule {

    @Singleton
    @Provides
    fun providePostRepository(
        @AppModule.RemotePostDataSource postRemoteDataSource: PostRemoteDataSource,
        @AppModule.RemoteUserDataSource userRemoteDataSource: UserRemoteDataSource,
        @AppModule.RemoteNotificationDataSource notificationRemoteDataSource: NotificationRemoteDataSource
    ): PostRepository = PostRepository(
        postRemoteDataSource,
        userRemoteDataSource,
        notificationRemoteDataSource
    )

}