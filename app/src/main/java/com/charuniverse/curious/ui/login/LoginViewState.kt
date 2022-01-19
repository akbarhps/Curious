package com.charuniverse.curious.ui.login

data class LoginViewState(
    var isLoading: Boolean = false,
    var error: Exception? = null,
    var isLoggedIn: Boolean = false,
)
