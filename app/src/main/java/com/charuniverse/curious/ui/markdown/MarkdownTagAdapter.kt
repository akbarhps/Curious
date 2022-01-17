package com.charuniverse.curious.ui.markdown

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.charuniverse.curious.databinding.ViewMarkdownTagItemBinding
import com.charuniverse.curious.util.Markdown

class MarkdownTagAdapter(private val events: Events) :
    ListAdapter<Markdown.Element, MarkdownTagAdapter.ViewHolder>(TaskDiffCallback()) {

    interface Events {
        fun onItemClicked(element: Markdown.Element)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(events, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ViewMarkdownTagItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder = ViewHolder(
                ViewMarkdownTagItemBinding.inflate(LayoutInflater.from(parent.context))
            )
        }

        fun bind(events: Events, item: Markdown.Element) = binding.apply {
            element = item
            this.events = events
            executePendingBindings()
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
