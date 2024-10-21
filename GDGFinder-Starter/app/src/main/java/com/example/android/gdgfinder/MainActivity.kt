/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.example.android.gdgfinder

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android.gdgfinder.databinding.ActivityMainBinding

/**
 * This is the single activity of the GDG Finder app. It sets up the navigation elements of the app,
 * in particular the action bar, DrawerLayout and Toolbar navigate up hamburger menu or back button
 */
class MainActivity : AppCompatActivity() {

    /**
     * The [ActivityMainBinding] binding generated for our layout file [R.layout.activity_main].
     */
    lateinit var binding: ActivityMainBinding

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`.
     * Then we initialize our [ActivityMainBinding] field [binding] to the binding returned by the
     * [DataBindingUtil.setContentView] method when it inflates our [R.layout.activity_main] layout
     * file and sets it as our content view. We call our [setupNavigation] to have it set up our
     * action bar, and navigation drawer. Finally we call the [AppCompatDelegate.setDefaultNightMode]
     * method to set the default night mode to [AppCompatDelegate.MODE_NIGHT_YES] (Night mode which
     * uses always uses a dark mode, enabling night qualified resources regardless of the time.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view.
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
                topMargin = insets.top
            }

            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        setupNavigation()
    }

    /**
     * Called when the hamburger menu or back button is pressed on the Toolbar. We delegate this to
     * the [NavController] in our layout with ID [R.id.nav_host_fragment] having it open the
     * navigation drawer layout in our layout file with resource ID [R.id.drawer_layout] (the
     * `drawerLayout` property of [binding]).
     */
    override fun onSupportNavigateUp(): Boolean =
        navigateUp(findNavController(R.id.nav_host_fragment), binding.drawerLayout)

    /**
     * Setup Navigation for this Activity. First we initialize our [NavController] variable
     * `val navController` by finding the [NavController] with ID [R.id.nav_host_fragment].
     * We call the [setSupportActionBar] method to have set the Toolbar to act as the ActionBar
     * for this Activity window to the `toolbar` property of binding (`Toolbar` with resource ID
     * [R.id.toolbar] in our layout file). We call [setupActionBarWithNavController] to have it
     * set up the ActionBar for use with our [NavController] `navController`, and have it set the
     * DrawerLayout that should be toggled from the Navigation button to the `drawerLayout` property
     * of [binding] (aka the drawer layout in our layout file with resource ID [R.id.drawer_layout]).
     * We call the `setupWithNavController` of the `NavigationView` property of [binding] to set
     * itself up for use with our [NavController] `navController` (This will call the method
     * `android.view.MenuItem.onNavDestinationSelected` when a menu item is selected. The selected
     * item in the NavigationView will automatically be updated when the destination changes).
     * Finally we add a lambda as an `OnDestinationChangedListener` to our [NavController]
     * `navController` which initializes its `ActionBar` variable `val toolBar` by retrieving the
     * support action bar (returning having done nothing if it is `null`). It then branches on the
     * ID of its [NavDestination] parameter `destination` and if the ID is [R.id.home] (the ID of
     * the `HomeFragment` in our navigation graph) it disables the display of the `toolBar` title,
     * and set our logo image to VISIBLE, otherwise is enables the display of the `toolBar` title
     * and sets our logo image to GONE.
     */
    private fun setupNavigation() {
        // first find the nav controller
        val navController = findNavController(R.id.nav_host_fragment)

        setSupportActionBar(binding.toolbar)

        // then setup the action bar, tell it about the DrawerLayout
        setupActionBarWithNavController(navController, binding.drawerLayout)


        // finally setup the left drawer (called a NavigationView)
        binding.navigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            val toolBar = supportActionBar ?: return@addOnDestinationChangedListener
            when (destination.id) {
                R.id.home -> {
                    toolBar.setDisplayShowTitleEnabled(false)
                    binding.heroImage.visibility = View.VISIBLE
                }

                else -> {
                    toolBar.setDisplayShowTitleEnabled(true)
                    binding.heroImage.visibility = View.GONE
                }
            }
        }
    }
}
