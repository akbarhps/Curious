package com.charuniverse.curious.ui.login

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.FragmentLoginBinding
import com.charuniverse.curious.ui.dialog.Dialogs
import com.charuniverse.curious.util.EventObserver
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    companion object {
        private const val TAG = "LoginFragment"
    }

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var binding: FragmentLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        binding.loginWithGoogle.setOnClickListener {
            signInLauncher.launch(viewModel.buildGoogleSignInClient(requireContext()).signInIntent)
        }

        setupObserver()
    }

    private fun setupObserver() {
        viewModel.loading.observe(viewLifecycleOwner, {
            Dialogs.toggleProgressBar(it)
        })
        viewModel.message.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })
        viewModel.loginSuccess.observe(viewLifecycleOwner, EventObserver {
            updateUI()
        })
    }

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.i(TAG, "login code: ${it.resultCode}")
            if (it.resultCode != Activity.RESULT_OK) {
                Log.i(TAG, "login failed, code: ${it.resultCode}")
                return@registerForActivityResult
            }

            val account = GoogleSignIn
                .getSignedInAccountFromIntent(it.data)
                .getResult(ApiException::class.java)!!

            viewModel.loginWithGoogle(account.idToken!!)
        }

    private fun updateUI() {
        val dest = LoginFragmentDirections.actionLoginFragmentToPostFeedFragment()
        findNavController().navigate(dest)
    }
}