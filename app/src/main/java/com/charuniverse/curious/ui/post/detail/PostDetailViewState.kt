package com.charuniverse.curious.ui.post.detail

data class PostDetailViewState(
    var isLoading: Boolean = false,
    var fetchPostError: String? = null,
    var fetchCommentsError: String? = null,
    var uploadCommentError: String? = null,
    var isUploadCommentComplete: Boolean = false,
    var isDeletePostComplete: Boolean = false,
)