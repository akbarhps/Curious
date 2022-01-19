package com.charuniverse.curious.data.model

data class User(
    var id: String = "",
    var username: String = "",
    var displayName: String = "",
    var email: String = "",
    var isVerified: Boolean = false,
    var profilePictureUrl: String = "",
    var createdAt: Long = 0L,
    var updatedAt: Long? = null,
)
