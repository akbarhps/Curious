package com.charuniverse.curious.di

import com.charuniverse.curious.data.source.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        dispatcherContext: CoroutineDispatcher
    ): AuthRepository {
        return AuthRepository(firebaseAuth, dispatcherContext)
    }
}