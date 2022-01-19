package com.charuniverse.curious.ui.binding

import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.charuniverse.curious.util.Markdown
import com.google.android.material.textfield.TextInputEditText

fun EditText.handleMarkdown(element: Markdown.Element) {
    val text = this.text.toString()
    val start = this.selectionStart
    val end = this.selectionEnd

    val textBeforeStart = text.substring(0, start)
    val textAfterEnd = text.substring(end)
    val textSelection = text.subSequence(start, end).toString()

    val newText = if (start == end) {
        "$textBeforeStart${element.prefix}${element.suffix}$textAfterEnd"
    } else {
        "$textBeforeStart${element.prefix}$textSelection${element.suffix}$textAfterEnd"
    }
    this.setText(newText)

    var newPosition = textBeforeStart.length + element.prefix.length
    if (start != end) {
        newPosition += element.suffix.length + textSelection.length
    }
    this.requestFocus()
    this.setSelection(newPosition)
}
