package com.charuniverse.curious.ui.post.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.dto.PostDetail
import com.charuniverse.curious.data.source.CommentRepository
import com.charuniverse.curious.data.source.PostRepository
import com.charuniverse.curious.exception.NotFound
import com.charuniverse.curious.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
) :
    ViewModel() {

    companion object {
        private const val TAG = "VMPostDetail"
        private var isFirstLoad = true
    }

    private val _viewState = MutableLiveData<Event<PostDetailViewState>>()
    val viewState: LiveData<Event<PostDetailViewState>> = _viewState

    private fun updateState(
        isLoading: Boolean = false,
        isFinished: Boolean = false,
        error: Exception? = null,
        post: PostDetail? = null,
        selectedUserId: String? = null,
        selectedCommentId: String? = null,
    ) {
        _viewState.value = Event(
            PostDetailViewState(
                isLoading = isLoading,
                isFinished = isFinished,
                error = error,
                post = post,
                selectedUserId = selectedUserId,
                selectedCommentId = selectedCommentId,
            )
        )
    }

    private lateinit var currentPostId: String

    fun setPostId(id: String) {
        if (id.isBlank()) {
            updateState(isFinished = true, error = NotFound("Post not found"))
            return
        }

        currentPostId = id
        refreshPost(isFirstLoad)

        if (isFirstLoad) {
            isFirstLoad = false
        }
    }

    fun refreshPost(forceRefresh: Boolean = false) = viewModelScope.launch {
        Log.i(TAG, "refreshPost: Terpanggil")
        postRepository.observePost(currentPostId, forceRefresh).collect {
            handleResult(it) { post -> updateState(post = post) }
        }
    }

    fun setSelectedUserId(userId: String) {
        updateState(selectedUserId = userId)
    }

    fun setSelectedCommentId(commentId: String) {
        updateState(selectedCommentId = commentId)
    }

    fun toggleLove() = viewModelScope.launch {
        postRepository.toggleLove(currentPostId).collect {
            handleResult(it) { refreshPost(false) }
        }
    }

    fun deleteComment(commentId: String) = viewModelScope.launch {
        commentRepository.delete(currentPostId, commentId).collect {
            handleResult(it) { refreshPost(false) }
        }
    }

    fun deletePost() = viewModelScope.launch {
        postRepository.delete(currentPostId).collect {
            handleResult(it) { updateState(isFinished = true) }
        }
    }

    private fun <T> handleResult(result: Result<T>, successCallback: (T) -> Unit) {
        when (result) {
            is Result.Loading -> updateState(isLoading = true)
            is Result.Error -> {
                Log.e(TAG, "handleResult: ${result.exception.message}", result.exception)
                updateState(error = result.exception)
            }
            is Result.Success -> successCallback(result.data)
        }
    }
}