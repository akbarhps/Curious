package com.charuniverse.curious.ui.post.detail

data class PostDetailViewState(
    var isLoading: Boolean = false,
    var error: String? = null,
    var isCompleted: Boolean = false,
)