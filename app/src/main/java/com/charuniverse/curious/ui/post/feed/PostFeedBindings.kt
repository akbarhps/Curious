package com.charuniverse.curious.ui.post.feed

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.charuniverse.curious.util.Constant.DAY_IN_SECONDS
import com.charuniverse.curious.util.Constant.HOUR_IN_SECONDS
import com.charuniverse.curious.util.Constant.MINUTE_IN_SECONDS
import com.charuniverse.curious.util.Constant.MONTH_IN_SECONDS
import com.charuniverse.curious.util.Constant.YEAR_IN_SECONDS

@BindingAdapter("app:onRefreshCallback")
fun onRefreshCallback(swipeRefreshLayout: SwipeRefreshLayout, callback: () -> Unit) {
    swipeRefreshLayout.setOnRefreshListener {
        callback()
    }
}

@BindingAdapter("app:timestamp")
fun formatTimestamp(textView: TextView, timestamp: Long) {
    val seconds = (System.currentTimeMillis() - timestamp) / 1000
    textView.text = when {
        (seconds < MINUTE_IN_SECONDS) -> "$seconds seconds ago"
        (seconds < HOUR_IN_SECONDS) -> "${seconds / MINUTE_IN_SECONDS} minutes ago"
        (seconds < DAY_IN_SECONDS) -> "${seconds / HOUR_IN_SECONDS} hours ago"
        (seconds < MONTH_IN_SECONDS) -> "${seconds / DAY_IN_SECONDS} days ago"
        (seconds < YEAR_IN_SECONDS) -> "${seconds / MONTH_IN_SECONDS} months ago"
        else -> "long time ago"
    }
}
