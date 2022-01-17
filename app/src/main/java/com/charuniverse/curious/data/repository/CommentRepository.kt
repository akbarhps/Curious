package com.charuniverse.curious.data.repository

import androidx.lifecycle.LiveData
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.Comment
import com.charuniverse.curious.data.model.CommentDetail
import com.charuniverse.curious.data.source.remote.CommentRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class CommentRepository(
    private val remoteDataSource: CommentRemoteDataSource,
    private val context: CoroutineContext = Dispatchers.IO,
) {

    fun observeComments(): LiveData<Result<List<CommentDetail>>> {
        return remoteDataSource.observeComments()
    }

    suspend fun refreshComments(postId: String) {
        return remoteDataSource.refreshComments(postId)
    }

    suspend fun save(comment: Comment): Result<Unit> {
        return remoteDataSource.save(comment)
    }

    suspend fun delete(postId: String, commentId: String): Result<Unit> {
        return remoteDataSource.delete(postId, commentId)
    }

}