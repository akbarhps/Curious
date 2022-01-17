package com.charuniverse.curious.ui.post.detail

import android.util.Log
import androidx.lifecycle.*
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.Comment
import com.charuniverse.curious.data.model.PostDetail
import com.charuniverse.curious.data.repository.CommentRepository
import com.charuniverse.curious.data.repository.PostRepository
import com.charuniverse.curious.data.repository.UserRepository
import com.charuniverse.curious.util.Event
import com.charuniverse.curious.util.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
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

        _postId.value?.let {
            viewModelScope.launch {
                commentRepository.refreshComments(it)
            }
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

    private val _comments: LiveData<List<Comment>> = commentRepository.observeComments()
        .distinctUntilChanged()
        .switchMap {
            return@switchMap handleCommentsResult(it)
        }

    val comments: LiveData<List<Comment>> = _comments

    private fun handlePostResult(result: Result<PostDetail>): PostDetail? {
        if (result is Result.Error) {
            Log.e(TAG, "handleResult: ${result.exception.message}", result.exception)

            updateState(postError = result.exception.message.toString())
            return null
        }

        val data = (result as Result.Success).data
        viewModelScope.launch { commentRepository.refreshComments(data.id) }
        updateState(isLoading = false)
        return data
    }

    private fun handleCommentsResult(result: Result<List<Comment>>): LiveData<List<Comment>> {
        if (result is Result.Error) {
            Log.e(TAG, "handleCommentsResult: ${result.exception.message}", result.exception)

            updateState(commentError = result.exception.message.toString())
            return MutableLiveData(listOf())
        }

        updateState(isLoading = false)
        return MutableLiveData((result as Result.Success).data)
    }

    fun uploadComment() = viewModelScope.launch {
        updateState(isLoading = true)

        val postId = _postId.value!!
        val comment = Comment(
            postId = postId,
            content = inputComment.value!!,
            createdBy = Preferences.userId,
        )

        val result = commentRepository.save(comment)
        if (result is Result.Error) {
            Log.e(TAG, "uploadComment: ${result.exception.message}", result.exception)

            updateState(commentError = result.exception.message.toString())
            return@launch
        }

        updateState(uploadCommentSuccess = true)
        commentRepository.refreshComments(postId)
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