package com.charuniverse.curious.ui.post.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentPostCreatePreviewBinding

class PostCreatePreviewFragment : Fragment(R.layout.fragment_post_create_preview) {

    private val viewModel: PostCreateViewModel by activityViewModels()

    private lateinit var binding: FragmentPostCreatePreviewBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPostCreatePreviewBinding.bind(view)
        binding.viewModel = viewModel
    }
}