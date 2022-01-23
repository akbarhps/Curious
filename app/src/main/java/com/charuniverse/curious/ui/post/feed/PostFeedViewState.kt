package com.charuniverse.curious.ui.post.feed

data class PostFeedViewState(
    var isLoading: Boolean = false,
    var error: Exception? = null,
    var selectedPostId: String? = null,
)
