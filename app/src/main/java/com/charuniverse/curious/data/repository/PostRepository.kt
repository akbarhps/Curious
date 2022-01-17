package com.charuniverse.curious.data.repository

import androidx.lifecycle.LiveData
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.Comment
import com.charuniverse.curious.data.entity.Post
import com.charuniverse.curious.data.model.PostDetail
import com.charuniverse.curious.data.source.remote.PostRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostRepository(
    private val remoteDataSource: PostRemoteDataSource,
    private val dispatcherContext: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun refreshPosts() = withContext(dispatcherContext) {
        remoteDataSource.refreshPosts()
    }

    fun observePosts(): LiveData<Result<List<PostDetail>>> {
        return remoteDataSource.observePosts()
    }

    fun observeUserPosts(userId: String): LiveData<Result<List<PostDetail>>> {
        return remoteDataSource.observeUserPosts(userId)
    }

    fun observePost(postId: String): LiveData<Result<PostDetail>> {
        return remoteDataSource.observePost(postId)
    }

    suspend fun save(post: Post): Result<Unit> {
        return remoteDataSource.save(post)
    }

    suspend fun update(post: Post): Result<Unit> {
        return remoteDataSource.update(post)
    }

    suspend fun delete(postId: String): Result<Unit> {
        return remoteDataSource.delete(postId)
    }

    suspend fun toggleLove(postId: String): Result<Unit> {
        return remoteDataSource.toggleLove(postId)
    }

    suspend fun addComment(comment: Comment): Result<Unit> {
        return remoteDataSource.addComment(comment)
    }

    suspend fun deleteComment(postId: String, commentId: String): Result<Unit> {
        return remoteDataSource.deleteComment(postId, commentId)
    }
}