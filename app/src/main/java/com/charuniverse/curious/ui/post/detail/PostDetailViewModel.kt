package com.charuniverse.curious.ui.post.detail

import android.util.Log
import androidx.lifecycle.*
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.model.PostDetail
import com.charuniverse.curious.data.repository.PostDetailRepository
import com.charuniverse.curious.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postDetailRepository: PostDetailRepository
) : ViewModel() {

    companion object {
        private const val TAG = "PostDetailViewModel"
    }

    private val _postId = MutableLiveData<String>()
    fun setPostId(id: String) {
        Log.i(TAG, "setPostId: di set")
        if (_isLoading.value == true || id.isBlank()) {
            return
        }

        Log.i(TAG, "setPostId: seharusnya lagi loading")
        _isLoading.value = true
        _postId.value = id
    }

    private val _post = _postId.switchMap { id ->
        postDetailRepository.observeData(id).map { handleResult(it) }
    }
    val post: LiveData<PostDetail?> = _post

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    private val _isDone = MutableLiveData<Event<Boolean>>()
    val isDone: LiveData<Event<Boolean>> = _isDone

    private fun handleResult(result: Result<PostDetail>): PostDetail? {
        if (result is Result.Error) {
            Log.e(TAG, "handleResult: ${result.exception.message}", result.exception)
            _errorMessage.value = Event(result.exception.message.toString())
            return null
        }

        Log.i(TAG, "handleResult: seharusnya loading selesai")
        _isLoading.value = false
        return (result as Result.Success).data
    }

    fun deletePost() = viewModelScope.launch {
        _isLoading.value = true

        val result = postDetailRepository.delete(_postId.value!!)
        if (result is Result.Error) {
            Log.e(TAG, "deletePost: ${result.exception.message}", result.exception)
            _errorMessage.value = Event(result.exception.message.toString())
        }

        _isLoading.value = false
        _isDone.value = Event(true)
    }
}