package com.charuniverse.curious.ui.post

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
class PostViewModel @Inject constructor(
    private val postDetailRepository: PostDetailRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "PostViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    private val _forceRefresh = MutableLiveData<Event<Boolean>>()
    val forceRefresh: LiveData<Event<Boolean>> = _forceRefresh

    private val _postList: LiveData<List<PostDetail>> = postDetailRepository
        .observeListData().distinctUntilChanged()
        .switchMap { return@switchMap handleResult(it) }
    val postList: LiveData<List<PostDetail>> = _postList

    private val _postId = MutableLiveData<Event<String>>()
    val postId: LiveData<Event<String>> = _postId

    fun setSelectedPostId(id: String) {
        _postId.value = Event(id)
    }

    init {
        refreshData()
    }

    fun forceRefresh() {
        _forceRefresh.value = Event(true)
    }

    fun refreshData() = viewModelScope.launch {
        _isLoading.value = true
        postDetailRepository.refreshDataList()
        _isLoading.value = false
    }

    private fun handleResult(result: Result<List<PostDetail>>): LiveData<List<PostDetail>> {
        if (result is Result.Error) {
            Log.e(TAG, "filterData: ${result.exception.message}", result.exception)
            _errorMessage.value = Event(result.exception.message.toString())
            return MutableLiveData(listOf())
        }

        val data = (result as Result.Success).data
        data.sortedByDescending { it.createdAt }
        return MutableLiveData(data)
    }

}