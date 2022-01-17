package com.charuniverse.curious.ui.comment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentCommentCreateEditBinding
import com.charuniverse.curious.ui.adapter.MarkdownTagAdapter
import com.charuniverse.curious.util.Markdown

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

            return@let it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MarkdownTagAdapter(viewModel).let {
            binding.rvMarkdownTags.adapter = it
            it.submitList(Markdown.elements)
        }
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