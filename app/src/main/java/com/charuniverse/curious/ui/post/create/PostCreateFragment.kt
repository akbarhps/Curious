package com.charuniverse.curious.ui.post.create

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentPostCreateBinding
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import java.util.concurrent.Executors

@AndroidEntryPoint
class PostCreateFragment : Fragment(R.layout.fragment_post_create) {

    private val viewModel: PostCreateViewModel by activityViewModels()

    private lateinit var binding: FragmentPostCreateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostCreateBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        setHasOptionsMenu(true)
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

        binding.content.addTextChangedListener(
            MarkwonEditorTextWatcher.withPreRender(
                MarkwonEditor.create(Markwon.create(requireContext())),
                Executors.newCachedThreadPool(),
                binding.content
            )
        )

        binding.materialButton.setOnClickListener {
            viewModel.createPost()
        }
    }
}