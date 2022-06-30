@file:Suppress("DEPRECATION", "RedundantNullableReturnType")

package com.example.android.gdgfinder.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.android.gdgfinder.R
import com.example.android.gdgfinder.databinding.AddGdgFragmentBinding
import com.google.android.material.snackbar.Snackbar

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


/**
 * This Fragment allow you to "apply" to run a new GDG chapter.
 */
class AddGdgFragment : Fragment() {

    /**
     * The [AddGdgViewModel] view model which holds the LiveData [Boolean] property `showSnackBarEvent`
     * that we use to control our display of a [Snackbar]. The "android:onClick" attribute of the
     * `button` property in our layout (resource ID [R.id.button]) is a binding expression lambda
     * which calls the `onSubmitApplication` method of our [AddGdgViewModel] which sets this property
     * to `true`. We observe `showSnackBarEvent` and when it toggles to `true` we show the [Snackbar],
     * then call the `doneShowingSnackbar` method of [AddGdgViewModel] to reset it to `false`.
     */
    private val viewModel: AddGdgViewModel by lazy {
        ViewModelProviders.of(this)[AddGdgViewModel::class.java]
    }

    /**
     * Called to have the fragment instantiate its user interface view. This will be called between
     * [onCreate] and [onActivityCreated]. We initialize our [AddGdgFragmentBinding] variable
     * `val binding` to the binding returned by the [AddGdgFragmentBinding.inflate] method when
     * it uses our [LayoutInflater] parameter [inflater] to inflate our layout file
     * layout/add_gdg_fragment.xml into an [AddGdgFragmentBinding] instance. We set the
     * `LifecycleOwner` of `binding` to `this`, and the `viewModel` variable of `binding` to our
     * [AddGdgViewModel] field [viewModel]. We set an observer on the `showSnackBarEvent` live data
     * [Boolean] of [viewModel] using our [Fragment]'s View lifecycle as the LifecycleOwner which
     * controls the observer. The lambda of the [Observer] will, if `showSnackBarEvent` has toggled
     * to `true`, show a [Snackbar], call the `doneShowingSnackbar` method of [viewModel] to reset
     * it to `false`, and set the `contentDescription` of the `button` property of `binding` (the
     * `Button` with ID [R.id.button]) to "Submitted, no need to submit again" and the `text` of
     * that `Button` to "Done".
     *
     * We then call `setHasOptionsMenu(true)` to report that this fragment would like to participate
     * in populating the options menu, and finally return the `root` property of `binding` (the
     * outermost [View] in the layout file associated with the Binding).
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate
     * any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the [View] for the fragment's UI, or null.
     */
    @Suppress("RedundantNullableReturnType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AddGdgFragmentBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.showSnackBarEvent.observe(viewLifecycleOwner) {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.application_submitted),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                viewModel.doneShowingSnackbar()
                binding.button.contentDescription = getString(R.string.submitted)
                binding.button.text = getString(R.string.done)
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

}
