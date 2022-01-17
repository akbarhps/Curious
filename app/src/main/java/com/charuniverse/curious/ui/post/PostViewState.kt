package com.charuniverse.curious.ui.post

data class PostViewState(
    var isLoading: Boolean = false,
    var isCompleted: Boolean = false,
    var error: Exception? = null,
)