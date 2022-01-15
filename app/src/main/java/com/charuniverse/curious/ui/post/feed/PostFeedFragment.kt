package com.charuniverse.curious.ui.post.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentPostFeedBinding
import com.charuniverse.curious.ui.post.PostViewModel
import com.charuniverse.curious.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFeedFragment : Fragment(R.layout.fragment_post_feed) {

    private val viewModel: PostViewModel by activityViewModels()

    private lateinit var binding: FragmentPostFeedBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPostFeedBinding.bind(view).also {
            it.lifecycleOwner = this
            it.postViewModel = viewModel
            it.postsList.adapter = PostAdapter(viewModel)
        }

        viewModel.postList.observe(viewLifecycleOwner, {
            (binding.postsList.adapter as PostAdapter).submitList(it)
        })

        setupEventObserver()
    }

    private fun setupEventObserver() {
        viewModel.postId.observe(viewLifecycleOwner, EventObserver {
            val dest = PostFeedFragmentDirections.actionPostFeedFragmentToPostDetailFragment(it)
            findNavController().navigate(dest)
        })
        viewModel.forceRefresh.observe(viewLifecycleOwner, EventObserver {
            viewModel.refreshData()
        })
    }
}