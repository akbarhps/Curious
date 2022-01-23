package com.charuniverse.curious.ui.post.feed

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentPostFeedBinding
import com.charuniverse.curious.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFeedFragment : Fragment(R.layout.fragment_post_feed) {

    private val viewModel: PostFeedViewModel by viewModels()

    private lateinit var binding: FragmentPostFeedBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPostFeedBinding.bind(view).also {
            it.viewModel = viewModel
            it.postList.adapter = PostFeedAdapter(viewModel)
        }

        binding.fabCreatePost.setOnClickListener {
            val dest = PostFeedFragmentDirections.actionPostFeedFragmentToPostCreateEditFragment()
            findNavController().navigate(dest)
        }

        // TODO: Refactor
        viewModel.posts.observe(viewLifecycleOwner, { posts ->
            (binding.postList.adapter as PostFeedAdapter).let {
                it.submitList(posts)
                // TODO: find another way to refresh the data
                it.notifyDataSetChanged()
            }
        })

        setupEventObserver()
    }

    private fun setupEventObserver() {
        viewModel.viewState.observe(viewLifecycleOwner, EventObserver { state ->
            binding.swipeLayout.isRefreshing = state.isLoading

            state.selectedPostId?.let {
                val dest = PostFeedFragmentDirections
                    .actionPostFeedFragmentToPostDetailFragment(it)
                findNavController().navigate(dest)
            }

            state.error?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}