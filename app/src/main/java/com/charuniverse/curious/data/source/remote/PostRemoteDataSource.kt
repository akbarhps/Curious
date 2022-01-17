package com.charuniverse.curious.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.Result.*
import com.charuniverse.curious.data.entity.Comment
import com.charuniverse.curious.data.entity.Post
import com.charuniverse.curious.data.entity.User
import com.charuniverse.curious.data.model.PostDetail
import com.charuniverse.curious.exception.NotFound
import com.charuniverse.curious.util.Cache
import com.charuniverse.curious.util.Preferences
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PostRemoteDataSource(
    firebaseDatabase: FirebaseDatabase,
    private val context: CoroutineDispatcher = Dispatchers.IO,
) {

    private val postRef = firebaseDatabase.reference.child("posts")
    private val userRef = firebaseDatabase.reference.child("users")

    private val observablePosts = MutableLiveData<Result<List<PostDetail>>>()
    fun observePosts(): LiveData<Result<List<PostDetail>>> = observablePosts

    suspend fun refreshPosts() = withContext(Dispatchers.Main) {
        Cache.clearCache()
        observablePosts.value = findAll() ?: Success(listOf<PostDetail>())
    }

    fun observeUserPosts(userId: String): LiveData<Result<List<PostDetail>>> {
        return observablePosts.map { posts ->
            return@map when (posts) {
                is Loading -> Loading
                is Error -> Error(posts.exception)
                is Success -> {
                    val data = posts.data.filter { it.createdBy == userId }
                    Success(data)
                }
            }
        }
    }

    fun observePost(postId: String): LiveData<Result<PostDetail>> {
        return observablePosts.map { posts ->
            return@map when (posts) {
                is Loading -> Loading
                is Error -> Error(posts.exception)
                is Success -> {
                    val post = posts.data.firstOrNull { it.id == postId }
                    if (post == null) Error(NotFound("Post not found"))
                    Success(post!!)
                }
            }
        }
    }

    // TODO: move to user remote data source maybe?
    private suspend fun findUser(userId: String): User? {
        var user = Cache.users[userId]
        if (user == null) {
            val userDoc = userRef.child(userId).get().await()
            user = userDoc.getValue(User::class.java)
        }
        return user
    }

    suspend fun findAll(): Result<List<PostDetail>> = withContext(context) {
        return@withContext try {
            val docs = postRef.orderByChild("createdAt").get().await()

            val result = docs.children.map { snapshot ->
                val post = snapshot.getValue(PostDetail::class.java)!!
                post.author = findUser(post.createdBy)
                post.comments?.toSortedMap()?.map { (_, comment) ->
                    comment.author = findUser(comment.createdBy)
                }

                post.isViewerLoved = post.lovers?.containsKey(Preferences.userId) ?: false
                return@map post
            }

            Success(result)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun findById(postId: String): Result<PostDetail> = withContext(context) {
        return@withContext try {
            val snapshot = postRef.child(postId).get().await()

            val result = snapshot.getValue(PostDetail::class.java)!!.let { post ->
                post.author = findUser(post.createdBy)
                post.comments?.toSortedMap()?.map { (_, comment) ->
                    comment.author = findUser(comment.createdBy)
                }

                return@let post
            }

            Success(result)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun findByUserId(userId: String): Result<List<Post>> = withContext(context) {
        return@withContext try {
            val docs = postRef.orderByChild("createdBy")
                .equalTo(userId)
                .get().await()
            Success(docs.children.map { it.getValue(Post::class.java)!! })
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun save(post: Post): Result<Unit> = withContext(context) {
        return@withContext try {
            postRef.child(post.id).setValue(post).await()
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun update(post: Post): Result<Unit> = withContext(context) {
        return@withContext try {
            val updates: MutableMap<String, Any> = HashMap()
            postRef.child(post.id).get().await().getValue(Post::class.java)
                ?: return@withContext Error(NotFound("Post not found"))

            updates["${post.id}/title"] = post.title
            updates["${post.id}/content"] = post.content
            updates["${post.id}/updatedAt"] = System.currentTimeMillis()
            postRef.updateChildren(updates)
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun delete(postId: String): Result<Unit> = withContext(context) {
        return@withContext try {
            postRef.child(postId).setValue(null).await()
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun addComment(comment: Comment): Result<Unit> = withContext(context) {
        val updates: MutableMap<String, Any> = HashMap()
        return@withContext try {
            postRef.child(comment.postId).get().await().getValue(Post::class.java)
                ?: return@withContext Error(NotFound("Post not found"))

            updates["${comment.postId}/comments/${comment.id}"] = comment
            updates["${comment.postId}/commentCount"] = ServerValue.increment(1)

            postRef.updateChildren(updates)
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun deleteComment(postId: String, commentId: String): Result<Unit> =
        withContext(context) {
            val updates: MutableMap<String, Any?> = HashMap()
            return@withContext try {
                postRef.child(postId).get().await().getValue(Post::class.java)
                    ?: return@withContext Error(NotFound("Post not found"))

                updates["${postId}/comments/${commentId}"] = null
                updates["${postId}/commentCount"] = ServerValue.increment(-1)
                postRef.updateChildren(updates)
                Success(Unit)
            } catch (e: Exception) {
                Error(e)
            }
        }

    suspend fun toggleLove(postId: String): Result<Unit> = withContext(context) {
        val uid = Preferences.userId
        val updates: MutableMap<String, Any?> = HashMap()
        return@withContext try {
            postRef.child(postId).get().await().getValue(Post::class.java)
                ?: return@withContext Error(NotFound("Post not found"))

            val hasLike = postRef.child("$postId/lovers/$uid").get().await()

            if (hasLike.getValue(Long::class.java) == null) {
                updates["$postId/lovers/$uid"] = System.currentTimeMillis()
                updates["$postId/loveCount"] = ServerValue.increment(1)
            } else {
                updates["$postId/lovers/$uid"] = null
                updates["$postId/loveCount"] = ServerValue.increment(-1)
            }

            postRef.updateChildren(updates)
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }
}