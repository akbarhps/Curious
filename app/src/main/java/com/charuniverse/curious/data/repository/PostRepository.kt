package com.charuniverse.curious.data.repository

import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.Post
import com.charuniverse.curious.data.remote.PostRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class PostRepository(
    private val postRemoteDataSource: PostRemoteDataSource,
    private val dispatcherContext: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun save(post: Post): Result<Unit> {
        return postRemoteDataSource.save(post)
    }

}