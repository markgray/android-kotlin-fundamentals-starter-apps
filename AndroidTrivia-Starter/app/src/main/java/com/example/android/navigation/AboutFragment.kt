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

@file:Suppress("RedundantNullableReturnType", "RedundantNullableReturnType",
    "RedundantNullableReturnType", "RedundantNullableReturnType", "RedundantNullableReturnType"
)

package com.example.android.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.android.navigation.databinding.FragmentAboutBinding

/**
 * [Fragment] which displays the simple "About" screen describing the app contained in the resource
 * file R.layout.fragment_about. This fragment is navigated to through the options menu of
 * [TitleFragment] or through the `DrawerLayout` which is accessible from all of the fragments, or
 * by pressing the "Rules" button in the [TitleFragment].
 */
class AboutFragment : Fragment() {
    /**
     * Called to have the fragment instantiate its user interface view. We use the method
     * [DataBindingUtil.inflate] to use our [LayoutInflater] parameter [inflater] to inflate our
     * layout file R.layout.fragment_about using our [ViewGroup] parameter [container] for its
     * `LayoutParams` without attaching to it in order to initialize our variable `val binding`
     * to the [FragmentAboutBinding] for our layout file. We then use `binding` to find the
     * `playButton` `Button` in our layout and set its `OnClickListener` to a lambda which
     * uses the [View] parameter `view` to call [View.findNavController] to fetch a handle to the
     * `NavController` associated with the [View], which it then uses to navigate to the
     * [GameFragment] using an `ActionOnlyNavDirections` (a `NavDirections` without any arguments)
     * constructed by the method [AboutFragmentDirections.actionAboutFragmentToGameFragment].
     *
     * Finally we return the `root` [View] of `binding` to the caller (this is the outermost [View]
     * in the layout file associated with the Binding).
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate any views in the
     * fragment
     * @param container If non-null, this is the parent view that the fragment's UI will be attached
     * to.  The fragment should not add the view itself, but this can be used to generate the
     * `LayoutParams` of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     * saved state as given here.
     * @return Return the [View] for the fragment's UI, or null.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentAboutBinding>(
                inflater,
                R.layout.fragment_about,
                container,
                false
        )
        /**
         * The complete onClickListener with Navigation to the [GameFragment]
         */
        binding.playButton.setOnClickListener { view : View ->
            view.findNavController().navigate(AboutFragmentDirections.actionAboutFragmentToGameFragment())
        }
        /**
         * Return the inflated view.
         */
        return binding.root
    }
}
