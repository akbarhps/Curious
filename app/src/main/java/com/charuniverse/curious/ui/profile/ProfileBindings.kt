package com.charuniverse.curious.ui.profile

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.charuniverse.curious.R

@BindingAdapter("app:loadUrl")
fun loadUrl(imageView: ImageView, url: String?) {
    if (url == null) return

    Glide.with(imageView.context)
        .load(url)
        .circleCrop()
        .placeholder(R.drawable.ic_baseline_person_24)
        .into(imageView)
}