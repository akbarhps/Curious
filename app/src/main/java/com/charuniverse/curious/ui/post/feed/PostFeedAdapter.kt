package com.charuniverse.curious.ui.post.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.charuniverse.curious.data.model.PostDetail
import com.charuniverse.curious.databinding.ViewPostFeedItemBinding
import com.charuniverse.curious.ui.post.PostViewModel

class PostAdapter(private val viewModel: PostViewModel) :
    ListAdapter<PostDetail, PostAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ViewPostFeedItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ViewPostFeedItemBinding.inflate(layoutInflater)
                return ViewHolder(binding)
            }
        }

        fun bind(viewModel: PostViewModel, item: PostDetail) {
            binding.post = item
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<PostDetail>() {
    override fun areItemsTheSame(oldItem: PostDetail, newItem: PostDetail): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PostDetail, newItem: PostDetail): Boolean {
        return oldItem == newItem
    }
}
