package com.charuniverse.curious.di

import com.charuniverse.curious.data.repository.CommentRepository
import com.charuniverse.curious.data.source.remote.CommentRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommentModule {

    @Singleton
    @Provides
    fun provideCommentRepository(
        @AppModule.RemoteCommentDataSource remoteDataSource: CommentRemoteDataSource,
        context: CoroutineDispatcher
    ): CommentRepository {
        return CommentRepository(remoteDataSource, context)
    }
}
