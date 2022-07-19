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

@file:Suppress("RedundantNullableReturnType", "DEPRECATION")

package com.example.android.guesstheword.screens.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding

/**
 * Fragment where the game is played
 */
@Suppress("SpellCheckingInspection")
class GameFragment : Fragment() {

    /**
     * The [androidx.databinding.ViewDataBinding] to our UI layout file R.layout.game_fragment
     */
    private lateinit var binding: GameFragmentBinding

    /**
     * The [GameViewModel] which holds all our data
     */
    private lateinit var viewModel: GameViewModel

    /**
     * Called to have the fragment instantiate its user interface view. We initialize our
     * [GameFragmentBinding] field [binding] by having the [DataBindingUtil.inflate] method
     * inflate our layout file R.layout.game_fragment using our [LayoutInflater] parameter
     * [inflater] and our [ViewGroup] parameter [container] (for its `LayoutParams`) without
     * attaching to it. We then initialize our [GameViewModel] field [viewModel] with an existing
     * `ViewModel` (if we are being recreated) or a newly created one in the scope of *this*
     * `Fragment`. We set the `gameViewModel` variable in our [binding] to [viewModel], and set
     * the `LifecycleOwner` that should be used for observing changes of `LiveData` in [binding]
     * to *this*. We add an [Observer] to the `eventGameFinish` `LiveData<Boolean>` field of
     * [viewModel] with an `onChanged` override which is a lambda which will call our method
     * [gameFinished] if the `hasFinished` flag passed it is true. Finally we return the root
     * view of our [GameFragmentBinding] field [binding] to the caller.
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /**
         * Inflate view and obtain an instance of the binding class
         */
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.game_fragment,
            container,
            false
        )

        Log.i("GameFragment", "Called ViewModelProviders.of")
        viewModel = ViewModelProviders.of(this)[GameViewModel::class.java]

        /**
         * Set the viewmodel for databinding - this allows the
         * bound layout access to all the data in the ViewModel
         */
        binding.gameViewModel = viewModel
        /**
         * Specify the current activity as the lifecycle owner of the binding.
         * This is used so that the binding can observe LiveData updates
         */
        binding.lifecycleOwner = this

        /** Observer for the Game finished event **/
        viewModel.eventGameFinish.observe(viewLifecycleOwner) { hasFinished ->
            if (hasFinished) gameFinished()
        }

        return binding.root
    }

    /** Methods for updating the UI Now handled by the LiveData **/

    /**
     * Called when the game is finished. We toast the fact that the game has finished, then
     * initialize our [GameFragmentDirections.ActionGameToScore] variable `val action` to
     * the `NavDirections` for navigating to the `ScoreFragment`. We fetch the `score` value
     * of the `LiveData` field for the score from our [GameViewModel] field [viewModel] and
     * set the `score` field of `action` to it (defaulting to 0 is the `value` is *null*).
     * We use the [NavHostFragment.findNavController] method to find the `NavController`
     * for our fragment and then use its `navigate` method to navigate using `action` as
     * the `NavDirections` for that navigation. Finally we call the `onGameFinishComplete`
     * method of our [GameViewModel] field [viewModel] to have it set the value of the
     * `eventGameFinish` `LiveData` to *false*.
     */
    private fun gameFinished() {
        Toast.makeText(activity, "Game has just finished", Toast.LENGTH_SHORT).show()
        val action = GameFragmentDirections.actionGameToScore()
        action.score = viewModel.score.value ?: 0
        NavHostFragment.findNavController(this).navigate(action)
        viewModel.onGameFinishComplete()
    }
}