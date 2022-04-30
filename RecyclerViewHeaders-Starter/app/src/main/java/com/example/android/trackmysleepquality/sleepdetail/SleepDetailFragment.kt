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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepDetailBinding


/**
 * A simple [Fragment] subclass.
 */
class SleepDetailFragment : Fragment() {

    /**
     * Called to have the fragment instantiate its user interface view. We initialize our
     * [FragmentSleepDetailBinding] variable `val binding` by having the [DataBindingUtil.inflate]
     * method inflate our layout file R.layout.fragment_sleep_detail using our [LayoutInflater]
     * parameter [inflater] and our [ViewGroup] parameter [container] without attaching to it.
     * We initialize our Application` variable `val application` to the application that owns this
     * activity, and our [SleepDetailFragmentArgs] variable `val arguments` to the arguments supplied
     * when our fragment was instantiated. We initialize our `SleepDatabaseDao` variable `val dataSource`
     * to a handle to our database's `SleepDatabaseDao`. We initialize our [SleepDetailViewModelFactory]
     * variable `val viewModelFactory` with a new instance constructed to use the `SleepNight`
     * whose `nightId` PrimaryKey is the `sleepNightKey` field of our variable `arguments` and whose
     * `SleepDatabaseDao` is our variable `dataSource`. We then initialize our [SleepDetailViewModel]
     * variable `val sleepDetailViewModel` to the `ViewModel` that our `viewModelFactory` variable
     * constructs.
     *
     * We then set the `sleepDetailViewModel` `variable` of our variable `binding` to this
     * `sleepDetailViewModel` and its `LifecycleOwner` to *this*. Next we add an Observer to the
     * [Boolean] `LiveData` state variable in our `sleepDetailViewModel` for Navigating when the
     * close button in our UI is clicked to a lambda which when the variable changes to
     * *true* will find the `NavController` of our Fragment and call its `navigate` method to
     * navigate back to the `SleepTrackerFragment` and then call the `doneNavigating` method of
     * `sleepDetailViewModel` to have it reset the `navigateToSleepTracker` to *null*.
     *
     * Finally we return the outermost View in the layout file associated with the Binding variable
     * `binding` to the caller.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's
     * UI will be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Suppress("RedundantNullableReturnType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepDetailBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_detail, container, false
        )

        val application = requireNotNull(this.activity).application
        val arguments = SleepDetailFragmentArgs.fromBundle(requireArguments())

        // Create an instance of the ViewModel Factory.
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepDetailViewModelFactory(arguments.sleepNightKey, dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        val sleepDetailViewModel =
            ViewModelProvider(this, viewModelFactory).get(SleepDetailViewModel::class.java)

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