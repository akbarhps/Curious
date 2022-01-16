package com.charuniverse.curious.ui.profile

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentProfileBinding
import com.charuniverse.curious.ui.dialog.Dialogs
import com.charuniverse.curious.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding = FragmentProfileBinding.bind(view)

        viewModel.viewState.observe(viewLifecycleOwner, {
            binding.viewState = it
        })
        viewModel.isLoading.observe(viewLifecycleOwner, {
            Dialogs.toggleProgressBar(it)
        })
        viewModel.errorMessage.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
        viewModel.isLoggedOut.observe(viewLifecycleOwner, EventObserver {
            val dest = ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
            findNavController().navigate(dest)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profile_log_out -> {
                viewModel.logOut(requireContext())
                true
            }
            R.id.profile_edit -> {
                // navigate to profile edit fragment
                true
            }
            else -> false
        }
    }
}