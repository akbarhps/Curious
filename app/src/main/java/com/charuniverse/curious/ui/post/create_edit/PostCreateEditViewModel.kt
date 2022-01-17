package com.charuniverse.curious.ui.post.create_edit

import android.util.Log
import androidx.lifecycle.*
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.Post
import com.charuniverse.curious.data.model.PostDetail
import com.charuniverse.curious.data.repository.PostRepository
import com.charuniverse.curious.util.Event
import com.charuniverse.curious.util.Markdown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostCreateEditViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "PostCreateViewModel"
    }

    private val _viewState = MutableLiveData<Event<PostCreateViewState>>()
    val viewState: LiveData<Event<PostCreateViewState>> = _viewState

    private fun updateState(
        isLoading: Boolean = false, errorMessage: String? = null, isCompleted: Boolean = false,
    ) {
        _viewState.value = Event(
            PostCreateViewState(
                isLoading = isLoading,
                errorMessage = errorMessage,
                isCompleted = isCompleted,
            )
        )
    }

    // two way data binding must be public
    val inputTitle = MutableLiveData("")
    val inputContent = MutableLiveData("")

    private val _postId = MutableLiveData<String>()
    fun setPostId(id: String?) {
        if (id == null) _postId.value = ""
        if (id == null || _postId.value == id) return

        _postId.value = id!!
        updateState(isLoading = true)
    }

    private val _post = _postId.switchMap { id ->
        if (id.isBlank()) return@switchMap MutableLiveData(null)
        postRepository.observePost(id).map { handleResult(it) }
    }
    val post: LiveData<PostDetail?> = _post

    private val _markdownElement = MutableLiveData<Event<Markdown.Element>>()
    val markdownElement: LiveData<Event<Markdown.Element>> = _markdownElement

    fun markdownTagHandler(element: Markdown.Element) {
        _markdownElement.value = Event(element)
    }

    private fun handleResult(result: Result<PostDetail?>): PostDetail? {
        updateState(isLoading = false)
        return when (result) {
            is Result.Success -> result.data
            is Result.Loading -> null
            is Result.Error -> {
                Log.e(TAG, "handleResult: ${result.exception.message}", result.exception)
                updateState(errorMessage = result.exception.message.toString())
                null
            }
        }
    }

    // FIXME: 16/01/2022
    fun createOrUpdatePost() = viewModelScope.launch {
        updateState(isLoading = true)

        val post = Post(
            title = inputTitle.value.toString().trim().replace('\n', ' '),
            content = inputContent.value.toString().trim().replace("\n", "  \n"),
        )

        val result = _post.value?.let {
            post.id = it.id
            postRepository.update(post)
        } ?: postRepository.save(post)

        if (result is Result.Error) {
            Log.e(TAG, "createPost failed: ${result.exception.message}", result.exception)
            updateState(isLoading = false, errorMessage = result.exception.message.toString())
        } else {
            inputTitle.value = ""
            inputContent.value = ""
            postRepository.refreshPosts()
            updateState(isLoading = false, isCompleted = true)
        }
    }
}