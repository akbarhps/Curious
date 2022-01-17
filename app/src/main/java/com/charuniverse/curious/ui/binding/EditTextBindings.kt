package com.charuniverse.curious.ui.binding

import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.charuniverse.curious.util.Markdown
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("app:handleMarkdownEvent")
fun handleMarkdownEvent(view: EditText, element: Markdown.Element?) {
    if (element == null) return
    val text = view.text.toString()
    val start = view.selectionStart
    val end = view.selectionEnd

    val textBeforeStart = text.substring(0, start)
    val textAfterEnd = text.substring(end)
    val textSelection = text.subSequence(start, end).toString()

    val newText = if (start == end) {
        "$textBeforeStart${element.prefix}${element.suffix}$textAfterEnd"
    } else {
        "$textBeforeStart${element.prefix}$textSelection${element.suffix}$textAfterEnd"
    }
    view.setText(newText)

    var newPosition = textBeforeStart.length + element.prefix.length
    if (start != end) {
        newPosition += element.suffix.length + textSelection.length
    }
    view.requestFocus()
    view.setSelection(newPosition)
}

@BindingAdapter("app:handleMarkdownEvent")
fun handleMarkdownEvent(view: TextInputEditText, element: Markdown.Element?) {
    if (element == null) return

    val text = view.text.toString()
    val start = view.selectionStart
    val end = view.selectionEnd

    val textBeforeStart = text.substring(0, start)
    val textAfterEnd = text.substring(end)
    val textSelection = text.subSequence(start, end).toString()

    val newText = if (start == end) {
        "$textBeforeStart${element.prefix}${element.suffix}$textAfterEnd"
    } else {
        "$textBeforeStart${element.prefix}$textSelection${element.suffix}$textAfterEnd"
    }
    view.setText(newText)

    var newPosition = textBeforeStart.length + element.prefix.length
    if (start != end) {
        newPosition += element.suffix.length + textSelection.length
    }
    view.setSelection(newPosition)
}
