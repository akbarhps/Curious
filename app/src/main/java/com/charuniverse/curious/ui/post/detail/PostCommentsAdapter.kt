package com.charuniverse.curious.ui.post.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.charuniverse.curious.data.entity.Comment
import com.charuniverse.curious.databinding.ViewPostCommentItemBinding

class PostCommentsAdapter(private val viewModel: PostDetailViewModel) :
    ListAdapter<Comment, PostCommentsAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ViewPostCommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ViewPostCommentItemBinding.inflate(layoutInflater)
                return ViewHolder(binding)
            }
        }

        fun bind(item: Comment) {
            binding.comment = item
//            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem == newItem
    }
}
