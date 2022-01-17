package com.charuniverse.curious.ui.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.entity.Comment
import com.charuniverse.curious.data.repository.PostRepository
import com.charuniverse.curious.ui.markdown.MarkdownTagAdapter
import com.charuniverse.curious.ui.post.BaseViewState
import com.charuniverse.curious.util.Event
import com.charuniverse.curious.util.Markdown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentCreateEditViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel(), MarkdownTagAdapter.Events {

    private val _viewState = MutableLiveData<Event<BaseViewState>>()
    val viewState: LiveData<Event<BaseViewState>> = _viewState

    private val _markdownElement = MutableLiveData<Markdown.Element>()
    val markdownElement: LiveData<Markdown.Element> = _markdownElement

    override fun onItemClicked(element: Markdown.Element) {
        _markdownElement.value = element
    }

    var postId = ""
    val inputComment = MutableLiveData("")

    fun uploadComment() = viewModelScope.launch {
        _viewState.value = Event(BaseViewState(isLoading = true))

        val comment = inputComment.value!!.trim().replace("\n", "  \n")
        val result = postRepository.addComment(Comment(postId = postId, content = comment))

        if (result is Result.Error) {
            _viewState.value = Event(BaseViewState(error = result.exception))
        } else {
            _viewState.value = Event(BaseViewState(isCompleted = true))
        }

        // refresh from different scope to avoid interrupt when fragment destroy
        CoroutineScope(Dispatchers.IO).launch {
            postRepository.refreshPosts()
        }
    }

}