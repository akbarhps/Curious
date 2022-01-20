package com.charuniverse.curious.di

import com.charuniverse.curious.data.source.NotificationRepository
import com.charuniverse.curious.data.source.remote.NotificationAPI
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Singleton
    @Provides
    fun provideNotificationAPI(
        firebaseMessaging: FirebaseMessaging,
        notificationAPI: NotificationAPI,
    ): NotificationRepository {
        return NotificationRepository(firebaseMessaging, notificationAPI)
    }
}
