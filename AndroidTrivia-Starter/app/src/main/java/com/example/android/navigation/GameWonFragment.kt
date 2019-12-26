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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.android.navigation.databinding.FragmentGameWonBinding

/**
 * This is the [Fragment] which is navigated to from [GameFragment] when the user answers three
 * questions correctly. Its layout file - R.layout.fragment_game_won, informs the user that he won
 * and has a button whose `onClick` override navigates back to [GameFragment].
 */
class GameWonFragment : Fragment() {
    /**
     * Called to have the fragment instantiate its user interface view. We use the method
     * [DataBindingUtil.inflate] to use our [LayoutInflater] parameter [inflater] to inflate our
     * layout file R.layout.fragment_game_won using our [ViewGroup] parameter [container] for its
     * `LayoutParams` without attaching to it in order to initialize our variable `val binding`
     * to the [FragmentGameWonBinding] for our layout file. We then use `binding` to find the
     * `nextMatchButton` `Button` in our layout and set its `OnClickListener` to a lambda which
     * uses the [View] parameter `view` to call [View.findNavController] to fetch a handle to the
     * `NavController` associated with the [View], which it then uses to navigate to the
     * [GameFragment].
     *
     * Finally we return the `root` [View] of `binding` to the caller (this is outermost [View] in
     * the layout file associated with the Binding).
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
        /**
         * Inflate the layout for this fragment
         */
        val binding: FragmentGameWonBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_game_won, container, false)
        /**
         * Add OnClick Handler for Next Match button, it will navigate to
         * the [GameFragment] when clicked.
         */
        binding.nextMatchButton.setOnClickListener{view: View->
            view.findNavController()
                    .navigate(R.id.action_gameWonFragment_to_gameFragment)}
        val args = GameWonFragmentArgs.fromBundle(arguments!!)
        Toast.makeText(
                context,
                "NumCorrect: ${args.numCorrect}, NumQuestions: ${args.numQuestions}",
                Toast.LENGTH_LONG
        ).show()

        return binding.root
    }
}
