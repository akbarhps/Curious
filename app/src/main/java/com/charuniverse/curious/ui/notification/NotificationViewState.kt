package com.charuniverse.curious.ui.notification

data class NotificationViewState(
    var isLoading: Boolean = false,
    var error: Exception? = null,
    var selectedNotificationId: String? = null,
)