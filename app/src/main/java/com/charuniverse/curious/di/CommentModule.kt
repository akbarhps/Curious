package com.charuniverse.curious.di

import com.charuniverse.curious.data.source.CommentRepository
import com.charuniverse.curious.data.source.remote.CommentRemoteDataSource
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
        @AppModule.RemoteCommentDataSource commentRemoteDataSource: CommentRemoteDataSource
    ): CommentRepository {
        return CommentRepository(commentRemoteDataSource)
    }

}