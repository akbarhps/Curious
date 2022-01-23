package com.charuniverse.curious.ui.post.detail

data class PostDetailViewState(
    var isLoading: Boolean = false,
    var error: Exception? = null,
    var isFinished: Boolean = false,
    var selectedUserId: String? = null,
    var selectedCommentId: String? = null,
)