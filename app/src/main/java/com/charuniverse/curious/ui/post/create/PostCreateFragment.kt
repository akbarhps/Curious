package com.charuniverse.curious.ui.post.create

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentPostCreateBinding
import com.charuniverse.curious.ui.dialog.Dialogs
import com.charuniverse.curious.util.EventObserver
import com.charuniverse.curious.util.Markdown
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostCreateFragment : Fragment(R.layout.fragment_post_create) {

    private val viewModel: PostCreateViewModel by activityViewModels()

    private lateinit var binding: FragmentPostCreateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        binding = FragmentPostCreateBinding.inflate(inflater, container, false)
            .also { it.viewModel = viewModel }

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.post_create_menu, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.helper.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

            adapter = PostCreateHelperAdapter(viewModel)
                .also { it.submitList(Markdown.elements) }
        }

        binding.body.setOnFocusChangeListener { _, isFocus ->
            binding.helper.visibility = if (isFocus) View.VISIBLE else View.GONE
        }

        val body = binding.body
        viewModel.helperEvent.observe(viewLifecycleOwner, EventObserver {
            val start = body.selectionStart
            val end = body.selectionEnd

            val currentText = body.text.toString()
            val textBeforeStart = currentText.substring(0, start)
            val textAfterEnd = currentText.substring(end)
            val currentSelection = currentText.subSequence(start, end).toString()

            val newText = if (start == end) {
                "$textBeforeStart${it.prefix}${it.suffix}$textAfterEnd"
            } else {
                "$textBeforeStart${it.prefix}$currentSelection${it.suffix}$textAfterEnd"
            }

            body.setText(newText)
            if (start == end) {
                body.setSelection(textBeforeStart.length + it.prefix.length)
            } else {
                body.setSelection(textBeforeStart.length + it.prefix.length + it.suffix.length + currentSelection.length)
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, {
            Dialogs.toggleProgressBar(it)
        })
        viewModel.error.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()
        })
        viewModel.uploadSucceed.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })
    }
}