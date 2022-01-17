package com.charuniverse.curious.ui.post.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentPostDetailBinding
import com.charuniverse.curious.ui.post.PostViewModel
import com.charuniverse.curious.util.EventObserver
import com.charuniverse.curious.util.Preferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {

    private val postViewModel: PostViewModel by activityViewModels()
    private val viewModel: PostDetailViewModel by viewModels()
    private val args: PostDetailFragmentArgs by navArgs()

    private lateinit var binding: FragmentPostDetailBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setPostId(args.postId)
        val postCommentsAdapter = PostCommentsAdapter(viewModel)

        binding = FragmentPostDetailBinding.bind(view).also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
            it.commentsList.adapter = postCommentsAdapter
        }

        viewModel.post.observe(viewLifecycleOwner, {
            if (it == null) return@observe
            setHasOptionsMenu(it.createdBy == Preferences.userId)
            postCommentsAdapter.submitList(it.comments?.values?.toList())
        })

        setupEventObserver()
    }

    private fun setupEventObserver() {
        postViewModel.forceRefresh.observe(viewLifecycleOwner, EventObserver {
            postViewModel.refreshData()
        })

        viewModel.viewState.observe(viewLifecycleOwner, EventObserver {
            binding.scrollLayout.isRefreshing = it.isLoading

            if (it.postError != null) {
                Toast.makeText(requireContext(), it.postError, Toast.LENGTH_SHORT).show()
            }

            if (it.commentError != null) {
                Toast.makeText(requireContext(), it.commentError, Toast.LENGTH_SHORT).show()
            }

            if (it.uploadCommentSuccess) {
                binding.comment.setText("")
                binding.comment.clearFocus()
                Toast.makeText(requireContext(), "Posted!", Toast.LENGTH_SHORT).show()
            }

            if (it.deletePostSuccess) {
                postViewModel.refresh()
                findNavController().navigateUp()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_post_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.postCreateEditFragment -> {
                openEditFragment()
                true
            }
            R.id.delete_post -> {
                viewModel.deletePost()
                true
            }
            else -> false
        }
    }

    private fun openEditFragment() {
        val dest = PostDetailFragmentDirections
            .actionPostDetailFragmentToPostCreateEditFragment(args.postId)
        findNavController().navigate(dest)
    }

}