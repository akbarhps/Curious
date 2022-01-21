package com.charuniverse.curious.ui.comment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentCommentCreateEditBinding
import com.charuniverse.curious.ui.binding.handleMarkdown
import com.charuniverse.curious.ui.markdown.MarkdownTagAdapter
import com.charuniverse.curious.util.Dialog
import com.charuniverse.curious.util.EventObserver
import com.charuniverse.curious.util.Markdown
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentCreateEditFragment : Fragment(R.layout.fragment_comment_create_edit),
    MarkdownTagAdapter.Events {

    private lateinit var binding: FragmentCommentCreateEditBinding

    private val viewModel: CommentCreateEditViewModel by viewModels()

    private val args: CommentCreateEditFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return FragmentCommentCreateEditBinding.inflate(inflater).let {
            binding = it
            it.lifecycleOwner = this
            it.viewModel = viewModel
            return@let it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setPostId(args.postId, args.commentId)

        MarkdownTagAdapter(this).let {
            binding.rvMarkdownTags.adapter = it
            it.submitList(Markdown.elements)
        }

        setupEventObserver()
    }

    private fun setupEventObserver() {
        viewModel.viewState.observe(viewLifecycleOwner, EventObserver { state ->
            Dialog.toggleProgressBar(state.isLoading)

            state.error?.let {
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
            }

            state.postTitle?.let {
                binding.postTitle = it
            }

            state.content?.let {
                binding.etComment.setText(it)
            }

            state.contentError?.let {
                binding.etComment.error = it
            }

            if (state.isFinished) {
                Log.i("MainViewModel", "setupEventObserver: selesai dan close")
                findNavController().navigateUp()
            }
        })
    }

    override fun onItemClicked(element: Markdown.Element) {
        binding.etComment.handleMarkdown(element)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_markdown_create_edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.send -> {
                viewModel.uploadComment(binding.etComment.text.toString())
                true
            }
            R.id.markdownPreviewFragment -> {
                openMarkdownPreviewFragment()
                true
            }
            else -> false
        }
    }

    private fun openMarkdownPreviewFragment() {
        val dest = CommentCreateEditFragmentDirections
            .actionCommentCreateEditFragmentToMarkdownPreviewFragment(
                binding.tvPostTitle.text.toString(),
                binding.etComment.text.toString()
            )
        findNavController().navigate(dest)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshContent()
    }
}