package com.charuniverse.curious.ui.post.create_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.model.Post
import com.charuniverse.curious.data.source.PostRepository
import com.charuniverse.curious.util.Event
import com.charuniverse.curious.util.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PostCreateEditViewModel @Inject constructor(private val postRepository: PostRepository) :
    ViewModel() {

    companion object {
        private const val TAG = "PostCreateViewModel"
    }

    private val _viewState = MutableLiveData<Event<PostCreateEditViewState>>()
    val viewState: LiveData<Event<PostCreateEditViewState>> = _viewState

    private fun updateState(
        isLoading: Boolean = false,
        isCompleted: Boolean = false,
        post: Post? = null,
        error: Exception? = null,
        titleError: String? = null,
        contentError: String? = null,
    ) {
        _viewState.value = Event(
            PostCreateEditViewState(
                isLoading = isLoading,
                error = error,
                isCompleted = isCompleted,
                post = post,
                titleError = titleError,
                contentError = contentError
            )
        )
    }

    val postTitle = MutableLiveData("")
    val postContent = MutableLiveData("")

    private var currentPostId: String? = null

    fun setPostId(id: String?) = id?.let {
        currentPostId = id
        refreshPost()
    }

    private fun refreshPost() = viewModelScope.launch {
        if (currentPostId == null) return@launch
        postRepository.observePost(currentPostId!!, false).collect { res ->
            resultHandler(res) { updateState(post = it.toDomainPost()) }
        }
    }

    private fun validateInput(): Boolean {
        val newTitle = postTitle.value!!
            .trim().replace('\n', ' ')

        val newContent = postContent.value!!
            .trim().replace("\n", "  \n")

        var titleError: String? = null
        var contentError: String? = null

        if (newTitle.isBlank()) {
            titleError = "Title can't be blank"
        }

        if (newContent.isBlank()) {
            contentError = "Content can't be blank"
        }

        return if (titleError != null || contentError != null) {
            updateState(titleError = titleError, contentError = contentError)
            false
        } else {
            true
        }
    }

    fun savePost() = viewModelScope.launch {
        val newTitle = postTitle.value!!
            .trim().replace('\n', ' ')

        val newContent = postContent.value!!
            .trim().replace("\n", "  \n")

        if (!validateInput()) return@launch

        val newPost = Post(
            id = currentPostId ?: UUID.randomUUID().toString(),
            title = newTitle,
            content = newContent,
            createdBy = Preferences.userId,
            createdAt = System.currentTimeMillis(),
            updatedAt = if (currentPostId != null) System.currentTimeMillis() else null
        )

        if (currentPostId == null) {
            postRepository.create(newPost).collect {
                resultHandler(it) { updateState(isCompleted = true) }
            }
        } else {
            postRepository.update(newPost).collect {
                resultHandler(it) { updateState(isCompleted = true) }
            }
        }
    }

    private fun <T> resultHandler(result: Result<T>, onSuccess: (T) -> Unit) {
        when (result) {
            is Result.Loading -> updateState(isLoading = true)
            is Result.Error -> updateState(error = result.exception)
            is Result.Success -> onSuccess(result.data)
        }
    }
}