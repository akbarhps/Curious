package com.charuniverse.curious.data.source

import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.model.Comment
import com.charuniverse.curious.data.source.in_memory.InMemoryPostDataSource
import com.charuniverse.curious.data.source.remote.CommentRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class CommentRepository(
    private val commentRemoteDataSource: CommentRemoteDataSource,
) {

    private val inMemoryPost = InMemoryPostDataSource

    suspend fun create(comment: Comment): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        commentRemoteDataSource.create(comment)
            .catch { emit(Result.Error(Exception(it.message))) }
            .collect {
                inMemoryPost.addPostComment(comment)
                emit(Result.Success(it))
            }
    }

    suspend fun update(comment: Comment): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        commentRemoteDataSource.update(comment)
            .catch { emit(Result.Error(Exception(it.message))) }
            .collect {
                inMemoryPost.updatePostComment(comment)
                emit(Result.Success(it))
            }
    }

    suspend fun delete(postId: String, commentId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        commentRemoteDataSource.delete(postId, commentId)
            .catch { emit(Result.Error(Exception(it.message))) }
            .collect {
                inMemoryPost.deletePostComment(postId, commentId)
                emit(Result.Success(it))
            }
    }

    suspend fun getById(postId: String, commentId: String): Flow<Result<Comment>> = flow {
        emit(Result.Loading)

        val post = inMemoryPost.getById(postId)
        if (post != null) {
            val comment = post.comments[commentId]
            if (comment != null) {
                return@flow emit(Result.Success(comment.toCommentEntity()))
            }
        }
    }

}