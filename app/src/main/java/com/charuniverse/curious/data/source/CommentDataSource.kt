package com.charuniverse.curious.data.source

import com.charuniverse.curious.data.model.Comment
import kotlinx.coroutines.flow.Flow

interface CommentDataSource {

    suspend fun create(comment: Comment): Flow<Unit>

    suspend fun update(comment: Comment): Flow<Unit>

    suspend fun delete(postId: String, commentId: String): Flow<Unit>

}