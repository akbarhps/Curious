package com.charuniverse.curious.data.entity

data class User(
    var id: String = "",
    var username: String = "",
    var displayName: String = "",
    var email: String = "",
    var profilePictureURL: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long? = null,
)
