package com.charuniverse.curious.data.model

import com.charuniverse.curious.data.entity.User
import java.util.*

data class PostFeedResponse(
    var id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var body: String = "",
    var createdBy: String = "",
    var author: User? = null,
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long? = null,
)