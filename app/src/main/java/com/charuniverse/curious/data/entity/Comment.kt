package com.charuniverse.curious.data.entity

import java.util.*

data class Comment(
    var postId: String = "",
    var content: String = "",
    var createdBy: String = "",
    var id: String = UUID.randomUUID().toString(),
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long? = null,
)
