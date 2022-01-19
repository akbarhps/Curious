package com.charuniverse.curious.ui.comment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.model.Comment
import com.charuniverse.curious.data.source.CommentRepository
import com.charuniverse.curious.data.source.PostRepository
import com.charuniverse.curious.util.Event
import com.charuniverse.curious.util.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CommentCreateEditViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "VMCommentCreateEdit"
    }

    private val _viewState = MutableLiveData<Event<CommentCreateEditViewState>>()
    val viewState: LiveData<Event<CommentCreateEditViewState>> = _viewState

    private fun updateState(
        isLoading: Boolean = false,
        error: Exception? = null,
        isFinished: Boolean = false,
        postTitle: String? = null,
        content: String? = null,
        contentError: String? = null,
    ) {
        _viewState.value = Event(
            CommentCreateEditViewState(
                isLoading = isLoading,
                error = error,
                isFinished = isFinished,
                postTitle = postTitle,
                content = content,
                contentError = contentError
            )
        )
    }

    val commentContent = MutableLiveData("")

    private lateinit var currentPostId: String
    private var currentCommentId: String? = null

    fun setPostId(postId: String, commentId: String?) {
        currentPostId = postId
        currentCommentId = commentId
        refreshContent()
    }

    fun refreshContent() = viewModelScope.launch {
        postRepository.observePost(currentPostId, false).collect { res ->
            resultHandler(res) { updateState(postTitle = it.title) }
        }
        currentCommentId?.let { id ->
            commentRepository.getById(currentPostId, id).collect { res ->
                resultHandler(res) { updateState(content = it.content) }
            }
        }
    }

    fun uploadComment(content: String) = viewModelScope.launch {
        val newContent = content.trim().replace("\n", "  \n")

        if (newContent.isBlank()) {
            updateState(contentError = "Content can't be blank")
            return@launch
        }

        val comment = Comment(
            id = currentCommentId ?: UUID.randomUUID().toString(),
            postId = currentPostId,
            content = newContent,
            createdAt = System.currentTimeMillis(),
            createdBy = Preferences.userId,
            updatedAt = if (currentCommentId != null) System.currentTimeMillis() else null,
        )

        if (currentCommentId == null) {
            commentRepository.create(comment).collect {
                resultHandler(it) { updateState(isFinished = true) }
            }
        } else {
            commentRepository.update(comment).collect {
                resultHandler(it) { updateState(isFinished = true) }
            }
        }
    }

    private fun <T> resultHandler(result: Result<T>, onSuccess: (T) -> Unit) {
        when (result) {
            is Result.Loading -> updateState(isLoading = true)
            is Result.Success -> onSuccess(result.data)
            is Result.Error -> {
                Log.e(TAG, "resultHandler: ${result.exception.message}", result.exception)
                updateState(error = result.exception)
            }
        }
    }
}