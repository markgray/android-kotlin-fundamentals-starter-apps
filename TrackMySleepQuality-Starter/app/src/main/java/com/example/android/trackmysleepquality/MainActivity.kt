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

package com.example.android.trackmysleepquality

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentContainerView

/**
 * This is the toy app for lesson 6 of the
 * Android App Development in Kotlin course on Udacity(https://www.udacity.com/course/???).
 *
 * The SleepQualityTracker app is a demo app that helps you collect information about your sleep.
 *  - Start time, end time, quality, and time slept
 *
 * This app demonstrates the following views and techniques:
 *  - Room database, DAO, and Coroutines
 *
 * It also uses and builds on the following techniques from previous lessons:
 *  - Transformation map
 *  - Data Binding in XML files
 *  - ViewModel Factory
 *  - Using Backing Properties to protect MutableLiveData
 *  - Observable state LiveData variables to trigger navigation
 */

/**
 * Our MainActivity is only responsible for setting the content view that contains the
 * Navigation Host.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge] to enable edge-to-edge
     * mode, then we call our super's implementation of `onCreate`, and set our content view to our
     * layout file `R.layout.activity_main`. It consists only of a `fragment` container for
     * android:name="androidx.navigation.fragment.NavHostFragment", with an app:defaultNavHost="true"
     * attribute (to ensure that our `NavHostFragment` intercepts the system Back button) and an
     * app:navGraph="@navigation/navigation" attribute (associates the `NavHostFragment` with the
     * navigation graph navigation/navigation.xml). The app:startDestination attribute of the
     * navigation graph specifies `R.id.sleep_tracker_fragment` to be the starting fragment
     * (`sleeptracker/SleepTrackerFragment.kt`)
     *
     * We initialize our [FragmentContainerView] variable `rootView` to the view with ID
     * `R.id.nav_host_fragment` then call [ViewCompat.setOnApplyWindowInsetsListener] to
     * take over the policy for applying window insets to `rootView`, with the listener
     * argument a lambda that accepts the [View] passed the lambda in variable `v` and the
     * [WindowInsetsCompat] passed the lambda in variable `windowInsets`. It initializes its
     * [Insets] variable `systemBars` to the [WindowInsetsCompat.getInsets] of `windowInsets`
     * with [WindowInsetsCompat.Type.systemBars] as the argument. It then gets the insets for
     * the IME (keyboard) using [WindowInsetsCompat.Type.ime]. It then updates the layout
     * parameters of `v` to be a [ViewGroup.MarginLayoutParams] with the left margin set to
     * `systemBars.left`, the right margin set to `systemBars.right`, the top margin set to
     * `systemBars.top`, and the bottom margin set to the maximum of the system bars bottom
     * inset and the IME bottom inset. Finally it returns [WindowInsetsCompat.CONSUMED] to
     * the caller (so that the window insets will not keep passing down to descendant views).
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val rootView = findViewById<FragmentContainerView>(R.id.nav_host_fragment)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v: View, windowInsets: WindowInsetsCompat ->
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
    }
}
