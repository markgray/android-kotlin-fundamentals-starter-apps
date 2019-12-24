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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Displays the rules for the game, which are contained in our layout file R.layout.fragment_rules.
 * This fragment is navigated to either through the options menu of [TitleFragment] or through the
 * `DrawerLayout` which is accessible from all of the fragments.
 */
class RulesFragment : Fragment() {
    /**
     * Called to have the fragment instantiate its user interface view. We just use our [LayoutInflater]
     * parameter [inflater] to inflate our layout file R.layout.fragment_rules using our [ViewGroup]
     * parameter [container] for its `LayoutParams` without attaching to it and return the [View]
     * created to our caller.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the [View] for the fragment's UI, or null.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        /**
         * Inflate the layout for this fragment
         */
        return inflater.inflate(R.layout.fragment_rules, container, false)
    }
}
