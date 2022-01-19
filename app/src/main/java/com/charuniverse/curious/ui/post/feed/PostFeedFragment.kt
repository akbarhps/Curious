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
            it.lifecycleOwner = this
            it.viewModel = viewModel
            it.postsList.adapter = PostFeedAdapter(viewModel)
        }

        setupEventObserver()
    }

    private fun setupEventObserver() {
        viewModel.viewState.observe(viewLifecycleOwner, EventObserver { state ->
            binding.swipeLayout.isRefreshing = state.isLoading

            state.posts?.let { posts ->
                (binding.postsList.adapter as PostFeedAdapter).let {
                    it.submitList(posts)
                    // TODO: find another way to refresh the data
                    it.notifyDataSetChanged()
                }
            }

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

    override fun onResume() {
        super.onResume()
        viewModel.refreshPosts(false)
    }
}