package com.charuniverse.curious.ui.post.create_edit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentPostCreateEditPreviewBinding

class PostCreateEditPreviewFragment : Fragment(R.layout.fragment_post_create_edit_preview) {

    private val viewModel: PostCreateEditViewModel by activityViewModels()

    private lateinit var binding: FragmentPostCreateEditPreviewBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPostCreateEditPreviewBinding.bind(view)
            .also { it.viewModel = viewModel }
    }
}