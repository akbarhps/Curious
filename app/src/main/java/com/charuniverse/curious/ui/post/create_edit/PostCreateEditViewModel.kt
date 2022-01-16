package com.charuniverse.curious.ui.post.create_edit

import android.util.Log
import androidx.lifecycle.*
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.Post
import com.charuniverse.curious.data.model.PostDetail
import com.charuniverse.curious.data.repository.PostDetailRepository
import com.charuniverse.curious.data.repository.PostRepository
import com.charuniverse.curious.util.Event
import com.charuniverse.curious.util.Markdown
import com.charuniverse.curious.util.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostCreateEditViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val postDetailRepository: PostDetailRepository,
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
        postDetailRepository.observeData(id).map { handleResult(it) }
    }
    val post: LiveData<PostDetail?> = _post

    private val _markdownElement = MutableLiveData<Event<Markdown.Element>>()
    val markdownElement: LiveData<Event<Markdown.Element>> = _markdownElement

    fun markdownTagHandler(element: Markdown.Element) {
        _markdownElement.value = Event(element)
    }

    private fun handleResult(result: Result<PostDetail>): PostDetail? {
        if (result is Result.Error) {
            Log.e(TAG, "handleResult: ${result.exception.message}", result.exception)
            updateState(isLoading = false, errorMessage = result.exception.message.toString())
            return null
        }

        updateState(isLoading = false)
        return (result as Result.Success).data
    }

    // FIXME: 16/01/2022
    fun createOrUpdatePost() = viewModelScope.launch {
        updateState(isLoading = true)

        val post = Post(
            title = inputTitle.value.toString().trim().replace('\n', ' '),
            content = inputContent.value.toString().trim().replace("\n", "  \n"),
            createdBy = Preferences.userId,
        )

        val oldPost = _post.value
        if (oldPost != null) {
            post.id = oldPost.id
            post.createdAt = oldPost.createdAt
            post.updatedAt = System.currentTimeMillis()
        }

        val result = postRepository.save(post)
        if (result is Result.Error) {
            Log.e(TAG, "createPost failed: ${result.exception.message}", result.exception)
            updateState(isLoading = false, errorMessage = result.exception.message.toString())
        } else {
            inputTitle.value = ""
            inputContent.value = ""
            updateState(isLoading = false, isCompleted = true)
        }
    }
}