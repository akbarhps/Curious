package com.charuniverse.curious.di

import com.charuniverse.curious.data.source.PostRepository
import com.charuniverse.curious.data.source.remote.NotificationAPI
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
        notificationAPI: NotificationAPI,
    ): PostRepository {
        return PostRepository(postRemoteDataSource, userRemoteDataSource, notificationAPI)
    }

}