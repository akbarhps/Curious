package com.charuniverse.curious.util

import android.content.Context
import com.charuniverse.curious.ui.dialog.ProgressBarDialog

object Dialog {

    private lateinit var dialogProgressBar: ProgressBarDialog

    fun init(context: Context) {
        dialogProgressBar = ProgressBarDialog(context)
    }

    fun toggleProgressBar(isVisible: Boolean) {
        if (isVisible) {
            dialogProgressBar.show()
        } else {
            dialogProgressBar.dismiss()
        }
    }

}