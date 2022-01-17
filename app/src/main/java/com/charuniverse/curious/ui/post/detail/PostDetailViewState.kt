package com.charuniverse.curious.ui.post.detail

data class PostDetailViewState(
    var isLoading: Boolean = false,
    var postError: String? = null,
    var commentError: String? = null,
    var uploadCommentSuccess: Boolean = false,
    var deletePostSuccess: Boolean = false,
)