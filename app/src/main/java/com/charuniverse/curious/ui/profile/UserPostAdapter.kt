package com.charuniverse.curious.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.charuniverse.curious.data.model.PostDetail
import com.charuniverse.curious.databinding.ViewPostUserItemBinding
import com.charuniverse.curious.ui.post.feed.TaskDiffCallback

class UserPostAdapter(private val viewModel: ProfileViewModel) :
    ListAdapter<PostDetail, UserPostAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ViewPostUserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder = ViewHolder(
                ViewPostUserItemBinding.inflate(LayoutInflater.from(parent.context))
            )
        }

        fun bind(viewModel: ProfileViewModel, item: PostDetail) {
            binding.post = item
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }
}