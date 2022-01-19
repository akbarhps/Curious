package com.charuniverse.curious.data.source

import android.util.Log
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.dto.PostDetail
import com.charuniverse.curious.data.model.Post
import com.charuniverse.curious.data.model.User
import com.charuniverse.curious.data.source.in_memory.InMemoryPostDataSource
import com.charuniverse.curious.data.source.in_memory.InMemoryUserDataSource
import com.charuniverse.curious.data.source.remote.PostRemoteDataSource
import com.charuniverse.curious.data.source.remote.UserRemoteDataSource
import com.charuniverse.curious.util.Preferences
import kotlinx.coroutines.flow.*

class PostRepository(
    private val postRemoteDataSource: PostRemoteDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
) {

    private val inMemoryPost = InMemoryPostDataSource
    private val inMemoryUser = InMemoryUserDataSource

    private suspend fun getUserById(userId: String): User? {
        var user: User? = inMemoryUser.getById(userId)
        if (user != null) {
            return user
        }

        userRemoteDataSource.getById(userId)
            .catch {}
            .collect { user = it }

        return user?.also { inMemoryUser.add(it) }
    }

    private suspend fun handlePost(post: PostDetail?): PostDetail? = post?.let {
        it.author = getUserById(it.createdBy)
        it.comments?.map { (_, comment) ->
            comment.author = getUserById(comment.createdBy)
        }

        it.isViewerLoved = it.lovers?.containsKey(Preferences.userId) ?: false
        inMemoryPost.add(it)
        return@let it
    }

    suspend fun observePosts(
        page: Int = 1,
        limit: Int = 1,
        forceRefresh: Boolean = false
    ): Flow<Result<List<PostDetail>>> = flow {
        if (!forceRefresh) {
            val posts = inMemoryPost.getAllAsList()
            if (posts.isNotEmpty()) {
                return@flow emit(Result.Success(posts))
            }
        }

        emit(Result.Loading)
        inMemoryPost.clear()
        inMemoryUser.clear()

        postRemoteDataSource.getAll(page, limit)
            .catch { throwable ->
                emit(Result.Error(Exception(throwable.message)))
            }
            .onEach { posts ->
                posts.map { handlePost(it) }
            }
            .collect {
                emit(Result.Success(it))
            }
    }

    suspend fun observeUserPosts(
        userId: String,
        forceRefresh: Boolean = false,
    ): Flow<Result<List<PostDetail>>> = flow {
        if (!forceRefresh) {
            val posts = inMemoryPost.getByUserId(userId)
            if (posts.isNotEmpty()) {
                return@flow emit(Result.Success(posts))
            }
        }

        emit(Result.Loading)
        postRemoteDataSource.getByUserId(userId)
            .catch { throwable ->
                emit(Result.Error(Exception(throwable.message)))
            }
            .onEach { posts ->
                posts.map { handlePost(it) }
            }
            .collect {
                emit(Result.Success(it))
            }
    }

    suspend fun observePost(
        postId: String,
        forceRefresh: Boolean = false
    ): Flow<Result<PostDetail>> = flow {
        if (!forceRefresh) {
            val post = inMemoryPost.getById(postId)
            if (post != null) {
                return@flow emit(Result.Success(post))
            }
        }

        emit(Result.Loading)
        postRemoteDataSource.getById(postId)
            .catch { throwable ->
                emit(Result.Error(Exception(throwable.message)))
            }
            .map {
                handlePost(it)
            }
            .collect {
                if (it == null) {
                    emit(Result.Error(IllegalArgumentException("Post not found")))
                } else {
                    emit(Result.Success(it))
                }
            }
    }

    suspend fun create(post: Post): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        postRemoteDataSource.create(post)
            .catch { throwable ->
                emit(Result.Error(Exception(throwable.message)))
            }
            .collect {
                inMemoryPost.add(post)
                emit(Result.Success(Unit))
            }
    }

    suspend fun update(post: Post): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        postRemoteDataSource.update(post)
            .catch { throwable ->
                emit(Result.Error(Exception(throwable.message)))
            }
            .collect {
                inMemoryPost.updatePost(post)
                emit(Result.Success(Unit))
            }
    }

    suspend fun delete(postId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        postRemoteDataSource.delete(postId)
            .catch { throwable ->
                emit(Result.Error(Exception(throwable.message)))
            }
            .collect {
                inMemoryPost.delete(postId)
                emit(Result.Success(Unit))
            }
    }

    suspend fun toggleLove(postId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)

        var postLovers = mutableMapOf<String, Long>()
        postRemoteDataSource.getById(postId).collect {
            if (it == null) throw IllegalArgumentException("Post not found")
            postLovers = it.lovers
        }

        val hasLike = postLovers.containsKey(Preferences.userId)
        postRemoteDataSource.toggleLove(postId, hasLike)
            .catch { throwable ->
                emit(Result.Error(Exception(throwable.message)))
            }
            .collect {
                inMemoryPost.updatePostLike(postId, hasLike)
                emit(Result.Success(Unit))
            }
    }
}