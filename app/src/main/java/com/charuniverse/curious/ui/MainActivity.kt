package com.charuniverse.curious.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.charuniverse.curious.R
import com.charuniverse.curious.databinding.ActivityMainBinding
import com.charuniverse.curious.ui.dialog.Dialogs
import com.charuniverse.curious.ui.dialog.ProgressBarDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val bottomNavFragments = setOf(
        R.id.postFeedFragment,
//            R.id.postCreateFragment,
        R.id.profileFragment,
    )

    private val hideToolbarOnFragment = listOf(
        R.id.loginFragment,
    )

    private val hideBottomNavOnFragment = listOf(
        R.id.loginFragment,
        R.id.postCreateFragment,
        R.id.postCreatePreviewFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        delegate.applyDayNight()

        Dialogs.let {
            it.dialogProgressBar = ProgressBarDialog(this)
        }

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

        navController.addOnDestinationChangedListener { controller, destination, args ->
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