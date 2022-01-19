package com.charuniverse.curious.ui.comment

data class CommentCreateEditViewState(
    var isLoading: Boolean = false,
    var error: Exception? = null,
    var isFinished: Boolean = false,
    var postTitle: String? = null,
    val content: String? = null,
    var contentError: String? = null,
)
