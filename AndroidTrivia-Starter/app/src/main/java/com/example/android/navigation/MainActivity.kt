/*
 * Copyright 2018, The Android Open Source Project
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

package com.example.android.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.navigation.databinding.ActivityMainBinding

/**
 * This is the main launch activity, it just initializes the UI and the navigation for the game and
 * all the business logic is handled by the fragments which are swapped into the `myNavHostFragment`
 * fragment in our layout file R.layout.activity_main by the `NavController`
 */
class MainActivity : AppCompatActivity() {

    /**
     * The [DrawerLayout] in our R.layout.activity_main layout file with the ID "@+id/drawerLayout"
     * It is the container for our content `LinearLayout` as well as the `NavigationView` for the
     * pull out action drawer.
     */
    private lateinit var drawerLayout: DrawerLayout

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`.
     * We initialize our [ActivityMainBinding] variable `val binding` to the value returned when
     * we use the [DataBindingUtil.setContentView] to inflate our layout file R.layout.activity_main
     * and set it as our content view. We then use `binding` to locate the [DrawerLayout] in that
     * `View` with ID `drawerLayout` and initialize our [DrawerLayout] field [drawerLayout] to it.
     * We initialize our [NavController] variable `val navController` to the [NavController] associated
     * with the view with ID R.id.myNavHostFragment (this is the fragment widget in our layout file
     * which will contain the UI of the various fragments we navigate to). We then call the method
     * [NavigationUI.setupActionBarWithNavController] to set up our `ActionBar` for use with the
     * [NavController] given by `navController` and [drawerLayout] as the [DrawerLayout] that will
     * be toggled from the home button. Finally we call the [NavigationUI.setupWithNavController]
     * method to setup the `NavigationView` with ID `navView` (found using `binding`) for use with
     * the [NavController] `navController`.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     * saved state as given here. We do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
                this,
                R.layout.activity_main
        )
        drawerLayout = binding.drawerLayout
        val navController: NavController = this.findNavController(R.id.myNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
    }

    /**
     * This method is called whenever the user chooses to navigate Up within your application's
     * activity hierarchy from the action bar. We initialize our [NavController] variable
     * `val navController` to the [NavController] associated with the view with ID R.id.myNavHostFragment
     * (the fragment widget in our layout). Then we return the value returned by the method
     * [NavigationUI.navigateUp] when it delegates the Up button behavior to the [NavController]
     * `navController`.
     *
     * @return true if Up navigation completed successfully and this Activity was finished,
     *         false otherwise.
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

}
