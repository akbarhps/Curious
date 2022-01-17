package com.charuniverse.curious.ui.post

data class BaseViewState(
    var isLoading: Boolean = false,
    var isCompleted: Boolean = false,
    var error: Exception? = null,
)