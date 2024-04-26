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
 */

package com.example.android.trackmysleepquality.sleepdetail

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.FragmentSleepDetailBinding

/**
 * This [Fragment] displays the details of the [SleepNight] which the user has clicked in the
 * `RecyclerView` of [SleepNight] icons displayed by `SleepTrackerFragment`. The `nightId` primary
 * key of the [SleepNight] of interest is passed to us in our `sleepNightKey` safe argument which
 * we then pass to our [SleepDetailViewModel] via its [SleepDetailViewModelFactory].
 */
class SleepDetailFragment : Fragment() {

    /**
     * Called to have the fragment instantiate its user interface view. First we use the
     * [DataBindingUtil.inflate] method to inflate our layout file R.layout.fragment_sleep_detail.
     * Then we initialize our [Application] variable `val application` to the application that owns
     * this activity, and our `SleepDetailFragmentArgs` variable `val arguments` to the instance
     * that the `SleepDetailFragmentArgs.fromBundle` method creates from the arguments supplied
     * when the fragment was instantiated (the safe args [Long] for the `sleepNightKey` argument
     * will be stored in the `sleepNightKey` property of `arguments`). We initialize our
     * [SleepDatabaseDao] variable `val dataSource` to the `sleepDatabaseDao` property of our
     * singleton [SleepDatabase] instance (creating the database if need be) (`dataSource` will
     * allow us to access the Room SQLite methods defined in the [SleepDatabaseDao]). We initialize
     * our [SleepDetailViewModelFactory] with an instance constructed to use the `sleepNightKey`
     * entry of `arguments` as the `nightId` of the `SleepNight` row, and `dataSource` as the
     * [SleepDatabaseDao]. We then use `viewModelFactory` as the Factory which will be used to
     * instantiate new ViewModels in a call to the [ViewModelProvider.get] method in order to
     * initialize our [SleepDetailViewModel] variable `val sleepDetailViewModel` to our
     * singleton [SleepDetailViewModel]. We set the `sleepDetailViewModel` variable of `binding`
     * to this `sleepDetailViewModel` (this will allow DataBinding binding expressions in the
     * associated layout file to use the [LiveData] of our ViewModel). We set the `lifecycleOwner`
     * of `binding` to `this` (Sets the LifecycleOwner that should be used for observing changes
     * of LiveData in the binding). We add an [Observer] to the `navigateToSleepTracker` [LiveData]
     * wrapped [Boolean] property of `sleepDetailViewModel` whose lambda will, if the property
     * transitions to `true`, find our `NavController` and use it to navigate to `SleepTrackerFragment`,
     * and then call the `doneNavigating` method of `sleepDetailViewModel` to reset
     * `navigateToSleepTracker` to `false` to make sure we only navigate once, even if the device
     * has a configuration change.
     *
     * Finally we return the outermost [View] in the layout file associated with `binding` to the
     * caller.
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
    @Suppress("RedundantNullableReturnType") // The method we override returns nullable
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepDetailBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_detail, container, false)

        val application = requireNotNull(this.activity).application
        val arguments = SleepDetailFragmentArgs.fromBundle(requireArguments())

        // Create an instance of the ViewModel Factory.
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepDetailViewModelFactory(arguments.sleepNightKey, dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        val sleepDetailViewModel =
            ViewModelProvider(this, viewModelFactory)[SleepDetailViewModel::class.java]

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.sleepDetailViewModel = sleepDetailViewModel

        binding.lifecycleOwner = this

        // Add an Observer to the state variable for Navigating when a Quality icon is tapped.
        sleepDetailViewModel.navigateToSleepTracker.observe(viewLifecycleOwner) {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(
                    SleepDetailFragmentDirections.actionSleepDetailFragmentToSleepTrackerFragment()
                )
                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                sleepDetailViewModel.doneNavigating()
            }
        }

        return binding.root
    }
}