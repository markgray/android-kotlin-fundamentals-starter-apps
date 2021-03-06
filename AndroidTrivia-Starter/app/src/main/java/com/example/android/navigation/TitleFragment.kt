package com.example.android.navigation


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
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
        binding.rulesButton.setOnClickListener {view: View ->
            view.findNavController()
                    .navigate(TitleFragmentDirections.actionTitleFragmentToRulesFragment())
        }
        /**
         * The complete onClickListener with Navigation to the [AboutFragment]
         * using a `ActionOnlyNavDirections`
         */
        binding.aboutButton.setOnClickListener {view: View ->
            view.findNavController()
                    .navigate(TitleFragmentDirections.actionTitleFragmentToAboutFragment())
        }
        setHasOptionsMenu(true)

        Log.i("TitleFragment", "onCreateView called")

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("TitleFragment", "onAttach called")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("TitleFragment", "onCreate called")
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i("TitleFragment", "onActivityCreated called")
    }
    override fun onStart() {
        super.onStart()
        Log.i("TitleFragment", "onStart called")
    }
    override fun onResume() {
        super.onResume()
        Log.i("TitleFragment", "onResume called")
    }
    override fun onPause() {
        super.onPause()
        Log.i("TitleFragment", "onPause called")
    }
    override fun onStop() {
        super.onStop()
        Log.i("TitleFragment", "onStop called")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("TitleFragment", "onDestroyView called")
    }
    override fun onDetach() {
        super.onDetach()
        Log.i("TitleFragment", "onDetach called")
    }

    /**
     * Initialize the contents of the Fragment host's standard options menu. First we call our
     * super's implementation of `onCreateOptionsMenu`, then we use our [MenuInflater] parameter
     * [inflater] to inflate our menu layout file R.menu.options_menu into our [Menu] parameter
     * [menu].
     *
     * @param menu The options menu in which you place your items.
     * @param inflater the [MenuInflater] you can use to inflate an xml [Menu] layout file.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    /**
     * This hook is called whenever an item in your options menu is selected. We call the method
     * [NavigationUI.onNavDestinationSelected] with our [MenuItem] parameter [item] and the
     * `NavController` associated with the root [View] of our layout. If it returns *true* to
     * indicate it was successful navigating to the fragment selected by [item] we return that
     * *true* to the caller, otherwise we return the value returned by our super's implementation
     * to the caller.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     *         proceed, true to consume it here.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, view!!.findNavController())
                || super.onOptionsItemSelected(item)
    }

}
