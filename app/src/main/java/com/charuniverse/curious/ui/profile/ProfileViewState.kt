package com.charuniverse.curious.ui.profile

import com.charuniverse.curious.data.entity.User

data class ProfileViewState(
    var user: User? = null,
    var isEditing: Boolean = false,
    var isLoading: Boolean = false,
    var isSignOut: Boolean = false,
)
