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
import androidx.navigation.fragment.navArgs
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentProfileBinding
import com.charuniverse.curious.ui.MainActivity
import com.charuniverse.curious.util.EventObserver
import com.charuniverse.curious.util.Preferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel: ProfileViewModel by viewModels()

    private val args: ProfileFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.userId != null && args.userId != Preferences.userId) {
            setHasOptionsMenu(false)
        } else {
            setHasOptionsMenu(true)
        }

        viewModel.setUserId(args.userId ?: Preferences.userId)

        val adapter = UserPostAdapter(viewModel)
        binding = FragmentProfileBinding.bind(view).apply {
            postsList.adapter = adapter
        }

        viewModel.user.observe(viewLifecycleOwner, {
            it?.let { binding.user = it }
        })
        viewModel.userPosts.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        setupEventObserver()
    }

    private fun setupEventObserver() {
        viewModel.selectedPostId.observe(viewLifecycleOwner, EventObserver {
            val dest = ProfileFragmentDirections
                .actionProfileFragmentToPostDetailFragment(it)
            findNavController().navigate(dest)
        })

        viewModel.viewState.observe(viewLifecycleOwner, EventObserver { state ->
            binding.swipeLayout.isRefreshing = state.isLoading

            state.error?.let {
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
            }

            if (state.isCompleted) {
                val dest = ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
                findNavController().navigate(dest)
            }
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
            R.id.profileEditFragment -> {
                val dest = ProfileFragmentDirections
                    .actionProfileFragmentToProfileEditFragment()
                findNavController().navigate(dest)
                true
            }
            else -> false
        }
    }
}