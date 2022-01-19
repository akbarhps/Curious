package com.charuniverse.curious.data.model

import java.util.*

data class Comment(
    var id: String = UUID.randomUUID().toString(),
    var postId: String = "",
    var content: String = "",
    var createdBy: String = "",
    var createdAt: Long = 0L,
    var updatedAt: Long? = null,
)
