package com.charuniverse.curious.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.Result.*
import com.charuniverse.curious.data.entity.Comment
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class CommentRemoteDataSource(
    firebaseDatabase: FirebaseDatabase,
    private val context: CoroutineContext = Dispatchers.IO,
) {
    private val commentRef = firebaseDatabase.reference.child("comments")

    private val observableComments = MutableLiveData<Result<List<Comment>>>()
    fun observeComments(): LiveData<Result<List<Comment>>> = observableComments

    suspend fun refreshComments(postId: String) = withContext(Dispatchers.Main) {
        observableComments.value = findByPostId(postId)!!
    }

    suspend fun findByPostId(postId: String): Result<List<Comment>> = withContext(context) {
        return@withContext try {
            val docs = commentRef
                .orderByChild("postId").equalTo(postId)
                .get().await()

            Success(docs.children.map { it.getValue(Comment::class.java)!! })
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun save(comment: Comment): Result<Unit> = withContext(context) {
        return@withContext try {
            commentRef.child(comment.id).setValue(comment)
                .await()
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun delete(commentId: String): Result<Unit> = withContext(context) {
        return@withContext try {
            commentRef.child(commentId).setValue(null).await()
            Success(Unit)
        } catch (e: Exception) {
            Error(e)
        }
    }
}