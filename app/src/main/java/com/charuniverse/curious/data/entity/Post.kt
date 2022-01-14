package com.charuniverse.curious.data.entity

import java.util.*

data class Post(
    var id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var body: String = "",
    var createdBy: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long? = null,
)
