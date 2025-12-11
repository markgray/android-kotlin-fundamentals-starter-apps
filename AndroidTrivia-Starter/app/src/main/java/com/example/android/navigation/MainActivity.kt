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
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
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
     * Called when the activity is starting. First we call [enableEdgeToEdge] to enable edge to edge
     * display, then we call our super's implementation of `onCreate`. We initialize our
     * [ActivityMainBinding] variable `val binding` to the binding object that the
     * [DataBindingUtil.setContentView] method returns when it inflates our layout file
     * `R.layout.activity_main` into a view which it sets as content view (binding object is
     * associated with the inflated content view of course). We intitialize our [DrawerLayout] field
     * [drawerLayout] to the `drawerLayout` property of `binding` (resourse ID `R.id.drawerLayout`
     * in our layout file). We call the [ViewCompat.setOnApplyWindowInsetsListener] method to set an
     * [OnApplyWindowInsetsListener] to take over over the policy for applying window insets to
     * the `root` view of `binding`, with the `listener` argument a lambda that accepts the [View]
     * passed the lambda in variable `v` and the [WindowInsetsCompat] passed the lambda in variable
     * `windowInsets`. It initializes its [Insets] variable `systemBars` to the
     * [WindowInsetsCompat.getInsets] of `windowInsets` with [WindowInsetsCompat.Type.systemBars]
     * as the argument. It then gets the insets for the IME (keyboard) using
     * [WindowInsetsCompat.Type.ime]. It then updates the layout parameters of `v` to be a
     * [ViewGroup.MarginLayoutParams] with the left margin set to `systemBars.left`, the right
     * margin set to `systemBars.right`, the top margin set to `systemBars.top`, and the bottom
     * margin set to the maximum of the system bars bottom inset and the IME bottom inset.
     * Finally it returns [WindowInsetsCompat.CONSUMED] to the caller (so that the window
     * insets will not keep passing down to descendant views).
     *
     * We initialize our `NavController` variable `val navController` to the
     * `NavController` in our layout file (the [NavHostFragment] with ID
     * `R.id.myNavHostFragment` in our layout file). We then call the method
     * [NavigationUI.setupActionBarWithNavController] to have it set up the `SupportActionBar`
     * for use with the NavController `navController`, with [drawerLayout] the DrawerLayout
     * that should be toggled from the home button. Finally we call the method
     * [NavigationUI.setupWithNavController] to designate the `NavigationView` in our layout file
     * whose `binding` property is `navView` (resource ID `R.id.navView`) to be the view that
     * should be kept in sync with changes to the NavController `navController`.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v: View, windowInsets: WindowInsetsCompat ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime())

            // Apply the insets as a margin to the view.
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = systemBars.left
                rightMargin = systemBars.right
                topMargin = systemBars.top
                bottomMargin = systemBars.bottom.coerceAtLeast(ime.bottom)
            }
            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }

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
