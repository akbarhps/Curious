package com.charuniverse.curious.ui.profile

import com.charuniverse.curious.data.dto.PostDetail
import com.charuniverse.curious.data.model.User

data class ProfileViewState(
    var isLoading: Boolean = false,
    var isLoggedOut: Boolean = false,
    var error: Exception? = null,
    var user: User? = null,
    var userPosts: List<PostDetail>? = null,
    var selectedPostId: String? = null,
)
