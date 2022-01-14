package com.charuniverse.curious.ui.login

data class LoginViewState(
    var isSuccess: Boolean = false,
    var isLoading: Boolean = false,
    var exception: Exception? = null,
)
