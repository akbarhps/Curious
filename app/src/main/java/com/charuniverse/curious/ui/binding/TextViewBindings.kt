package com.charuniverse.curious.ui.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.charuniverse.curious.util.Constant

@BindingAdapter("app:timestamp")
fun formatTimestamp(textView: TextView, timestamp: Long) {
    val seconds = (System.currentTimeMillis() - timestamp) / 1000
    textView.text = when {
        (seconds < Constant.MINUTE_IN_SECONDS) -> "$seconds seconds ago"
        (seconds < Constant.HOUR_IN_SECONDS) -> "${seconds / Constant.MINUTE_IN_SECONDS} minutes ago"
        (seconds < Constant.DAY_IN_SECONDS) -> "${seconds / Constant.HOUR_IN_SECONDS} hours ago"
        (seconds < Constant.MONTH_IN_SECONDS) -> "${seconds / Constant.DAY_IN_SECONDS} days ago"
        (seconds < Constant.YEAR_IN_SECONDS) -> "${seconds / Constant.MONTH_IN_SECONDS} months ago"
        else -> "${seconds / Constant.YEAR_IN_SECONDS} years ago"
    }
}
