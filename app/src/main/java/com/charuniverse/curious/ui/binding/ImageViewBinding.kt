package com.charuniverse.curious.ui.binding

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.charuniverse.curious.R
import java.net.URI

@BindingAdapter("app:load")
fun imageViewLoadUrl(imageView: ImageView, url: String?) {
    if (url == null) return
    Glide.with(imageView.context).load(url)
        .placeholder(R.drawable.ic_baseline_person_24)
        .into(imageView)
}

@BindingAdapter("app:loadCircular")
fun imageViewLoadUrlCircular(imageView: ImageView, url: String?) {
    if (url == null) return
    Glide.with(imageView.context).load(url)
        .circleCrop()
        .placeholder(R.drawable.ic_baseline_person_24)
        .into(imageView)
}

@BindingAdapter("app:load")
fun imageViewLoadUrl(imageView: ImageView, uri: Uri?) {
    if (uri == null) return
    imageView.setImageURI(uri)
}

@BindingAdapter("app:loadCircular")
fun imageViewLoadUrlCircular(imageView: ImageView, uri: Uri?) {
    if (uri == null) return
    Glide.with(imageView.context).load(uri)
        .circleCrop()
        .placeholder(R.drawable.ic_baseline_person_24)
        .into(imageView)
}

@BindingAdapter("app:load")
fun imageViewLoadUrl(imageView: ImageView, drawable: Int?) {
    if (drawable == null) return
    imageView.setImageResource(drawable)
}

@BindingAdapter("app:loadCircular")
fun imageViewLoadUrlCircular(imageView: ImageView, drawable: Int?) {
    if (drawable == null) return
    Glide.with(imageView.context).load(drawable)
        .circleCrop()
        .placeholder(R.drawable.ic_baseline_person_24)
        .into(imageView)
}
