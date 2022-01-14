package com.charuniverse.curious.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.charuniverse.curious.databinding.DialogProgressBarBinding

class ProgressBarDialog(context: Context) : Dialog(context) {

    init {
        setCancelable(false)
    }

    override fun onStart() {
        super.onStart()
        window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DialogProgressBarBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
    }
}
