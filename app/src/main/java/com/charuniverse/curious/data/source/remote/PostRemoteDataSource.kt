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
        observablePosts.value = findAll()!!
    }

    private val observableUserPosts = MutableLiveData<Result<List<Post>>>()
    fun observeUserPosts(): LiveData<Result<List<Post>>> = observableUserPosts

    suspend fun refreshUserPosts(userId: String) = withContext(Dispatchers.Main) {
        observableUserPosts.value = findByUserId(userId)!!
    }

    fun observePost(postId: String): LiveData<Result<PostDetail>> {
        return observablePosts.map { posts ->
            val result = when (posts) {
                is Loading -> Loading
                is Error -> Error(posts.exception)
                is Success -> {
                    val post = posts.data.firstOrNull { it.id == postId }!!
                    Success(post)
                }
            }
            result
        }
    }

    suspend fun findAll(): Result<List<PostDetail>> = withContext(context) {
        return@withContext try {
            val docs = postRef.get().await()

            // TODO: refactor this shit
            val result = docs.children.map { snapshot ->
                snapshot.getValue(PostDetail::class.java)!!.let { post ->

                    var postAuthor = Cache.users[post.createdBy]
                    if (postAuthor == null) {
                        val userDoc = userRef.child(post.createdBy).get().await()
                        postAuthor = userDoc.getValue(User::class.java)!!
                    }
                    post.author = postAuthor

                    post.comments?.map { (_, comment) ->
                        var commentAuthor = Cache.users[comment.createdBy]
                        if (commentAuthor == null) {
                            val userDoc = userRef.child(post.createdBy).get().await()
                            commentAuthor = userDoc.getValue(User::class.java)!!
                        }
                        comment.author = commentAuthor
                    }

                    post.isViewerLoved = post.lovers?.containsKey(Preferences.userId) ?: false
                    return@map post
                }
            }

            Success(result)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun findById(postId: String): Result<PostDetail> = withContext(context) {
        return@withContext try {
            val snapshot = postRef.child(postId).get().await()

            // TODO: refactor this shit
            val result = snapshot.getValue(PostDetail::class.java)!!.let { post ->
                var postAuthor = Cache.users[post.createdBy]
                if (postAuthor == null) {
                    val userDoc = userRef.child(post.createdBy).get().await()
                    postAuthor = userDoc.getValue(User::class.java)!!
                }
                post.author = postAuthor

                post.comments?.toSortedMap()?.map { (_, comment) ->
                    var commentAuthor = Cache.users[comment.createdBy]
                    if (commentAuthor == null) {
                        val userDoc = userRef.child(post.createdBy).get().await()
                        commentAuthor = userDoc.getValue(User::class.java)!!
                    }
                    comment.author = commentAuthor
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
                updates["${postId}/comments/${commentId}"] = null
                updates["${postId}/commentCount"] = ServerValue.increment(-1)
                postRef.updateChildren(updates)
                Success(Unit)
            } catch (e: Exception) {
                Error(e)
            }
        }

    suspend fun addLove(postId: String): Result<Unit> = withContext(context) {
        val uid = Preferences.userId
        val updates: MutableMap<String, Any> = HashMap()

        return@withContext try {
            val hasLike = postRef.child("$postId/lovers/$uid").get().await()

            if (hasLike.getValue(Long::class.java) == null) {
                updates["$postId/lovers/$uid"] = System.currentTimeMillis()
                updates["$postId/loveCount"] = ServerValue.increment(1)
                postRef.updateChildren(updates)
            }
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun deleteLove(postId: String): Result<Unit> = withContext(context) {
        val uid = Preferences.userId
        val updates: MutableMap<String, Any?> = HashMap()

        return@withContext try {
            val hasLike = postRef.child("$postId/lovers/$uid").get().await()

            if (hasLike.getValue(Long::class.java) != null) {
                updates["$postId/lovers/$uid"] = null
                updates["$postId/loveCount"] = ServerValue.increment(-1)
                postRef.updateChildren(updates)
            }
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }
}