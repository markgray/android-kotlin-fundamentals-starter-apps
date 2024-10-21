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

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {
    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onActivityCreated]. We initialize our [FragmentSleepTrackerBinding] variable
     * `val binding` by having the [DataBindingUtil.inflate] method use our [LayoutInflater] parameter
     * [inflater] to inflate our layout file R.layout.fragment_sleep_tracker with our [ViewGroup]
     * parameter [container] supplying the LayoutParams. We initialize our [Application] variable
     * `val application` to the application that owns this activity. We use `application` in a call
     * to the [SleepDatabase.getInstance] to retrieve the singleton [SleepDatabase] and initialize
     * our [SleepDatabaseDao] variable `val dataSource` to the `sleepDatabaseDao` field of the
     * [SleepDatabase]. We then initialize our [SleepTrackerViewModelFactory] variable
     * `val viewModelFactory` to an instance constructed to use `dataSource` and `application`,
     * and then use `viewModelFactory` in a call to [ViewModelProvider] to retrieve the singleton
     * [SleepTrackerViewModel] (creating it if need be) and initialize our [SleepTrackerViewModel]
     * variable `val sleepTrackerViewModel` to it.
     *
     * We set the `LifecycleOwner` that should be used for observing changes of [LiveData] in `binding`
     * to `this`, and we set the `sleepTrackerViewModel` variable of `binding` to `sleepTrackerViewModel`
     * to allow the binding to use the view model.
     *
     * Finally we return the outermost [View] in the layout file associated with `binding` to the
     * caller.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    @Suppress("RedundantNullableReturnType") // The method we override returns nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_tracker, container, false)

        val application = requireNotNull<Activity>(this.activity).application

        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        // Create an instance of the ViewModel Factory.
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)

        // Get a reference to the ViewModel associated with this fragment.
        val sleepTrackerViewModel =
            ViewModelProvider(this, viewModelFactory)[SleepTrackerViewModel::class.java]
        binding.sleepTrackerViewModel = sleepTrackerViewModel

        binding.lifecycleOwner = this
        return binding.root
    }
}
