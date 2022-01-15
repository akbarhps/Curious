package com.charuniverse.curious.data.model

import com.charuniverse.curious.data.entity.User
import java.util.*

data class PostDetail(
    var id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var content: String = "",
    var createdBy: String = "",
    var author: User = User(),
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long? = null,
)