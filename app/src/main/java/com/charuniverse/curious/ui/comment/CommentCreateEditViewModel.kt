package com.charuniverse.curious.ui.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.curious.data.repository.PostRepository
import com.charuniverse.curious.ui.adapter.MarkdownTagAdapter
import com.charuniverse.curious.util.Markdown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentCreateEditViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel(), MarkdownTagAdapter.Events {

    private val _markdownElement = MutableLiveData<Markdown.Element>()
    val markdownElement: LiveData<Markdown.Element> = _markdownElement

    override fun onItemClicked(element: Markdown.Element) {
        _markdownElement.value = element
    }

    val inputComment = MutableLiveData("")

    fun uploadComment() = viewModelScope.launch {

    }

}