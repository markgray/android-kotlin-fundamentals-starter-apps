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

package com.example.android.trackmysleepquality.sleepquality

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.databinding.FragmentSleepQualityBinding

/**
 * Fragment that displays a list of clickable icons,
 * each representing a sleep quality rating.
 * Once the user taps an icon, the quality is set in the current sleepNight
 * and the database is updated.
 */
class SleepQualityFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen. This function uses
     * [DataBindingUtil] to inflate R.layout.fragment_sleep_quality. It is also responsible for
     * passing the [SleepQualityViewModel] ViewModel to the [FragmentSleepQualityBinding] generated
     * by DataBinding. This will allow DataBinding to use the `LiveData` on our ViewModel.
     *
     * We initialize our [FragmentSleepQualityBinding] variable `val binding` by having the
     * [DataBindingUtil.inflate] method use our [LayoutInflater] parameter [inflater] to inflate
     * our layout file R.layout.fragment_sleep_quality, with our [ViewGroup] parameter [container]
     * providing the `LayoutParams` without attaching to it. We initialize our `Application`
     * variable `val application` to the application that owns our activity, and our
     * [SleepQualityFragmentArgs] variable `val arguments` with the safe arguments passed to our
     * fragment inside the arguments [Bundle] supplied when the fragment was instantiated. We
     * initialize our [SleepDatabaseDao] variable `val dataSource` to the singleton instance our
     * app can use to access our Room database, and then initialize [SleepQualityViewModelFactory]
     * variable `val viewModelFactory` to an instance constructed to use the `sleepNightKey` field
     * of our [SleepQualityFragmentArgs] `arguments` as the `nightId` PrimaryKey for the `SleepNight`
     * we are to have updated by our [SleepQualityViewModel] with the sleep quality value selected
     * by the user, and our [SleepDatabaseDao] `dataSource` as the Room handle to the database to use
     * to access that database. We then use the [ViewModelProvider.get] method to have the
     * `viewModelFactory` create (or return the existing) [SleepQualityViewModel] and save the
     * reference in our [SleepQualityViewModel] variable `val sleepQualityViewModel`. We initialize
     * the `sleepQualityViewModel` field of `binding` to `sleepQualityViewModel` (this allows our
     * layout file access to the data in the ViewModel through the `<variable>` element of its
     * `<data>` element). We add an [Observer] for the `navigateToSleepTracker` LiveData [Boolean]
     * in our `sleepQualityViewModel` using *this* as the `LifecycleOwner` and a lambda which, when
     * the field transitions to *true* (the user has clicked one of the icons in our UI which causes
     * the `onSetSleepQuality` method of our ViewModel to be called with the sleep value selected
     * which it updates in the database and then sets the flag to *true*) we locate our `NavController`
     * and have it navigate to the `SleepTrackerFragment`, then we call the `doneNavigating` method
     * of `sleepQualityViewModel` to have it set its `navigateToSleepTracker` field to *null*.
     *
     * Finally we return the outermost View in the layout file associated with `binding` to the
     * caller.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI will be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Suppress("RedundantNullableReturnType") // The method we override returns nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepQualityBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_quality, container, false
        )

        val application = requireNotNull(this.activity).application
        val arguments = SleepQualityFragmentArgs.fromBundle(requireArguments())

        // Create an instance of the ViewModel Factory.
        val dataSource: SleepDatabaseDao = SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepQualityViewModelFactory(arguments.sleepNightKey, dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        val sleepQualityViewModel =
            ViewModelProvider(
                this, viewModelFactory
            )[SleepQualityViewModel::class.java]

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.sleepQualityViewModel = sleepQualityViewModel

        // Add an Observer to the state variable for Navigating when a Quality icon is tapped.
        sleepQualityViewModel.navigateToSleepTracker.observe(viewLifecycleOwner) {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(
                    SleepQualityFragmentDirections.actionSleepQualityFragmentToSleepTrackerFragment()
                )
                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                sleepQualityViewModel.doneNavigating()
            }
        }

        return binding.root
    }
}
