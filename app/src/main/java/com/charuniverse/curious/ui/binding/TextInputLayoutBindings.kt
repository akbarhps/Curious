package com.charuniverse.curious.ui.binding

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("app:endIconClick")
fun textInputLayoutEndIconClick(view: TextInputLayout, operation: () -> Unit) {
    view.setEndIconOnClickListener { operation() }
}
