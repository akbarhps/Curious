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
        selectedUserId: String? = null,
        selectedCommentId: String? = null,
    ) {
        _viewState.value = Event(
            PostDetailViewState(
                isLoading = isLoading,
                isFinished = isFinished,
                error = error,
                selectedUserId = selectedUserId,
                selectedCommentId = selectedCommentId,
            )
        )
    }

    private val _post = MutableLiveData<PostDetail>()
    val post: LiveData<PostDetail> = _post

    private lateinit var currentPostId: String

    fun setPostId(id: String) {
        if (id.isBlank()) {
            updateState(isFinished = true, error = Exception("Post not found"))
            return
        }

        currentPostId = id
        refreshPost(isFirstLoad)

        if (isFirstLoad) {
            isFirstLoad = false
        }
    }

    fun refreshPost(forceRefresh: Boolean = false) = viewModelScope.launch {
        postRepository.observePost(currentPostId, forceRefresh).collect { res ->
            handleResult(res) {
                updateState(isLoading = false)
                _post.value = it
            }
        }
    }

    fun setSelectedUserId(userId: String) {
        updateState(selectedUserId = userId)
    }

    fun setSelectedCommentId(commentId: String) {
        updateState(selectedCommentId = commentId)
    }

    fun togglePostLove() = viewModelScope.launch {
        postRepository.toggleLove(currentPostId).collect {
            handleResult(it) { refreshPost(false) }
        }
    }

    fun toggleCommentLove(commentId: String) = viewModelScope.launch {
        commentRepository.toggleLove(currentPostId, commentId).collect {
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