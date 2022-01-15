package com.charuniverse.curious.data.repository

import androidx.lifecycle.LiveData
import com.charuniverse.curious.data.Result
import com.charuniverse.curious.data.model.PostDetail
import com.charuniverse.curious.data.source.remote.PostDetailRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostDetailRepository(
    private val remoteDataSource: PostDetailRemoteDataSource,
    private val dispatcherContext: CoroutineDispatcher = Dispatchers.IO
) {

    fun observeListData(): LiveData<Result<List<PostDetail>>> {
        return remoteDataSource.observeDataList()
    }

    fun observeData(id: String): LiveData<Result<PostDetail>> {
        return remoteDataSource.observeData(id)
    }

    suspend fun refreshDataList() = withContext(dispatcherContext) {
        remoteDataSource.refreshData()
    }

    suspend fun delete(id: String): Result<Unit> = withContext(dispatcherContext) {
        return@withContext remoteDataSource.delete(id)
    }

}
