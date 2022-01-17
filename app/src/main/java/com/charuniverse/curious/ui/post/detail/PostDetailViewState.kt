package com.charuniverse.curious.ui.post.detail

data class PostDetailViewState(
    var isLoading: Boolean = false,
    var postError: Exception? = null,
    var commentError: Exception? = null,
    var uploadCommentSuccess: Boolean = false,
    var deletePostSuccess: Boolean = false,
)