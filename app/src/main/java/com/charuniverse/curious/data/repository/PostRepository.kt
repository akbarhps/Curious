package com.charuniverse.curious.data.repository

import androidx.lifecycle.LiveData
import com.charuniverse.curious.data.Result
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

    suspend fun refreshUserPosts(userId: String) = withContext(dispatcherContext) {
        remoteDataSource.refreshUserPosts(userId)
    }

    fun observeUserPosts(userId: String): LiveData<Result<List<Post>>> {
        return remoteDataSource.observeUserPosts()
    }

    fun observePost(postId: String): LiveData<Result<PostDetail>> {
        return remoteDataSource.observePost(postId)
    }

    suspend fun save(post: Post): Result<Unit> {
        return remoteDataSource.save(post)
    }

    suspend fun delete(postId: String): Result<Unit> {
        return remoteDataSource.delete(postId)
    }

}