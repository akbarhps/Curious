package com.charuniverse.curious.di

import com.charuniverse.curious.data.source.AuthRepository
import com.charuniverse.curious.data.source.remote.MessagingRemoteDataSource
import com.charuniverse.curious.data.source.remote.UserRemoteDataSource
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        @AppModule.RemoteMessagingDataSource messagingRemoteDataSource: MessagingRemoteDataSource,
        @AppModule.RemoteUserDataSource userRemoteDataSource: UserRemoteDataSource,
    ): AuthRepository {
        return AuthRepository(firebaseAuth, messagingRemoteDataSource, userRemoteDataSource)
    }
}