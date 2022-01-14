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

    private val _viewState = MutableLiveData(PostCreateViewState())
    val viewState: LiveData<PostCreateViewState> = _viewState

    fun update(title: String, content: String) {
        _viewState.value = PostCreateViewState(title, content)
    }

    fun createPost() = viewModelScope.launch {
        val loginUser = authRepository.getLoginUser()
        val state = _viewState.value!!
        val post = Post(
            title = state.title,
            body = state.content,
            createdBy = loginUser!!.id,
        )

        val result = postRepository.save(post)
        if (result.failed) {
            Log.e(TAG, "createPost failed: ${result.exception.message}", result.exception)
        }
    }

}