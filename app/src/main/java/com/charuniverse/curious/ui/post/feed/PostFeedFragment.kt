package com.charuniverse.curious.ui.post.feed

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentPostFeedBinding
import com.charuniverse.curious.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFeedFragment : Fragment(R.layout.fragment_post_feed) {

    private val viewModel: PostFeedViewModel by activityViewModels()

    private lateinit var binding: FragmentPostFeedBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postAdapter = PostFeedAdapter(viewModel)

        binding = FragmentPostFeedBinding.bind(view).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
            it.postsList.adapter = postAdapter
        }

        viewModel.posts.observe(viewLifecycleOwner, {
            postAdapter.submitList(it)
        })

        setupEventObserver()
    }

    private fun setupEventObserver() {
        viewModel.selectedPostId.observe(viewLifecycleOwner, EventObserver {
            val dest = PostFeedFragmentDirections.actionPostFeedFragmentToPostDetailFragment(it)
            findNavController().navigate(dest)
        })
        viewModel.viewState.observe(viewLifecycleOwner, EventObserver { state ->
            binding.swipeLayout.isRefreshing = state.isLoading

            state.error?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}