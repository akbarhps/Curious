package com.charuniverse.curious.ui.post.create

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.charuniverse.curious.databinding.ViewPostCreateHelperItemBinding
import com.charuniverse.curious.util.Markdown

class PostCreateHelperAdapter(private val viewModel: PostCreateViewModel) :
    ListAdapter<Markdown.Element, PostCreateHelperAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ViewPostCreateHelperItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ViewPostCreateHelperItemBinding.inflate(layoutInflater)
                return ViewHolder(binding)
            }
        }

        fun bind(viewModel: PostCreateViewModel, item: Markdown.Element) {
            binding.element = item
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<Markdown.Element>() {
    override fun areItemsTheSame(oldItem: Markdown.Element, newItem: Markdown.Element): Boolean {
        return oldItem.tag == newItem.tag
    }

    override fun areContentsTheSame(oldItem: Markdown.Element, newItem: Markdown.Element): Boolean {
        return oldItem == newItem
    }
}
