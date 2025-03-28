package com.example.android.navigation


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.navigation.databinding.FragmentTitleBinding

/**
 * A simple [Fragment] subclass which displays the title page for the game that is given in the
 * layout file R.layout.fragment_title, which contains a button which when clicked will navigate
 * to the [GameFragment]. In addition we populate the options menu with menu items which will
 * navigate to [RulesFragment] or [AboutFragment] if clicked.
 */
class TitleFragment : Fragment() {
    /**
     * Called to have the fragment instantiate its user interface view. We use the method
     * [DataBindingUtil.inflate] to use our [LayoutInflater] parameter [inflater] to inflate our
     * layout file R.layout.fragment_title using our [ViewGroup] parameter [container] for its
     * `LayoutParams` without attaching to it in order to initialize our variable `val binding`
     * to the [FragmentTitleBinding] for our layout file. We then use `binding` to find the
     * `playButton` `Button` in our layout and set its `OnClickListener` to a lambda which
     * uses the [View] parameter `view` to call [View.findNavController] to fetch a handle to the
     * `NavController` associated with the [View], which it then uses to navigate to the
     * [GameFragment]. For the homework assignment we also add `OnClickListener`'s for the buttons
     * `rulesButton` and `aboutButton` which navigate to the [RulesFragment] and [AboutFragment]
     * respectively using a `ActionOnlyNavDirections`.
     *
     * We initialize our [MenuHost] variable `val menuHost` to to the `FragmentActivity` this fragment
     * is currently associated with, then call its [MenuHost.addMenuProvider] method to add our
     * [MenuProvider] field [menuProvider] to add the [MenuProvider] to the [MenuHost]. Finally we
     * return the `root` property of `binding` (the outermost [View] in the layout file associated
     * with the Binding) to the caller.
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
        val binding = DataBindingUtil.inflate<FragmentTitleBinding>(
            inflater,
            R.layout.fragment_title,
            container,
            false
        )
        /**
         * The complete onClickListener with Navigation to the [GameFragment]
         * using a `ActionOnlyNavDirections`
         */
        binding.playButton.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(TitleFragmentDirections.actionTitleFragmentToGameFragment())
        }
        /**
         * The complete onClickListener with Navigation to the [RulesFragment]
         * using a `ActionOnlyNavDirections`
         */
        binding.rulesButton.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(TitleFragmentDirections.actionTitleFragmentToRulesFragment())
        }
        /**
         * The complete onClickListener with Navigation to the [AboutFragment]
         * using a `ActionOnlyNavDirections`
         */
        binding.aboutButton.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(TitleFragmentDirections.actionTitleFragmentToAboutFragment())
        }
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(menuProvider, viewLifecycleOwner)

        Log.i("TitleFragment", "onCreateView called")

        return binding.root
    }

    /**
     * Our  [MenuProvider]
     */
    private val menuProvider: MenuProvider = object : MenuProvider {
        /**
         * Initialize the contents of the Fragment host's standard options menu. We use our
         * [MenuInflater] parameter [menuInflater] to inflate our menu layout file
         * `R.menu.options_menu` into our [Menu] parameter [menu].
         *
         * @param menu The options menu in which you place your items.
         * @param menuInflater a [MenuInflater] you can use to inflate an XML menu file with.
         */
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.options_menu, menu)
        }

        /**
         * This hook is called whenever an item in your options menu is selected. We return the result
         * of calling the [NavigationUI.onNavDestinationSelected] method with our [MenuItem] parameter
         * [menuItem] and the `NavController` associated with the root [View] of our [Fragment] (this
         * method will try to navigate to the fragment whose ID is the same as the item ID of
         * [menuItem] which in our case is `aboutFragment`). If it was able to navigate to the
         * destination associated with the given MenuItem it returns `true` which we return to our
         * caller, if it fails it returns `false` and we return the value returned by our super's
         * implementation of `onOptionsItemSelected`.
         *
         * @param menuItem The menu item that was selected.
         * @return [Boolean] Return `false` to allow normal menu processing to
         *         proceed, `true` to consume it here.
         */
        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return NavigationUI.onNavDestinationSelected(menuItem, requireView().findNavController())
        }
    }

    /**
     * Debugging override of `onAttach` which simply logs the fact that it was called
     *
     * @param context the [Context] this fragment is running in.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("TitleFragment", "onAttach called")
    }

    /**
     * Debugging override of `onCreate` which simply logs the fact that it was called
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     * this is the state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("TitleFragment", "onCreate called")
    }

    /**
     * Called when all saved state has been restored into the view hierarchy
     * of the fragment.  This can be used to do initialization based on saved
     * state that you are letting the view hierarchy track itself, such as
     * whether check box widgets are currently checked.  This is called
     * after [onViewCreated] and before [onStart].
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.i("TitleFragment", "onViewStateRestored called")
    }

    /**
     * Debugging override of `onStart` which simply logs the fact that it was called
     */
    override fun onStart() {
        super.onStart()
        Log.i("TitleFragment", "onStart called")
    }

    /**
     * Debugging override of `onResume` which simply logs the fact that it was called
     */
    override fun onResume() {
        super.onResume()
        Log.i("TitleFragment", "onResume called")
    }

    /**
     * Debugging override of `onPause` which simply logs the fact that it was called
     */
    override fun onPause() {
        super.onPause()
        Log.i("TitleFragment", "onPause called")
    }

    /**
     * Debugging override of `onStop` which simply logs the fact that it was called
     */
    override fun onStop() {
        super.onStop()
        Log.i("TitleFragment", "onStop called")
    }

    /**
     * Debugging override of `onDestroyView` which simply logs the fact that it was called
     */
    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("TitleFragment", "onDestroyView called")
    }

    /**
     * Debugging override of `onDetach` which simply logs the fact that it was called
     */
    override fun onDetach() {
        super.onDetach()
        Log.i("TitleFragment", "onDetach called")
    }
}
