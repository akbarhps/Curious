package com.charuniverse.curious.di

import com.charuniverse.curious.data.repository.PostRepository
import com.charuniverse.curious.data.source.remote.PostRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PostModule {

    @Singleton
    @Provides
    fun providePostRepository(
        @AppModule.RemotePostDataSource postRemoteDataSource: PostRemoteDataSource,
        dispatcherContext: CoroutineDispatcher
    ): PostRepository {
        return PostRepository(postRemoteDataSource, dispatcherContext)
    }
}