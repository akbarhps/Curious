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
    annotation class RemoteNotificationDataSource

    @Singleton
    @AppModule.RemoteNotificationDataSource
    @Provides
    fun provideNotificationRemoteDataSource(
        firebaseDatabase: FirebaseDatabase,
        firebaseMessaging: FirebaseMessaging,
        pushNotificationAPI: PushNotificationAPI,
    ): NotificationRemoteDataSource {
        return NotificationRemoteDataSource(firebaseDatabase, firebaseMessaging, pushNotificationAPI)
    }

    @Provides
    @Singleton
    fun provideNotificationAPI(): PushNotificationAPI {
        return Retrofit.Builder()
            .baseUrl(Constant.FCM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PushNotificationAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideDispatcherContext() = Dispatchers.IO
}