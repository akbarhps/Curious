package com.charuniverse.curious.ui.post.create_edit

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentPostCreateEditBinding
import com.charuniverse.curious.ui.binding.handleMarkdown
import com.charuniverse.curious.ui.markdown.MarkdownTagAdapter
import com.charuniverse.curious.util.Dialog
import com.charuniverse.curious.util.EventObserver
import com.charuniverse.curious.util.Markdown
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostCreateEditFragment : Fragment(R.layout.fragment_post_create_edit),
    MarkdownTagAdapter.Events {

    private val viewModel: PostCreateEditViewModel by viewModels()

    private val args: PostCreateEditFragmentArgs by navArgs()

    private lateinit var binding: FragmentPostCreateEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        requireActivity().title = if (args.postId != null) "Edit Post" else "Create Post"
        return FragmentPostCreateEditBinding.inflate(inflater).let {
            binding = it
            it.viewModel = viewModel
            it.lifecycleOwner = this
            return@let it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setPostId(args.postId)

        MarkdownTagAdapter(this).also { adapter ->
            binding.rvMarkdownTags.adapter = adapter
            adapter.submitList(Markdown.elements)
        }

        binding.content.setOnFocusChangeListener { _, isFocus ->
            binding.rvMarkdownTags.visibility = if (isFocus) View.VISIBLE else View.GONE
        }

        setupEventObserver()
    }

    override fun onItemClicked(element: Markdown.Element) {
        binding.content.handleMarkdown(element)
    }

    private fun setupEventObserver() {
        viewModel.viewState.observe(viewLifecycleOwner, EventObserver { state ->
            Dialog.toggleProgressBar(state.isLoading)

            state.error?.let {
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()
            }

            state.titleError?.let { message ->
                binding.title.error = message
            }

            state.contentError?.let { message ->
                binding.content.error = message
            }

            state.post?.let {
                binding.title.setText(it.title)
                binding.content.setText(it.content)
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
                viewModel.savePost()
                true
            }
            R.id.markdownPreviewFragment -> {
                openMarkdownPreview()
                true
            }
            else -> false
        }
    }

    private fun openMarkdownPreview() {
        val dest = PostCreateEditFragmentDirections
            .actionPostCreateEditFragmentToMarkdownPreviewFragment(
                binding.title.text.toString(),
                binding.content.text.toString()
            )
        findNavController().navigate(dest)
    }
}