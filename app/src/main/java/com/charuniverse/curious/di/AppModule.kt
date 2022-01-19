package com.charuniverse.curious.di

import com.charuniverse.curious.data.source.remote.CommentRemoteDataSource
import com.charuniverse.curious.data.source.remote.PostRemoteDataSource
import com.charuniverse.curious.data.source.remote.UserRemoteDataSource
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemoteUserDataSource

    @Singleton
    @RemoteUserDataSource
    @Provides
    fun provideUserRemoteDataSource(
        firebaseDatabase: FirebaseDatabase,
        dispatcherContext: CoroutineDispatcher
    ): UserRemoteDataSource {
        return UserRemoteDataSource(firebaseDatabase, dispatcherContext)
    }

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemotePostDataSource

    @Singleton
    @AppModule.RemotePostDataSource
    @Provides
    fun providePostRemoteDataSource(
        firebaseDatabase: FirebaseDatabase,
        dispatcherContext: CoroutineDispatcher
    ): PostRemoteDataSource {
        return PostRemoteDataSource(firebaseDatabase, dispatcherContext)
    }

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemoteCommentDataSource

    @Singleton
    @AppModule.RemoteCommentDataSource
    @Provides
    fun provideCommentRemoteDataSource(
        firebaseDatabase: FirebaseDatabase,
        dispatcherContext: CoroutineDispatcher
    ): CommentRemoteDataSource {
        return CommentRemoteDataSource(firebaseDatabase, dispatcherContext)
    }

    @Singleton
    @Provides
    fun provideDispatcherContext() = Dispatchers.IO
}