package com.charuniverse.curious.ui.post.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentPostFeedBinding
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
        val adapter = PostAdapter()
        binding.postsList.adapter = adapter

        viewModel.posts.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }
}