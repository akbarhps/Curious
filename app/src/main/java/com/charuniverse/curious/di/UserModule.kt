package com.charuniverse.curious.di

import com.charuniverse.curious.data.source.UserRepository
import com.charuniverse.curious.data.source.remote.UserRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Singleton
    @Provides
    fun provideUserRepository(
        @AppModule.RemoteUserDataSource userRemoteDataSource: UserRemoteDataSource,
    ): UserRepository {
        return UserRepository(userRemoteDataSource)
    }
}