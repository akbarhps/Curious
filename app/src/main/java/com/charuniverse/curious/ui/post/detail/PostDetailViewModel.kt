package com.charuniverse.curious.ui.post.detail

import android.util.Log
import androidx.lifecycle.*
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.Comment
import com.charuniverse.curious.data.model.PostDetail
import com.charuniverse.curious.data.repository.PostRepository
import com.charuniverse.curious.exception.NotFound
import com.charuniverse.curious.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "PostDetailViewModel"
    }

    private val _viewState = MutableLiveData<Event<PostDetailViewState>>()
    val viewState: LiveData<Event<PostDetailViewState>> = _viewState

    private fun updateState(
        isLoading: Boolean = false,
        postError: Exception? = null,
        commentError: Exception? = null,
        uploadCommentSuccess: Boolean = false,
        deletePostSuccess: Boolean = false,
    ) {
        _viewState.value = Event(
            PostDetailViewState(
                isLoading = isLoading,
                postError = postError,
                commentError = commentError,
                uploadCommentSuccess = uploadCommentSuccess,
                deletePostSuccess = deletePostSuccess,
            )
        )
    }

    private val _postId = MutableLiveData<String>()

    fun setPostId(id: String) {
        if (id.isBlank()) {
            updateState(postError = NotFound("Post not found"))
            return
        }

        updateState(isLoading = true)
        _postId.value = id
    }

    private val _post = _postId.switchMap { id ->
        postRepository.observePost(id).map { handlePostResult(it) }
    }
    val post: LiveData<PostDetail> = _post

    // two way data binding
    val inputComment = MutableLiveData("")

    private fun handlePostResult(result: Result<PostDetail>): PostDetail {
        return when (result) {
            is Result.Loading -> PostDetail()
            is Result.Error -> {
                Log.e(TAG, "handleResult: ${result.exception.message}", result.exception)
                updateState(postError = result.exception)
                PostDetail()
            }
            is Result.Success -> {
                updateState(isLoading = false)
                result.data
            }
        }
    }

    fun refreshPost() = viewModelScope.launch {
        postRepository.refreshPosts()
    }

    fun togglePostLove() = viewModelScope.launch {
        postRepository.toggleLove(_postId.value!!)
        refreshPost()
    }

    fun uploadComment() = viewModelScope.launch {
        updateState(isLoading = true)

        val comment = Comment(
            postId = _postId.value!!,
            content = inputComment.value.toString().trim().replace("\n", "  \n"),
        )

        val result = postRepository.addComment(comment)
        if (result is Result.Error) {
            Log.e(TAG, "uploadComment: ${result.exception.message}", result.exception)
            updateState(commentError = result.exception)
            return@launch
        }

        refreshPost()
        updateState(uploadCommentSuccess = true)
    }

    fun deletePost() = viewModelScope.launch {
        val result = postRepository.delete(_postId.value!!)
        if (result is Result.Error) {
            Log.e(TAG, "deletePost: ${result.exception.message}", result.exception)
            updateState(postError = result.exception)
        } else {
            refreshPost()
            updateState(deletePostSuccess = true)
        }
    }
}