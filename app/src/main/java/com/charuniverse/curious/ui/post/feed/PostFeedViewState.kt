package com.charuniverse.curious.ui.post.feed

import com.charuniverse.curious.data.dto.PostDetail

data class PostFeedViewState(
    var isLoading: Boolean = false,
    var error: Exception? = null,
    var posts: List<PostDetail>? = null,
    var selectedPostId: String? = null,
)
