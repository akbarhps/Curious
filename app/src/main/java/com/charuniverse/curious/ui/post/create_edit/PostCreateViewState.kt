package com.charuniverse.curious.ui.post.create_edit

data class PostCreateViewState(
    var isLoading: Boolean = false,
    var isCompleted: Boolean = false,
    var errorMessage: String? = null,
)
