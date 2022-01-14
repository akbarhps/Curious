package com.charuniverse.curious.ui.post.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PostCreateViewModel : ViewModel() {

    private val _viewState = MutableLiveData(PostCreateViewState())
    val viewState: LiveData<PostCreateViewState> = _viewState

    fun update(title: String, content: String) {
        _viewState.value = PostCreateViewState(title, content)
    }

}