package com.charuniverse.curious.ui.post.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.charuniverse.curious.data.model.CommentDetail
import com.charuniverse.curious.databinding.ViewPostCommentItemBinding

class PostCommentsAdapter(private val viewModel: PostDetailViewModel) :
    ListAdapter<CommentDetail, PostCommentsAdapter.ViewHolder>(TaskDiffCallback()) {

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

        fun bind(item: CommentDetail) = binding.apply {
            comment = item
//            viewModel = viewModel
            executePendingBindings()
        }
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<CommentDetail>() {
    override fun areItemsTheSame(oldItem: CommentDetail, newItem: CommentDetail): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CommentDetail, newItem: CommentDetail): Boolean {
        return oldItem == newItem
    }
}
