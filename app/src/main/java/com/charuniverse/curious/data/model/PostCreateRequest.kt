package com.charuniverse.curious.data.model

data class PostCreateRequest(
    var title: String = "",
    var body: String = "",
    var createdBy: String = "",
)
