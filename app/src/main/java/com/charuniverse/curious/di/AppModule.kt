package com.charuniverse.curious.di

import com.charuniverse.curious.data.source.remote.*
import com.charuniverse.curious.util.Constant
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    ): UserRemoteDataSource {
        return UserRemoteDataSource(firebaseDatabase)
    }

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemotePostDataSource

    @Singleton
    @AppModule.RemotePostDataSource
    @Provides
    fun providePostRemoteDataSource(
        firebaseDatabase: FirebaseDatabase,
    ): PostRemoteDataSource {
        return PostRemoteDataSource(firebaseDatabase)
    }

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemoteCommentDataSource

    @Singleton
    @AppModule.RemoteCommentDataSource
    @Provides
    fun provideCommentRemoteDataSource(
        firebaseDatabase: FirebaseDatabase,
    ): CommentRemoteDataSource {
        return CommentRemoteDataSource(firebaseDatabase)
    }

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemoteMessagingDataSource

    @Singleton
    @AppModule.RemoteMessagingDataSource
    @Provides
    fun provideMessagingRemoteDataSource(
        firebaseMessaging: FirebaseMessaging
    ): MessagingRemoteDataSource {
        return MessagingRemoteDataSource(firebaseMessaging)
    }

    @Provides
    @Singleton
    fun provideNotificationAPI(): NotificationAPI {
        return Retrofit.Builder()
            .baseUrl(Constant.FCM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotificationAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideDispatcherContext() = Dispatchers.IO
}