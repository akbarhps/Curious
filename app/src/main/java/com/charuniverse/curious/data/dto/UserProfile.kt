package com.charuniverse.curious.data.dto

import com.charuniverse.curious.data.model.Post

data class UserProfile(
    var id: String = "",
    var username: String = "",
    var displayName: String = "",
    var email: String = "",
    var isVerified: Boolean = false,
    var profilePictureUrl: String = "",
    var createdAt: Long = 0L,
    var updatedAt: Long? = null,
    var posts: List<Post> = listOf(),
)
