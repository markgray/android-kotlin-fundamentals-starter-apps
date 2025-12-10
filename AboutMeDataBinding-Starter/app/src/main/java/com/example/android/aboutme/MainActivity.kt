/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.aboutme

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import com.example.android.aboutme.databinding.ActivityMainBinding

/**
 * Main Activity of the AboutMe app.
 * This codelab demonstrates how to add:
 *     * Data binding between MainActivity and activity_main.xml. How to remove findViewById,
 *       and how to display data in views using the data binding object.
 * This is the starter app.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val myName: MyName = MyName("Mark Gray")

    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge] to enable edge to edge
     * display, then we call our super's implementation of `onCreate`.
     *
     * We initialize our [ActivityMainBinding] field [binding] to the binding associated with our
     * content view which the [DataBindingUtil.setContentView] method creates when it inflates
     * our layout file `layout/activity_main.xml` into it. Then we use the
     * [ViewCompat.setOnApplyWindowInsetsListener] method to set an [OnApplyWindowInsetsListener] to
     * take over over the policy for applying window insets to the root view of [binding] , with the
     * `listener` argument a lambda that accepts the [View] passed the lambda in variable `v`
     * and the [WindowInsetsCompat] passed the lambda in variable `windowInsets`. It initializes
     * its [Insets] variable `systemBars` to the [WindowInsetsCompat.getInsets] of `windowInsets`
     * with [WindowInsetsCompat.Type.systemBars] as the argument. It then gets the insets for the
     * IME (keyboard) using [WindowInsetsCompat.Type.ime]. It then updates the layout parameters
     * of `v` to be a [ViewGroup.MarginLayoutParams] with the left margin set to `systemBars.left`,
     * the right margin set to `systemBars.right`, the top margin set to `systemBars.top`, and the
     * bottom margin set to the maximum of the system bars bottom inset and the IME bottom inset.
     * Finally it returns [WindowInsetsCompat.CONSUMED] to the caller (so that the window insets
     * will not keep passing down to descendant views).
     *
     * We then use [binding] to set the `myName` variable of our UI to our [MyName] field [myName],
     * and to set the `OnClickListener` of the `doneButton` in our UI to a lambda which calls our
     * [addNickname] method with the [View] that was clicked (the `doneButton` itself of course).
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
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

        binding.myName = myName
        binding.doneButton.setOnClickListener {
            addNickname(it)
        }
    }

    /**
     * Click handler for the Done button.
     */
    private fun addNickname(view: View) {

        binding.apply {
            myName?.nickname = nicknameEdit.text.toString()
            invalidateAll()
            nicknameEdit.visibility = View.GONE
            doneButton.visibility = View.GONE
            nicknameText.visibility = View.VISIBLE
        }

        // Hide the keyboard.
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
