package com.charuniverse.curious.data.repository

import androidx.lifecycle.LiveData
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.Post
import com.charuniverse.curious.data.remote.PostRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostRepository(
    private val postRemoteDataSource: PostRemoteDataSource,
    private val dispatcherContext: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun refreshPost() = withContext(dispatcherContext) {
        postRemoteDataSource.refreshPosts()
    }

    fun observePosts(): LiveData<Result<List<Post>>> {
        return postRemoteDataSource.observePosts()
    }

    suspend fun save(post: Post): Result<Unit> {
        return postRemoteDataSource.save(post)
    }

}