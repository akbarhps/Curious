package com.charuniverse.curious.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.ActivityMainBinding
import com.charuniverse.curious.util.Dialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val bottomNavFragments = setOf(
        R.id.postFeedFragment,
        R.id.profileFragment,
    )

    private val hideToolbarOnFragment = setOf(
        R.id.loginFragment,
    )

    private val hideBottomNavOnFragment = setOf(
        R.id.loginFragment,
        R.id.postDetailFragment,
        R.id.postCreateEditFragment,
        R.id.commentCreateEditFragment,
        R.id.markdownPreviewFragment,
        R.id.profileEditFragment,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dialog.init(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController =
            (supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment)
                .findNavController()

        setupToolbar()
        setupBottomNavigation()
    }

    private fun setupToolbar() {
        val appBarConfiguration = AppBarConfiguration(bottomNavFragments)
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.apply {
            setupWithNavController(navController)
            setOnItemReselectedListener { }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.visibility =
                if (hideToolbarOnFragment.contains(destination.id)) View.GONE else View.VISIBLE

            binding.bottomNavigation.visibility =
                if (hideBottomNavOnFragment.contains(destination.id)) View.GONE else View.VISIBLE
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navController.handleDeepLink(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}