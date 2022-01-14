package com.charuniverse.curious.ui.post.create

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentPostCreateBinding
import com.charuniverse.curious.ui.dialog.Dialogs
import com.charuniverse.curious.util.EventObserver
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