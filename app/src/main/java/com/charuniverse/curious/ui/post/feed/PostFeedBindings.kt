package com.charuniverse.curious.ui.post.feed

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("app:onRefreshCallback")
fun onRefreshCallback(swipeRefreshLayout: SwipeRefreshLayout, callback: () -> Unit) {
    swipeRefreshLayout.setOnRefreshListener {
        callback()
    }
}

@BindingAdapter("app:timestamp")
fun formatTimestamp(textView: TextView, timestamp: Long) {
    textView.text = SimpleDateFormat("dd/MM/yyy HH:mm", Locale.getDefault())
        .format(Date(timestamp))
}
