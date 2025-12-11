/*
 * Copyright (C) 2019 Google Inc.
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

package com.example.android.devbyteviewer.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.example.android.devbyteviewer.R

/**
 * This is a single activity application that uses the Navigation library. Content is displayed
 * by Fragments.
 */
class DevByteActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting. This is where most initialization should go. First we
     * call [enableEdgeToEdge] to enable the edge-to-edge display. Then we call our super's
     * implementation of `onCreate`, and set our content view to our layout file
     * `R.layout.activity_dev_byte_viewer`.
     *
     * We initialize our [FrameLayout] variable `rootView` to the view with ID `R.id.root_view`,
     * then we call the [ViewCompat.setOnApplyWindowInsetsListener] method to set a
     * [OnApplyWindowInsetsListener] to take over over the policy for applying window insets to
     * `rootView`, with the `listener`rgument a lambda that accepts the [View] passed the lambda
     * in variable `v` and the [WindowInsetsCompat] passed the lambda in variable `windowInsets`.
     * It initializes its [Insets] variable `systemBars` to the [WindowInsetsCompat.getInsets] of
     * `windowInsets` with [WindowInsetsCompat.Type.systemBars] as the argument. It then gets the
     * insets for the IME (keyboard) using [WindowInsetsCompat.Type.ime]. It then updates the
     * layout parameters of `v` to be a [ViewGroup.MarginLayoutParams] with the left margin set
     * to `systemBars.left`, the right margin set to `systemBars.right`, the top margin set to
     * `systemBars.top`, and the bottom margin set to the maximum of the system bars bottom inset
     * and the IME bottom inset. Finally it returns [WindowInsetsCompat.CONSUMED] to the caller
     * (so that the window insets will not keep passing down to descendant views).
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_byte_viewer)

        val rootView = findViewById<FrameLayout>(R.id.root_view)
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
