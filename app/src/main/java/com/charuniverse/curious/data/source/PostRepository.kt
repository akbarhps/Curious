package com.charuniverse.curious.data.source

import android.util.Log
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.dto.PostDetail
import com.charuniverse.curious.data.model.NotificationData
import com.charuniverse.curious.data.model.Post
import com.charuniverse.curious.data.model.User
import com.charuniverse.curious.data.source.in_memory.InMemoryPostDataSource
import com.charuniverse.curious.data.source.in_memory.InMemoryUserDataSource
import com.charuniverse.curious.data.source.remote.NotificationAPI
import com.charuniverse.curious.data.source.remote.PostRemoteDataSource
import com.charuniverse.curious.data.source.remote.UserRemoteDataSource
import com.charuniverse.curious.util.NotificationBuilder
import com.charuniverse.curious.util.Preferences
import kotlinx.coroutines.flow.*

class PostRepository(
    private val postRemoteDataSource: PostRemoteDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val notificationAPI: NotificationAPI,
) {

    private val inMemoryPost = InMemoryPostDataSource
    private val inMemoryUser = InMemoryUserDataSource

    private suspend fun getUserById(userId: String): User? {
        var user: User? = inMemoryUser.getById(userId)
        if (user != null) {
            return user
        }

        return try {
            user = userRemoteDataSource.getById(userId)
            user?.also { inMemoryUser.add(it) }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun handlePost(post: PostDetail?): PostDetail? = post?.let {
        val viewerUID = Preferences.userId
        it.author = getUserById(it.createdBy)
        it.comments.map { (_, comment) ->
            comment.author = getUserById(comment.createdBy)
            comment.isViewerLoved = comment.lovers.containsKey(viewerUID)
        }

        it.isViewerLoved = it.lovers.containsKey(viewerUID)
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

        try {
            val posts = postRemoteDataSource.getAll(page, limit)
                .map { handlePost(it)!! }
            emit(Result.Success(posts))
        } catch (e: Exception) {
            emit(Result.Error(e))
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
        try {
            val posts = postRemoteDataSource.getByUserId(userId)
                .map { handlePost(it)!! }
            emit(Result.Success(posts))
        } catch (e: Exception) {
            emit(Result.Error(e))
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
        try {
            var post = postRemoteDataSource.getById(postId)
            post = handlePost(post)
                ?: throw IllegalArgumentException("Post not found")
            emit(Result.Success(post))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun create(post: Post): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            postRemoteDataSource.create(post)
            inMemoryPost.add(post)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun update(post: Post): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            postRemoteDataSource.update(post)
            inMemoryPost.updatePost(post)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun delete(postId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        try {
            postRemoteDataSource.delete(postId)
            inMemoryPost.delete(postId)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun toggleLove(postId: String): Flow<Result<Unit>> = flow {
        emit(Result.Loading)

        try {
            val post = postRemoteDataSource.getById(postId)
                ?: throw IllegalArgumentException("Post not found")
            val isUserCurrentlyLovePost = post.lovers.containsKey(Preferences.userId)
            postRemoteDataSource.toggleLove(postId, isUserCurrentlyLovePost)

            inMemoryPost.togglePostLove(postId, isUserCurrentlyLovePost)
            emit(Result.Success(Unit))
            if (isUserCurrentlyLovePost) return@flow
        } catch (e: Exception) {
            emit(Result.Error(e))
        }

        val notification = NotificationBuilder.build(
            postId,
            NotificationData.EVENT_POST_LIKE
        ) ?: return@flow

        try {
            notificationAPI.send(notification)
        } catch (e: Exception) {
            Log.e("PostRepository", "toggleLove: ${e.message}", e)
        }
    }
}