package com.charuniverse.curious.data.source

import com.charuniverse.curious.data.dto.PostDetail
import com.charuniverse.curious.data.model.Post
import kotlinx.coroutines.flow.Flow

interface PostDataSource {

    suspend fun getAll(page: Int = 1, limit: Int = 10): Flow<List<PostDetail>>

    suspend fun getById(postId: String): Flow<PostDetail?>

    suspend fun getByUserId(userId: String): Flow<List<PostDetail>>

    suspend fun create(post: Post): Flow<Unit>

    suspend fun update(post: Post): Flow<Unit>

    suspend fun delete(postId: String): Flow<Unit>

    suspend fun toggleLove(postId: String, hasLike: Boolean): Flow<Unit>

}