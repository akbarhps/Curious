package com.charuniverse.curious.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.charuniverse.curious.data.dto.NotificationDetail
import com.charuniverse.curious.databinding.ViewNotificationItemBinding
import com.charuniverse.curious.util.NotificationEvent

class NotificationAdapter(private val viewModel: NotificationViewModel) :
    ListAdapter<NotificationDetail, NotificationAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data, viewModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ViewNotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder = ViewHolder(
                ViewNotificationItemBinding.inflate(LayoutInflater.from(parent.context))
            )
        }

        fun bind(data: NotificationDetail, viewModel: NotificationViewModel) = binding.apply {
            this.viewModel = viewModel
            this.notification = data

            tvNotificationEventValue.text =
                "${NotificationEvent.getEventValue(data.eventType)} ${data.eventTitle}"

            tvNotificationEventBody.visibility =
                if (data.eventType != NotificationEvent.POST_COMMENT) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

            executePendingBindings()
        }
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<NotificationDetail>() {
    override fun areItemsTheSame(
        oldItem: NotificationDetail,
        newItem: NotificationDetail
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: NotificationDetail,
        newItem: NotificationDetail
    ): Boolean {
        return oldItem == newItem
    }
}
