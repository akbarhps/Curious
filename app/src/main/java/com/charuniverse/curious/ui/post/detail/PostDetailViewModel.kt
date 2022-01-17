package com.charuniverse.curious.ui.post.detail

import android.util.Log
import androidx.lifecycle.*
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.Comment
import com.charuniverse.curious.data.model.PostDetail
import com.charuniverse.curious.data.repository.PostRepository
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
        postError: String? = null,
        commentError: String? = null,
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
            updateState(postError = "No Post Id Provided")
            return
        }

        updateState(isLoading = true)
        _postId.value = id
    }

    private val _post = _postId.switchMap { id ->
        postRepository.observePost(id).map {
            handlePostResult(it)
        }
    }

    val post: LiveData<PostDetail?> = _post

    // two way data binding
    val inputComment = MutableLiveData("")

    private fun handlePostResult(result: Result<PostDetail>): PostDetail? {
        if (result is Result.Error) {
            Log.e(TAG, "handleResult: ${result.exception.message}", result.exception)

            updateState(postError = result.exception.message.toString())
            return null
        }

        updateState(isLoading = false)
        return (result as Result.Success).data
    }

    fun refreshPost() = viewModelScope.launch {
        postRepository.refreshPosts()
    }

    fun handlePostLove(isLoved: Boolean) = viewModelScope.launch {
        val postId = _postId.value!!

        if (isLoved) {
            postRepository.deleteLove(postId)
        } else {
            postRepository.addLove(postId)
        }

        postRepository.refreshPosts()
    }

    fun uploadComment() = viewModelScope.launch {
        updateState(isLoading = true)

        val postId = _postId.value!!
        val comment = Comment(
            postId = postId,
            content = inputComment.value.toString().trim().replace("\n", "  \n"),
        )

        val result = postRepository.addComment(comment)
        if (result is Result.Error) {
            Log.e(TAG, "uploadComment: ${result.exception.message}", result.exception)

            updateState(commentError = result.exception.message.toString())
            return@launch
        }

        updateState(uploadCommentSuccess = true)
        postRepository.refreshPosts()
    }

    fun deletePost() = viewModelScope.launch {
        when (val result = postRepository.delete(_postId.value!!)) {
            is Result.Loading -> updateState(isLoading = true)
            is Result.Success -> updateState(deletePostSuccess = true)
            is Result.Error -> {
                Log.e(TAG, "deletePost: ${result.exception.message}", result.exception)
                updateState(postError = result.exception.message.toString())
            }
        }
    }
}