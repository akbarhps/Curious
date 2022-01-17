package com.charuniverse.curious.data.model

import com.charuniverse.curious.data.entity.Comment
import com.charuniverse.curious.data.entity.User
import com.charuniverse.curious.util.Preferences
import java.util.*

data class CommentDetail(
    var postId: String = "",
    var content: String = "",
    var createdBy: String = Preferences.userId,
    var id: String = UUID.randomUUID().toString(),
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long? = null,
    var author: User? = null,
) {
    companion object {
        fun fromComment(comment: Comment, author: User? = null): CommentDetail = CommentDetail(
            postId = comment.postId,
            content = comment.content,
            createdBy = comment.createdBy,
            id = comment.id,
            createdAt = comment.createdAt,
            updatedAt = comment.updatedAt,
            author = author,
        )
    }
}