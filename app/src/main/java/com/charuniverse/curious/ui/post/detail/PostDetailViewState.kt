package com.charuniverse.curious.ui.post.detail

import com.charuniverse.curious.data.dto.PostDetail

data class PostDetailViewState(
    var isLoading: Boolean = false,
    var error: Exception? = null,
    var post: PostDetail? = null,
    var isFinished: Boolean = false,
    var selectedUserId: String? = null,
    var selectedCommentId: String? = null,
)