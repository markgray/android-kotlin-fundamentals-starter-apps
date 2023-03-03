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

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
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
     * [GameFragment] using a `ActionOnlyNavDirections`.
     *
     * We initialize our [MenuHost] variable `val menuHost` to to the `FragmentActivity` this
     * fragment is currently associated with, then call its [MenuHost.addMenuProvider] method to
     * add our [MenuProvider] field [menuProvider] to add the [MenuProvider] to the [MenuHost].
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
    @Suppress("RedundantNullableReturnType") // The method we override returns nullable
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
        binding.nextMatchButton.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(GameWonFragmentDirections.actionGameWonFragmentToGameFragment())
        }
        val args = GameWonFragmentArgs.fromBundle(requireArguments())
        Toast.makeText(
            context,
            "NumCorrect: ${args.numCorrect}, NumQuestions: ${args.numQuestions}",
            Toast.LENGTH_LONG
        ).show()

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(menuProvider, viewLifecycleOwner)
        return binding.root
    }

    /**
     * Our  [MenuProvider]
     */
    private val menuProvider: MenuProvider = object : MenuProvider {
        /**
         * Initialize the contents of the Fragment host's standard options menu. We use our
         * [MenuInflater] parameter [menuInflater] to inflate our menu layout file
         * [R.menu.winner_menu] into our [Menu] parameter [menu]. If the `resolveActivity` method
         * of the [Intent] created by our [getShareIntent] method is unable to find an activity
         * which can handle we set the visibility of the menu item in [menu] with the id [R.id.share]
         * to invisible.
         *
         * @param menu The options menu in which you place your items.
         * @param menuInflater a [MenuInflater] one can use to inflate an XML menu file.
         */
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.winner_menu, menu)
            // check if the activity resolves
            if (null == getShareIntent().resolveActivity(requireActivity().packageManager)) {
                // hide the menu item if it doesn't resolve
                menu.findItem(R.id.share)?.isVisible = false
            }
        }

        /**
         * This hook is called whenever an item in your options menu is selected. When the ID of our
         * [MenuItem] parameter [menuItem] is [R.id.share] we call our method [shareSuccess] (which
         * will launch another activity which will "share" our game results). In any case we then
         * return `true` to consume the event here.
         *
         * @param menuItem The menu item that was selected.
         * @return boolean Return `false` to allow normal menu processing to proceed, `true` to
         * consume it here.
         */
        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.share -> shareSuccess()
            }
            return true
        }
    }

    /**
     * Creating our Share Intent. We initialize our [GameWonFragmentArgs] variable `val args`
     * to the safe `NavArgs` class that the [GameWonFragmentArgs.fromBundle]  method creates
     * from the arguments supplied when the fragment was instantiated (it contains getters for
     * the `numCorrect` and `numQuestions` safe arguments). We initialize our [Intent] variable
     * `val shareIntent` with the action [Intent.ACTION_SEND] (Deliver some data to someone else).
     * We set the type of `shareIntent` to "text/plain" and add an [Intent.EXTRA_TEXT] extra which
     * consists of a formatted string displaying the `numCorrect` (number correctly answered) and
     * `numQuestions` (total number of questions asked) properties of `args`. Finally we return
     * `shareIntent` to the caller.
     *
     * @return an [Intent] whose action is [Intent.ACTION_SEND] and whose extra is a formatted
     * string reporting the results of playing the game.
     */
    private fun getShareIntent(): Intent {
        val args = GameWonFragmentArgs.fromBundle(requireArguments())
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
            .putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.share_success_text, args.numCorrect, args.numQuestions)
            )
        return shareIntent
    }

    /**
     * Starting an Activity with our new Intent. We just call the [startActivity] method with the
     * [Intent] created by our [getShareIntent] method. Called from our [onOptionsItemSelected]
     * override when the `itemId` of the [MenuItem] selected is [R.id.share].
     */
    private fun shareSuccess() {
        startActivity(getShareIntent())
    }
}
