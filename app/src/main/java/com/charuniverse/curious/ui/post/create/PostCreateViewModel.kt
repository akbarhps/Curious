package com.charuniverse.curious.ui.post.create

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.curious.data.entity.Post
import com.charuniverse.curious.data.exception
import com.charuniverse.curious.data.failed
import com.charuniverse.curious.data.repository.AuthRepository
import com.charuniverse.curious.data.repository.PostRepository
import com.charuniverse.curious.util.Event
import com.charuniverse.curious.util.Markdown
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostCreateViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "PostCreateViewModel"
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<Event<Exception>>()
    val error: LiveData<Event<Exception>> = _error

    private val _uploadSucceed = MutableLiveData<Event<Boolean>>()
    val uploadSucceed: LiveData<Event<Boolean>> = _uploadSucceed

    val title = MutableLiveData("")
    val body = MutableLiveData("")

    private val _helperEvent = MutableLiveData<Event<Markdown.Element>>()
    val helperEvent: LiveData<Event<Markdown.Element>> = _helperEvent

    fun helperListener(element: Markdown.Element) {
        _helperEvent.value = Event(element)
    }

    fun createPost() = viewModelScope.launch {
        _isLoading.value = true

        val loginUser = authRepository.getLoginUser()
        val post = Post(
            title = title.value.toString(),
            body = body.value.toString(),
            createdBy = loginUser!!.id,
        )

        val result = postRepository.save(post)
        if (result.failed) {
            Log.e(TAG, "createPost failed: ${result.exception.message}", result.exception)
            _error.value = Event(result.exception)
        }

        if (_error.value == null) {
            title.value = ""
            body.value = ""
            _uploadSucceed.value = Event(true)
        }

        _isLoading.value = false
    }

}