package com.charuniverse.curious.ui.post.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.charuniverse.curious.R
import com.charuniverse.curious.data.source.in_memory.InMemoryUserDataSource
import com.charuniverse.curious.databinding.FragmentPostDetailBinding
import com.charuniverse.curious.util.EventObserver
import com.charuniverse.curious.util.Preferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {

    private val MENU_CREATE_COMMENT = 0
    private val MENU_EDIT_POST = 1
    private val MENU_DELETE_POST = 2

    private val viewModel: PostDetailViewModel by viewModels()

    private val args: PostDetailFragmentArgs by navArgs()

    private lateinit var binding: FragmentPostDetailBinding

    private var isViewerAuthor: Boolean? = null

    //TODO: Refactor
    private val isViewerModerator: Boolean = InMemoryUserDataSource
        .getById(Preferences.userId)?.isModerator ?: false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel.setPostId(args.id)
        binding = FragmentPostDetailBinding.bind(view).also {
            it.viewModel = viewModel
            it.commentList.adapter = PostCommentsAdapter(viewModel)
            it.ivOpenCommentFragment.setOnClickListener {
                openCreateEditCommentFragment()
            }
        }

        // TODO: Refactor
        viewModel.post.observe(viewLifecycleOwner, { post ->
            binding.post = post
            isViewerAuthor = post.createdBy == Preferences.userId

            (binding.commentList.adapter as PostCommentsAdapter).let { adapter ->
                adapter.submitList(post.comments.values.sortedBy { it.createdAt })
                // TODO: find better way to refresh
                adapter.notifyDataSetChanged()
            }
        })

        setupEventObserver()
    }

    private fun setupEventObserver() {
        viewModel.viewState.observe(viewLifecycleOwner, EventObserver { state ->
            binding.scrollLayout.isRefreshing = state.isLoading

            state.error?.let {
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        if (isViewerAuthor != null) {
            menu.add(0, MENU_CREATE_COMMENT, Menu.NONE, "Comment this post")
                .setIcon(R.drawable.ic_baseline_add_comment_24)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

            if (isViewerAuthor!! || isViewerModerator) {
                menu.add(0, MENU_EDIT_POST, Menu.NONE, "Edit Post")
                    .setIcon(R.drawable.ic_baseline_edit_24)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)

                menu.add(0, MENU_DELETE_POST, Menu.NONE, "Delete Post")
                    .setIcon(R.drawable.ic_baseline_delete_24)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
            }
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            MENU_CREATE_COMMENT -> {
                openCreateEditCommentFragment()
                true
            }
            MENU_EDIT_POST -> {
                openEditFragment()
                true
            }
            MENU_DELETE_POST -> {
                viewModel.deletePost()
                true
            }
            else -> false
        }
    }

    private fun openEditFragment() {
        val dest = PostDetailFragmentDirections
            .actionPostDetailFragmentToPostCreateEditFragment(args.id)
        findNavController().navigate(dest)
    }

    private fun openUserProfileFragment(userId: String) {
        val dest = PostDetailFragmentDirections.actionGlobalProfileFragment(userId)
        findNavController().navigate(dest)
    }

    private fun openCreateEditCommentFragment(commentId: String? = null) {
        val dest = PostDetailFragmentDirections
            .actionPostDetailFragmentToCommentCreateEditFragment(args.id, commentId)
        findNavController().navigate(dest)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPost(false)
    }
}