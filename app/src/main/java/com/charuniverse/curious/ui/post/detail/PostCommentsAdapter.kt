package com.charuniverse.curious.ui.post.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.charuniverse.curious.R
import com.charuniverse.curious.data.dto.CommentDetail
import com.charuniverse.curious.data.source.in_memory.InMemoryUserDataSource
import com.charuniverse.curious.databinding.ViewPostCommentItemBinding
import com.charuniverse.curious.util.Preferences

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
            this.viewModel = viewModel

            //TODO: Refactor
            val isViewerModerator: Boolean = InMemoryUserDataSource
                .getById(Preferences.userId)?.isModerator ?: false

            ivOpenCommentMenu.apply {
                visibility =
                    if (comment.createdBy == Preferences.userId || isViewerModerator) View.VISIBLE else View.GONE

                setOnClickListener {
                    val menu = PopupMenu(it.context, it)
                    menu.menuInflater.inflate(R.menu.menu_edit_delete, menu.menu)
                    menu.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit_item -> viewModel.setSelectedCommentId(comment.id)
                            R.id.delete_item -> viewModel.deleteComment(comment.id)
                        }
                        true
                    }
                    menu.show()
                }
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
