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

    val inputTitle = MutableLiveData("")
    val inputContent = MutableLiveData("")

    private val _postId = MutableLiveData<String>()
    fun setPostId(id: String?) {
        if (id == null) {
            _postId.value = ""
        }

        if (_isLoading.value == true || _postId.value == id || id == null) {
            return
        }

        _postId.value = id!!
        _isLoading.value = true
    }

    private val _post = _postId.switchMap { id ->
        if (id.isBlank()) return@switchMap MutableLiveData(PostDetail())
        postDetailRepository.observeData(id).map { handleResult(it) }
    }
    val post: LiveData<PostDetail?> = _post

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    private val _isDone = MutableLiveData<Event<Boolean>>()
    val isDone: LiveData<Event<Boolean>> = _isDone

    private val _markdownElement = MutableLiveData<Event<Markdown.Element>>()
    val markdownElement: LiveData<Event<Markdown.Element>> = _markdownElement

    fun markdownTagHandler(element: Markdown.Element) {
        _markdownElement.value = Event(element)
    }

    private fun handleResult(result: Result<PostDetail>): PostDetail? {
        if (result is Result.Error) {
            Log.e(TAG, "handleResult: ${result.exception.message}", result.exception)
            _errorMessage.value = Event(result.exception.message.toString())
            return null
        }

        _isLoading.value = false
        return (result as Result.Success).data
    }

    fun createOrUpdatePost() = viewModelScope.launch {
        _isLoading.value = true

        val post = Post(
            title = inputTitle.value.toString().trim().replace('\n', ' '),
            content = inputContent.value.toString().trim(),
            createdBy = Preferences.userId,
        )

        val oldPost = _post.value!!
        if (oldPost.id.isNotBlank()) {
            post.id = oldPost.id
            post.createdAt = oldPost.createdAt
            post.updatedAt = System.currentTimeMillis()
        }

        val result = postRepository.save(post)
        if (result is Result.Error) {
            Log.e(TAG, "createPost failed: ${result.exception.message}", result.exception)
            _errorMessage.value = Event(result.exception.message.toString())
        }

        if (_errorMessage.value == null) {
            inputTitle.value = ""
            inputContent.value = ""
            _isDone.value = Event(true)
        }

        _isLoading.value = false
    }
}