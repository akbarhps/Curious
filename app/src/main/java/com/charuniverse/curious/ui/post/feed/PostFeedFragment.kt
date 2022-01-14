package com.charuniverse.curious.ui.post.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostFeedBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PostAdapter(viewModel)
        binding.postsList.adapter = adapter

        viewModel.posts.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
        viewModel.openDetail.observe(viewLifecycleOwner, EventObserver {
            val dest = PostFeedFragmentDirections.actionPostFeedFragmentToPostDetailFragment()
            findNavController().navigate(dest)
        })
    }
}