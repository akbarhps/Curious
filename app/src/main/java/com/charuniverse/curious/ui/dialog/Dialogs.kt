package com.charuniverse.curious.ui.dialog

object Dialogs {

    lateinit var dialogProgressBar: ProgressBarDialog

    fun toggleProgressBar(isVisible: Boolean) {
        if (isVisible) {
            dialogProgressBar.show()
        } else {
            dialogProgressBar.dismiss()
        }
    }

}