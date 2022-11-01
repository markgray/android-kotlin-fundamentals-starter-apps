/*
 * Copyright 2019, The Android Open Source Project
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
 *
 */

package com.example.android.marsrealestate.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.marsrealestate.R
import com.example.android.marsrealestate.databinding.FragmentOverviewBinding
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.MarsProperty

/**
 * This fragment shows the the status of the Mars real-estate web services transaction.
 */
class OverviewFragment : Fragment() {

    /**
     * Lazily initialize our [OverviewViewModel].
     */
    private val viewModel: OverviewViewModel by lazy {
        ViewModelProvider(this)[OverviewViewModel::class.java]
    }

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onActivityCreated]. We initialize our [FragmentOverviewBinding] variable
     * `val binding` by having the [FragmentOverviewBinding.inflate] method use our [LayoutInflater]
     * parameter [inflater] to inflate the layout file it is associated with (the layout file
     * layout/fragment_overview.xml) into a [FragmentOverviewBinding] instance. We set the
     * LifecycleOwner that should be used for observing changes of LiveData in `binding` to `this`
     * [OverviewFragment], and set the `viewModel` variable property of `binding` to our
     * [OverviewViewModel] field [viewModel] (gives the binding expressions in `binding` access to
     * our [OverviewViewModel]). We then set the adapter of the `photosGrid` RecyclerView in
     * `binding` to a new instance of [PhotoGridAdapter] constructed to use an `OnClickListener`
     * whose lambda calls the `displayPropertyDetails` method of `viewModel` with the [MarsProperty]
     * that the `ViewHolder` of the view that was clicked was constructed to hold. We add an
     * [Observer] to the `navigateToSelectedProperty` property of [viewModel] which, if the
     * [MarsProperty] is not `null`, will find a `NavController` for our fragment and call its
     * `navigate` method to have it store the [MarsProperty] as a safe args for "selectedProperty"
     * in the argument bundle for the action `actionShowDetail` of [OverviewFragmentDirections]
     * (the action in the navigation graph with ID [R.id.action_showDetail]) and navigate to the
     * `DetailFragment`. After doing this the lambda will call the `displayPropertyDetailsComplete`
     * method of [viewModel] to tell the ViewModel we've made the navigate call to prevent multiple
     * navigation (sets the value of its `_navigateToSelectedProperty` property to `null`).
     *
     * Next we call [setHasOptionsMenu] with `true` to report that this fragment would like to
     * participate in populating the options menu, and finally we return the outermost [View] in the
     * layout file associated with `binding` to the caller.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the [View] for the fragment's UI, or null.
     */
    @Suppress("RedundantNullableReturnType")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentOverviewBinding.inflate(inflater)
        // val binding = GridViewItemBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        binding.photosGrid.adapter = PhotoGridAdapter(PhotoGridAdapter.OnClickListener {
            viewModel.displayPropertyDetails(it)
        })
        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner) {
            if (null != it) {
                this.findNavController().navigate(
                    OverviewFragmentDirections.actionShowDetail(it)
                )
                viewModel.displayPropertyDetailsComplete()
            }
        }
        @Suppress("DEPRECATION") // TODO: Replace setHasOptionsMenu with MenuHost
        setHasOptionsMenu(true)
        return binding.root
    }

    /**
     * Inflates the overflow menu that contains filtering options. We use our [MenuInflater] parameter
     * [inflater] to inflate our menu layout file R.menu.overflow_menu into our [Menu] parameter
     * [menu], then call our super's implementation of `onCreateOptionsMenu`.
     *
     * @param menu The options menu in which you place your items.
     * @param inflater a [MenuInflater] you can use to inflate XML menu files.
     */
    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION") // TODO: Replace onCreateOptionsMenu with MenuHost
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Updates the filter in the [OverviewViewModel] when the menu items are selected from the
     * overflow menu. This hook is called whenever an item in your options menu is selected.
     * We call the `updateFilter` method of [OverviewViewModel] field [viewModel] with a parameter
     * which depends on the `itemId` of the [item] that was selected:
     *  - for R.id.show_rent_menu we use the enum [MarsApiFilter.SHOW_RENT]
     *  - for R.id.show_rent_menu we use the enum [MarsApiFilter.SHOW_BUY]
     *  - for any other item we use the enum [MarsApiFilter.SHOW_ALL]
     *
     * Then we return `true` to consume the event here.
     *
     * @param item The menu item that was selected.
     * @return [Boolean] Return `false` to allow normal menu processing to
     * proceed, `true` to consume it here.
     */
    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION") // TODO: Replace onOptionsItemSelected with MenuHost
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateFilter(
            when (item.itemId) {
                R.id.show_rent_menu -> MarsApiFilter.SHOW_RENT
                R.id.show_buy_menu -> MarsApiFilter.SHOW_BUY
                else -> MarsApiFilter.SHOW_ALL
            }
        )
        return true
    }
}
