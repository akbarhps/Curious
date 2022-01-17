package com.charuniverse.curious.ui.profile.edit

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentProfileEditBinding
import com.charuniverse.curious.util.Dialog
import com.charuniverse.curious.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileEditFragment : Fragment() {

    private val viewModel: ProfileEditViewModel by viewModels()

    private lateinit var binding: FragmentProfileEditBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return FragmentProfileEditBinding.inflate(inflater).let {
            binding = it
            return@let it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.user.observe(viewLifecycleOwner, {
            if (it == null) return@observe
            binding.user = it
        })

        viewModel.viewState.observe(viewLifecycleOwner, EventObserver {
            Dialog.toggleProgressBar(it.isLoading)

            if (it.isCompleted) {
                findNavController().navigateUp()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile_edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save_edit) {
            viewModel.updateUser(
                username = binding.etUserUsername.text.toString(),
                displayName = binding.etUserDisplayName.text.toString(),
            )
        }
        return true
    }
}