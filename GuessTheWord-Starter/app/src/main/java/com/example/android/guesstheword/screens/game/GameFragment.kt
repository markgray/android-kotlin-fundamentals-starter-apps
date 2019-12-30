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

package com.example.android.guesstheword.screens.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
     * `Fragment`. We then use [binding] to set the `OnClickListener` of `correctButton` to our
     * method [onCorrect], the `OnClickListener` of `skipButton` to our method [onSkip], and the
     * `OnClickListener` of `endGameButton` to our method [onEndGame]. We then call our method
     * [updateScoreText] to update the text in the `binding.scoreText` `TextView` of our UI to
     * the string value of the `score` field in our [GameViewModel] field [viewModel] and call
     * our method [updateWordText] to update the text in the `binding.wordText` `TextView` of our
     * UI to the value of the `word` field in our [GameViewModel] field [viewModel]. Finally we
     * return the root view of our [GameFragmentBinding] field [binding] to the caller.
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
        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)

        binding.correctButton.setOnClickListener { onCorrect() }
        binding.skipButton.setOnClickListener { onSkip() }
        binding.endGameButton.setOnClickListener { onEndGame() }
        updateScoreText()
        updateWordText()
        return binding.root
    }

    /** Methods for button click handlers **/

    /**
     * `OnClickListener` for the `skipButton` `Button` in our UI. We call the `onSkip` method of our
     * [GameViewModel] field [viewModel] to have it update its data (moves to next word and subtracts
     * one from the score), then we call our [updateWordText] method to update the `TextView` for
     * the word in our UI from the new word contained in our [viewModel], and our [updateScoreText]
     * method to update the `TextView` for the score in our UI from the score contained in our
     * [viewModel].
     */
    private fun onSkip() {
        viewModel.onSkip()
        updateWordText()
        updateScoreText()
    }

    /**
     * `OnClickListener` for the `correctButton` `Button` in our UI. We call the `onCorrect` method
     * of our [GameViewModel] field [viewModel] to have it update its data (moves to next word and
     * adds one to the score), then we call our [updateWordText] method to update the `TextView` for
     * the word in our UI from the new word contained in our [viewModel], and our [updateScoreText]
     * method to update the `TextView` for the score in our UI from the score contained in our
     * [viewModel].
     */
    private fun onCorrect() {
        viewModel.onCorrect()
        updateScoreText()
        updateWordText()
    }

    /**
     * `OnClickListener` for the `endGameButton` `Button` in our UI. We just call our [gameFinished]
     * method.
     */
    private fun onEndGame() {
        gameFinished()
    }

    /** Methods for updating the UI **/

    /**
     * Reads the `word` field of our [GameViewModel] field [viewModel] and sets the text of the
     * `wordText` `TextView` in our UI (found using our [GameFragmentBinding] field [binding]) to
     * that string.
     */
    private fun updateWordText() {
        binding.wordText.text = viewModel.word
    }

    /**
     * Reads the `score` field of our [GameViewModel] field [viewModel] and sets the text of the
     * `score` `TextView` in our UI (found using our [GameFragmentBinding] field [binding]) to
     * the string value of that score.
     */
    private fun updateScoreText() {
        binding.scoreText.text = viewModel.score.toString()
    }

    /**
     * Called when the game is finished. We toast the fact that the game has finished, then
     * initialize our [GameFragmentDirections.ActionGameToScore] variable `val action` to
     * the `NavDirections` for navigating to the `ScoreFragment`. We fetch the `score` field
     * from our [GameViewModel] field [viewModel] and set the `score` field of `action` to it.
     * We use the [NavHostFragment.findNavController] method to find the `NavController` for
     * our fragment and then use its `navigate` method to navigate using `action` as the
     * `NavDirections` for that navigation.
     */
    private fun gameFinished() {
        Toast.makeText(activity, "Game has just finished", Toast.LENGTH_SHORT).show()
        val action = GameFragmentDirections.actionGameToScore()
        action.score = viewModel.score
        NavHostFragment.findNavController(this).navigate(action)
    }
}