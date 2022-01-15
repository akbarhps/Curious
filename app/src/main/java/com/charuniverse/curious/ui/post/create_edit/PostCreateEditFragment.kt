package com.charuniverse.curious.ui.post.create_edit

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentPostCreateEditBinding
import com.charuniverse.curious.ui.dialog.Dialogs
import com.charuniverse.curious.ui.post.PostViewModel
import com.charuniverse.curious.util.EventObserver
import com.charuniverse.curious.util.Markdown
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostCreateEditFragment : Fragment(R.layout.fragment_post_create_edit) {

    private val postViewModel: PostViewModel by activityViewModels()
    private val viewModel: PostCreateEditViewModel by activityViewModels()
    private val args: PostCreateEditFragmentArgs by navArgs()

    private lateinit var binding: FragmentPostCreateEditBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.setPostId(args.postId)

        binding = FragmentPostCreateEditBinding.bind(view).also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
            it.helper.adapter = MarkdownTagAdapter(viewModel).also { md ->
                md.submitList(Markdown.elements)
            }
            it.content.setOnFocusChangeListener { _, isFocus ->
                it.helper.visibility = if (isFocus) View.VISIBLE else View.GONE
            }
        }

        viewModel.post.observe(viewLifecycleOwner, {
            if (it == null) return@observe
            binding.content.setText(it.content)
            binding.title.setText(it.title)
        })

        setupEventObserver()
    }

    private fun setupEventObserver() {
        viewModel.markdownElement.observe(viewLifecycleOwner, EventObserver {
            val content = binding.content
            val start = content.selectionStart
            val end = content.selectionEnd

            val currentText = content.text.toString()
            val textBeforeStart = currentText.substring(0, start)
            val textAfterEnd = currentText.substring(end)
            val currentSelection = currentText.subSequence(start, end).toString()

            val newText = if (start == end) {
                "$textBeforeStart${it.prefix}${it.suffix}$textAfterEnd"
            } else {
                "$textBeforeStart${it.prefix}$currentSelection${it.suffix}$textAfterEnd"
            }

            content.setText(newText)
            if (start == end) {
                content.setSelection(textBeforeStart.length + it.prefix.length)
            } else {
                content.setSelection(textBeforeStart.length + it.prefix.length + it.suffix.length + currentSelection.length)
            }
        })
        viewModel.isLoading.observe(viewLifecycleOwner, {
            Dialogs.toggleProgressBar(it)
        })
        viewModel.errorMessage.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })
        viewModel.isDone.observe(viewLifecycleOwner, EventObserver {
            postViewModel.forceRefresh()
            findNavController().navigateUp()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_post_create, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.upload_post) {
            viewModel.createOrUpdatePost()
            return true
        }

        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}