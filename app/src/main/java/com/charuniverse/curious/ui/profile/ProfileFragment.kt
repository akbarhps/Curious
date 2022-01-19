package com.charuniverse.curious.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentProfileBinding
import com.charuniverse.curious.util.EventObserver
import com.charuniverse.curious.util.Preferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel: ProfileViewModel by viewModels()

    private val args: ProfileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return FragmentProfileBinding.inflate(inflater).let {
            binding = it
            it.lifecycleOwner = this
            return@let it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(args.userId == null)
        viewModel.setUserId(args.userId ?: Preferences.userId)

        binding.let {
            it.postsList.adapter = UserPostAdapter(viewModel)
            it.swipeLayout.setOnRefreshListener {
                viewModel.refreshUser(true, true)
            }
        }

        setupEventObserver()
    }

    private fun setupEventObserver() {
        viewModel.viewState.observe(viewLifecycleOwner, EventObserver { state ->
            binding.swipeLayout.isRefreshing = state.isLoading

            state.error?.let {
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
            }

            state.selectedPostId?.let {
                openPostDetailFragment(it)
            }

            state.user?.let {
                binding.user = it
            }

            state.userPosts?.let { posts ->
                (binding.postsList.adapter as UserPostAdapter).let {
                    it.submitList(posts)
                    // TODO: find better way to refresh data
                    it.notifyDataSetChanged()
                }
            }

            if (state.isLoggedOut) {
                val dest = ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
                findNavController().navigate(dest)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile_log_out -> viewModel.logOut(requireContext())
            R.id.profileEditFragment -> openProfileEditFragment()
        }
        return true
    }

    private fun openPostDetailFragment(postId: String) {
        val dest = ProfileFragmentDirections
            .actionProfileFragmentToPostDetailFragment(postId)
        findNavController().navigate(dest)
    }

    private fun openProfileEditFragment() {
        val dest = ProfileFragmentDirections
            .actionProfileFragmentToProfileEditFragment()
        findNavController().navigate(dest)
    }
}