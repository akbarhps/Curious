package com.charuniverse.curious.ui.comment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentCommentCreateEditBinding
import com.charuniverse.curious.ui.markdown.MarkdownTagAdapter
import com.charuniverse.curious.util.Dialog
import com.charuniverse.curious.util.EventObserver
import com.charuniverse.curious.util.Markdown
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentCreateEditFragment : Fragment(R.layout.fragment_comment_create_edit) {

    private lateinit var binding: FragmentCommentCreateEditBinding

    private val viewModel: CommentCreateEditViewModel by viewModels()

    private val args: CommentCreateEditFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return FragmentCommentCreateEditBinding.inflate(inflater).let {
            binding = it

            it.viewModel = viewModel
            it.postTitle = args.postTitle
            it.lifecycleOwner = this

            return@let it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.postId = args.postId

        MarkdownTagAdapter(viewModel).let {
            binding.rvMarkdownTags.adapter = it
            it.submitList(Markdown.elements)
        }

        viewModel.viewState.observe(viewLifecycleOwner, EventObserver { state ->
            Dialog.toggleProgressBar(state.isLoading)

            state.error?.let {
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
            }

            if (state.isCompleted) {
                findNavController().navigateUp()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_markdown_create_edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.send -> {
                viewModel.uploadComment()
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
        val destination = CommentCreateEditFragmentDirections
            .actionCommentCreateEditFragmentToMarkdownPreviewFragment(
                args.postTitle, binding.etComment.text.toString()
            )
        findNavController().navigate(destination)
    }
}