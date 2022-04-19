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

@file:Suppress("DEPRECATION")

package com.example.android.guesstheword.screens.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.ScoreFragmentBinding

/**
 * Fragment where the final score is shown, after the game is over
 */
class ScoreFragment : Fragment() {

    /**
     * The [ScoreViewModel] that holds the data and business logic for our fragment.
     */
    private lateinit var viewModel: ScoreViewModel
    /**
     * The [ScoreViewModelFactory] that we use to build our [ScoreViewModel] field
     * [viewModel]
     */
    private lateinit var viewModelFactory: ScoreViewModelFactory

    /**
     * Called to have the fragment instantiate its user interface view. We initialize our
     * [ScoreFragmentBinding] variable `binding` by having the [DataBindingUtil.inflate]
     * method inflate our layout file R.layout.score_fragment using our [LayoutInflater]
     * parameter [inflater] and our [ViewGroup] parameter [container] (for its
     * `LayoutParams`) without attaching to it. We initialize our [ScoreViewModelFactory]
     * field [viewModelFactory] to an instance constructed using the [Int] stored under
     * the key `score` in our arguments as its `finalScore` parameter. We then use the
     * [ViewModelProviders.of] method to initialize our [ScoreViewModel] field [viewModel]
     * to an [ScoreViewModel] for our fragment's scope using either one that already exists
     * or creating a new one. We set the `scoreViewModel` variable of `binding` to our
     * [ScoreViewModelFactory] field [viewModel] and set the `LifecycleOwner` that should
     * be used for observing changes of `LiveData` in `binding` to *this*. We add an [Observer]
     * to the `LiveData<Boolean>` field `eventPlayAgain` of [viewModel] whose `onChanged`
     * override is a lambda which will, if its `playAgain` parameter is *true*, use the
     * `NavController` returned by the [findNavController] method to navigate to the
     * `ActionOnlyNavDirections` action `actionRestart` which navigates back to the
     * `GameFragment`. Finally we return the root view of our [ScoreFragmentBinding] variable
     * `binding` to the caller.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the [View] for the fragment's UI, or null.
     */
    @Suppress("RedundantNullableReturnType")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        /**
         * Inflate view and obtain an instance of the binding class.
         */
        val binding: ScoreFragmentBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.score_fragment,
                container,
                false
        )

        viewModelFactory = ScoreViewModelFactory(
                ScoreFragmentArgs.fromBundle(requireArguments()).score
        )
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ScoreViewModel::class.java)

        binding.scoreViewModel = viewModel
        // Specify the current activity as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = this

        // Navigates back to game when button is pressed
        viewModel.eventPlayAgain.observe(viewLifecycleOwner) { playAgain ->
            if (playAgain) {
                findNavController().navigate(ScoreFragmentDirections.actionRestart())
                viewModel.onPlayAgainComplete()
            }
        }

        return binding.root
    }
}
