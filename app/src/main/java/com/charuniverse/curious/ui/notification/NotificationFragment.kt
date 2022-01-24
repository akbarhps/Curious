package com.charuniverse.curious.ui.notification

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentNotificationBinding
import com.charuniverse.curious.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : Fragment(R.layout.fragment_notification) {

    private lateinit var binding: FragmentNotificationBinding

    private val viewModel: NotificationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentNotificationBinding.bind(view).let {
            binding = it
            it.notifications.adapter = NotificationAdapter(viewModel)
            it.swipeLayout.setOnRefreshListener {
                viewModel.refresh(true)
            }
        }

        viewModel.notifications.observe(viewLifecycleOwner, {
            (binding.notifications.adapter as NotificationAdapter)
                .submitList(it)
        })

        setupEventObserver()
    }

    private fun setupEventObserver() {
        viewModel.viewState.observe(viewLifecycleOwner, EventObserver { state ->
            binding.swipeLayout.isRefreshing = state.isLoading

            state.error?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }

            state.selectedNotificationId?.let {
                val dest = NotificationFragmentDirections
                    .actionNotificationFragmentToPostDetailFragment(it)
                findNavController().navigate(dest)
            }
        })
    }
}