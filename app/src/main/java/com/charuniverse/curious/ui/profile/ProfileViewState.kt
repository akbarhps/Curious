package com.charuniverse.curious.ui.profile

data class ProfileViewState(
    var isLoading: Boolean = false,
    var isLoggedOut: Boolean = false,
    var error: Exception? = null,
    var selectedPostId: String? = null,
)
