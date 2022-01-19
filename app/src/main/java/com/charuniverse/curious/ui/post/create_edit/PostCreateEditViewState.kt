package com.charuniverse.curious.ui.post.create_edit

import com.charuniverse.curious.data.model.Post

data class PostCreateEditViewState(
    var isLoading: Boolean = false,
    var isCompleted: Boolean = false,
    var post: Post? = null,
    var error: Exception? = null,
    var titleError: String? = null,
    var contentError: String? = null,
)
