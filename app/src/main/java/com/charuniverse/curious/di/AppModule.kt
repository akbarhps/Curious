package com.charuniverse.curious.di

import com.charuniverse.curious.data.repository.AuthRepository
import com.charuniverse.curious.data.repository.PostDetailRepository
import com.charuniverse.curious.data.repository.PostRepository
import com.charuniverse.curious.data.repository.UserRepository
import com.charuniverse.curious.data.source.remote.PostDetailRemoteDataSource
import com.charuniverse.curious.data.source.remote.PostRemoteDataSource
import com.charuniverse.curious.data.source.remote.UserRemoteDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
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

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemotePostDataSource

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemotePostDetailDataSource

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

    @Singleton
    @Provides
    fun provideDispatcherContext() = Dispatchers.IO

    @Singleton
    @Provides
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        dispatcherContext: CoroutineDispatcher
    ): AuthRepository {
        return AuthRepository(firebaseAuth, dispatcherContext)
    }

    @Singleton
    @RemoteUserDataSource
    @Provides
    fun provideUserRemoteDataSource(
        firebaseDatabase: FirebaseDatabase,
        dispatcherContext: CoroutineDispatcher
    ): UserRemoteDataSource {
        return UserRemoteDataSource(firebaseDatabase, dispatcherContext)
    }

    @Singleton
    @RemotePostDataSource
    @Provides
    fun providePostRemoteDataSource(
        firebaseDatabase: FirebaseDatabase,
        dispatcherContext: CoroutineDispatcher
    ): PostRemoteDataSource {
        return PostRemoteDataSource(firebaseDatabase, dispatcherContext)
    }

    @Singleton
    @RemotePostDetailDataSource
    @Provides
    fun providePostDetailDataSource(
        firebaseDatabase: FirebaseDatabase,
        dispatcherContext: CoroutineDispatcher
    ): PostDetailRemoteDataSource {
        return PostDetailRemoteDataSource(firebaseDatabase, dispatcherContext)
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        @RemoteUserDataSource userRemoteDataSource: UserRemoteDataSource,
        dispatcherContext: CoroutineDispatcher
    ): UserRepository {
        return UserRepository(userRemoteDataSource, dispatcherContext)
    }

    @Singleton
    @Provides
    fun providePostRepository(
        @RemotePostDataSource postRemoteDataSource: PostRemoteDataSource,
        dispatcherContext: CoroutineDispatcher
    ): PostRepository {
        return PostRepository(postRemoteDataSource, dispatcherContext)
    }

    @Singleton
    @Provides
    fun providePostDetailRepository(
        @RemotePostDetailDataSource remoteDataSource: PostDetailRemoteDataSource,
        dispatcherContext: CoroutineDispatcher
    ): PostDetailRepository {
        return PostDetailRepository(remoteDataSource, dispatcherContext)
    }
}