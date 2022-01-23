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
        binding = FragmentNotificationBinding.bind(view)

        binding.notifications.adapter = NotificationAdapter(viewModel)

        binding.swipeLayout.setOnRefreshListener {
            viewModel.refresh()
        }

        // TODO: Refactor
        viewModel.notifications.observe(viewLifecycleOwner, {
            (binding.notifications.adapter as NotificationAdapter)
                .submitList(it)
        })

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