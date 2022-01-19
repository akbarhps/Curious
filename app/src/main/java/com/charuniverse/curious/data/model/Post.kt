package com.charuniverse.curious.data.model

import java.util.*

data class Post(
    var id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var content: String = "",
    var createdBy: String = "",
    var createdAt: Long = 0L,
    var updatedAt: Long? = null,
)