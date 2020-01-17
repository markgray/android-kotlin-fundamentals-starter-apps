/*
 *  Copyright 2019, The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.android.marsrealestate.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.marsrealestate.databinding.FragmentDetailBinding

/**
 * This [Fragment] will show the detailed information about a selected piece of Mars real estate.
 */
class DetailFragment : Fragment() {
    /**
     * Called to have the fragment instantiate its user interface view. We initialize our
     * `Application` variable `val application` to the application that owns this activity, then
     * initialize our [FragmentDetailBinding] variable `val binding` by having the `inflate` method
     * of [FragmentDetailBinding] use our [LayoutInflater] parameter [inflater] to inflate the
     * the layout file fragment_detail.xml that [FragmentDetailBinding] is generated from, we then
     * set the `LifecycleOwner` of `binding` to *this* (sets the LifecycleOwner that should be used
     * for observing changes of LiveData in this binding). We initialize our `MarsProperty` variable
     * `val marsProperty` to the safe args value stored under the "selectedProperty" key in the
     * argument bundle that launched this [Fragment]. We initialize our [DetailViewModelFactory]
     * variable `val viewModelFactory` with an instance which will use `marsProperty` and
     * `application` for the [DetailViewModel] that it creates (or re-attaches to). We then set the
     * "viewModel" *variable* in the *data* element of fragment_detail.xml `binding` to the instance
     * of [DetailViewModel] that `viewModelFactory` creates or re-attaches to. Finally we return
     * the outermost [View] in the layout file associated with the Binding `binding`.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the [View] for the fragment's UI, or null.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val application = requireNotNull(activity).application
        val binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val marsProperty = DetailFragmentArgs.fromBundle(arguments!!).selectedProperty
        val viewModelFactory = DetailViewModelFactory(marsProperty, application)
        binding.viewModel = ViewModelProviders.of(
                this, viewModelFactory).get(DetailViewModel::class.java)
        return binding.root
    }
}