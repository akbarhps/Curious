package com.charuniverse.curious.ui.post.detail

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentPostDetailBinding
import com.charuniverse.curious.util.EventObserver
import com.charuniverse.curious.util.Preferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {

    private val viewModel: PostDetailViewModel by viewModels()

    private val args: PostDetailFragmentArgs by navArgs()

    private lateinit var binding: FragmentPostDetailBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setPostId(args.postId)
        binding = FragmentPostDetailBinding.bind(view).also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
            it.commentsList.adapter = PostCommentsAdapter(viewModel)

            it.fabOpenCreateComment.setOnClickListener {
                openCreateEditCommentFragment()
            }
        }

        setupEventObserver()
    }

    private fun setupEventObserver() {
        viewModel.viewState.observe(viewLifecycleOwner, EventObserver { state ->
            binding.scrollLayout.isRefreshing = state.isLoading

            state.error?.let {
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
            }

            state.post?.let { post ->
                setHasOptionsMenu(post.createdBy == Preferences.userId)
                binding.post = post
                (binding.commentsList.adapter as PostCommentsAdapter)
                    .submitList(post.comments.values.sortedBy { it.createdAt })
            }

            state.selectedUserId?.let {
                openUserProfileFragment(it)
            }

            state.selectedCommentId?.let {
                openCreateEditCommentFragment(it)
            }

            if (state.isFinished) {
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

    private fun openUserProfileFragment(userId: String) {
        val dest = PostDetailFragmentDirections.actionGlobalProfileFragment(userId)
        findNavController().navigate(dest)
    }

    private fun openCreateEditCommentFragment(commentId: String? = null) {
        val dest = PostDetailFragmentDirections
            .actionPostDetailFragmentToCommentCreateEditFragment(args.postId, commentId)
        findNavController().navigate(dest)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPost(false)
    }

}