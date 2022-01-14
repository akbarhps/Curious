package com.charuniverse.curious.ui.post.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentPostFeedBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PostFeedFragment : Fragment(R.layout.fragment_post_feed) {

    private val viewModel: PostFeedViewModel by viewModels()

    private lateinit var binding: FragmentPostFeedBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPostFeedBinding.bind(view)

        binding.btn.setOnClickListener {
            viewModel.logot(requireContext())
        }
    }
}