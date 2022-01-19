package com.charuniverse.curious.ui.profile.edit

import com.charuniverse.curious.data.model.User

data class ProfileEditViewState(
    var isLoading: Boolean = false,
    var error: Exception? = null,
    var isFinished: Boolean = false,
    var user: User? = null,
)