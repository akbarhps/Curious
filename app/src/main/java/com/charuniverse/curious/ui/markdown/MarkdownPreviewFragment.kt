package com.charuniverse.curious.ui.markdown

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentMarkdownPreviewBinding

class MarkdownPreviewFragment : Fragment(R.layout.fragment_markdown_preview) {

    private val args: MarkdownPreviewFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FragmentMarkdownPreviewBinding.bind(view).let {
            it.content = args.content
            it.title = args.title
        }
    }
}