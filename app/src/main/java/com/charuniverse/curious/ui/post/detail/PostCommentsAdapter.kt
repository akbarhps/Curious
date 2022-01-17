package com.charuniverse.curious.ui.post.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.charuniverse.curious.R
import com.charuniverse.curious.data.model.CommentDetail
import com.charuniverse.curious.databinding.ViewPostCommentItemBinding

class PostCommentsAdapter(private val viewModel: PostDetailViewModel) :
    ListAdapter<CommentDetail, PostCommentsAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel, item)
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

        fun bind(viewModel: PostDetailViewModel, comment: CommentDetail) = binding.apply {
            this.comment = comment

            ivOpenCommentMenu.setOnClickListener {
                val menu = PopupMenu(it.context, it)
                menu.menuInflater.inflate(R.menu.menu_post_detail, menu.menu)
                menu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.postCreateEditFragment -> {}
                        R.id.delete_post -> viewModel.deleteComment(comment.id)
                    }
                    true
                }
                menu.show()
            }

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
