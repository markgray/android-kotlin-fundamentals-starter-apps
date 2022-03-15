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

package com.example.android.gdgfinder.search

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.gdgfinder.R
import com.example.android.gdgfinder.databinding.FragmentGdgListBinding
import com.example.android.gdgfinder.network.GdgChapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar

/**
 * The permission request code we use when we call `requestPermissions` to ask user for location
 * permission.
 */
private const val LOCATION_PERMISSION_REQUEST = 1

/**
 * [String] used to request the "ACCESS_FINE_LOCATION" permission.
 */
private const val LOCATION_PERMISSION = "android.permission.ACCESS_FINE_LOCATION"

/**
 * This [Fragment] displays the list of `GdgChapter` objects downloaded from the network in a
 * `RecycleView`. The list is sorted by distance from the devices current location if that location
 * is available.
 */
class GdgListFragment : Fragment() {

    /**
     * Our handle to our fragment's singleton [GdgListViewModel] view model.
     */
    private val viewModel: GdgListViewModel by lazy {
        ViewModelProvider(this)[GdgListViewModel::class.java]
    }

    /**
     * Called to have the fragment instantiate its user interface view. We initialize our
     * [FragmentGdgListBinding] variable `val binding` to the binding object that the method
     * [FragmentGdgListBinding.inflate] returns when it uses our [LayoutInflater] parameter
     * [inflater] to inflate our layout file layout/fragment_gdg_list.xml into an instance of
     * [FragmentGdgListBinding]. We set the `LifecycleOwner` of `binding` to `this` and set the
     * `viewModel` variable of `binding` to our [GdgListViewModel] field [viewModel]. We set our
     * [GdgListAdapter] variable `val adapter` to an instance constructed to use a [GdgClickListener]
     * whose lambda argument uses the `GdgChapter` parameter passed it in `chapter` to create an
     * [Uri] for the `website` UriString field of `chapter` to initialize variable `val destination`,
     * and starts an activity with an [Intent] whose activity is [Intent.ACTION_VIEW] and whose
     * Intent data URI is `destination`.
     *
     * Having constructed `adapter` we set the adapter of the `RecyclerView` in our layout file
     * whose resource ID is R.id.gdg_chapter_list (aka the `gdgChapterList` property of `binding`)
     * to `adapter`.
     *
     * We set an [Observer] on the `showNeedLocation` [LiveData] wrapped [Boolean] property of
     * [viewModel] whose lambda shows a [Snackbar] when that property transitions to `true` (the
     * [Snackbar] asks the user to enable location in the settings app). We set an [Observer] on
     * the `regionList` [LiveData] wrapped list of [String] property of [viewModel] whose lambda
     * overrides the `onChanged` method with a method which returns having done nothing is its
     * [List] of [String] parameter `data` is `null`. Otherwise it constructs [Chip] widgets for
     * every [String] in the `data` [List] whose text and tag are both that string, sets the
     * chip's `OnCheckedChangeListener` to a lambda which calls the `onFilterChanged` method
     * of [viewModel] with the tag and the `isChecked` status of the chip. When done creating
     * the chips the `onChanged` override removes all views from the `regionList` `ChipGroup`
     * property of `binding` and then loops adding each of the [Chip] widgets we just created
     * to the `regionList` `ChipGroup`.
     *
     * The last things that our [onCreateView] override has to do is to call [setHasOptionsMenu]
     * with `true` to report that this fragment would like to participate in populating the options
     * menu, and then to return the outermost [View] in the layout file associated with `binding`
     * to the caller.
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate layout XML files.
     * @param container If non-null, this is the parent view that the fragment's
     * UI will be attached to. The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the [View] for the fragment's UI.
     */
    @Suppress("RedundantNullableReturnType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentGdgListBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        val adapter = GdgListAdapter(GdgClickListener { chapter ->
            val destination = Uri.parse(chapter.website)
            startActivity(Intent(Intent.ACTION_VIEW, destination))
        })

        // Sets the adapter of the RecyclerView
        binding.gdgChapterList.adapter = adapter

        @Suppress("RedundantSamConstructor", "RemoveExplicitTypeArguments")
        viewModel.showNeedLocation.observe(viewLifecycleOwner,
            Observer<Boolean> { show -> // Snackbar is like Toast but it lets us show forever
                if (show == true) {
                    Snackbar.make(
                        binding.root,
                        "No location. Enable location in settings (hint: test with Maps) then check app permissions!",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })

        viewModel.regionList.observe(viewLifecycleOwner, object : Observer<List<String>> {
            override fun onChanged(data: List<String>?) {
                data ?: return
                val chipGroup = binding.regionList
                val inflator = LayoutInflater.from(chipGroup.context)
                val children = data.map { regionName ->
                    val chip = inflator.inflate(R.layout.region, chipGroup, false) as Chip
                    chip.text = regionName
                    chip.tag = regionName
                    chip.setOnCheckedChangeListener { button, isChecked ->
                        viewModel.onFilterChanged(button.tag as String, isChecked)
                    }
                    chip
                }
                chipGroup.removeAllViews()

                for (chip in children) {
                    chipGroup.addView(chip)
                }
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    /**
     * Called to do initial creation of a fragment. This is called after [onAttach] and before
     * [onCreateView]. Note that this can be called while the fragment's activity is still in the
     * process of being created. As such, you can not rely on things like the activity's content
     * view hierarchy being initialized at this point.  If you want to do work once the activity
     * itself is created, see [onActivityCreated]. First we call our super's implementation of
     * `onCreate`, then we call our [requestLastLocationOrStartLocationUpdates] method to request
     * the last location of this device, if known, otherwise to start location updates.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     * this is the state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestLastLocationOrStartLocationUpdates()
    }

    /**
     * Show the user a dialog asking for permission to use location. Just calls the method
     * [requestPermissions] with our [LOCATION_PERMISSION] permission string and our request
     * code [LOCATION_PERMISSION_REQUEST] ([LOCATION_PERMISSION_REQUEST] will be returned to
     * our [onRequestPermissionsResult] override so we can verify it was called for this
     * permission request).
     */
    private fun requestLocationPermission() {
        requestPermissions(arrayOf(LOCATION_PERMISSION), LOCATION_PERMISSION_REQUEST)
    }

    /**
     * Request the last location of this device if known, otherwise start location updates. The
     * last location is cached from the last application to request location. First we call the
     * [ContextCompat.checkSelfPermission] method to see if we already have permission to get
     * the devices location, and if not we call our [requestLocationPermission] method to show
     * the user a dialog asking for permission to use location when just return. If we do have
     * permission we initialize our [FusedLocationProviderClient] variable `val fusedLocationClient`
     * with a new instance of [FusedLocationProviderClient] for use in the Context this fragment
     * is currently associated with. We fetch the `lastLocation` property of `fusedLocationClient`
     * and add an `OnSuccessListener` to the `Task` of [Location] it returns. The lambda of this
     * `OnSuccessListener` checks if the [Location] is `null` and if so calls our [startLocationUpdates]
     * method with `fusedLocationClient` as the argument to start location updates, otherwise it calls
     * the `onLocationUpdated` method of our [GdgListViewModel] field [viewModel] with the [Location]
     * to have it update its sorted list of [GdgChapter] objects for the new [Location].
     */
    private fun requestLastLocationOrStartLocationUpdates() {
        // if we don't have permission ask for it and wait until the user grants it
        if (ContextCompat.checkSelfPermission(requireContext(), LOCATION_PERMISSION)
            != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
            return
        }

        val fusedLocationClient: FusedLocationProviderClient
                = LocationServices.getFusedLocationProviderClient(requireContext())

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                startLocationUpdates(fusedLocationClient)
            } else {
                viewModel.onLocationUpdated(location)
            }
        }
    }

    /**
     * Start location updates, this will ask the operating system to figure out the devices location.
     * First we call the [ContextCompat.checkSelfPermission] method to check whether we have the
     * persmission to access the devices location, and if not we ask for it and wait until the user
     * grants it. If we already have permission we initialize our [LocationRequest] variable
     * `val request` which a new instance with priority [LocationRequest.PRIORITY_LOW_POWER]. We
     * initialize our [LocationCallback] variable `val callback` with an instance whose lambda
     * overrides the [LocationCallback.onLocationResult] method where it uses the [LocationResult]
     * parameter `locationResult` to fetch its `lastLocation` property returning if it is `null`.
     * If not `null` we call the `onLocationUpdated` method of our [GdgListViewModel] field [viewModel]
     * with the [Location] to have it update its sorted list of [GdgChapter] objects for the new
     * [Location].
     *
     * Having set up our [LocationRequest] and [LocationCallback] we call the `requestLocationUpdates`
     * method of our [FusedLocationProviderClient] parameter [fusedLocationClient] to request location
     * updates using `request` with the callback `callback`, and a `null` Looper thread.
     *
     * @param fusedLocationClient the [FusedLocationProviderClient] we are to use to request location
     * updates.
     */
    private fun startLocationUpdates(fusedLocationClient: FusedLocationProviderClient) {
        // if we don't have permission ask for it and wait until the user grants it
        if (ContextCompat.checkSelfPermission(requireContext(), LOCATION_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
            return
        }

        val request = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER)
        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                viewModel.onLocationUpdated(location)
            }
        }
        fusedLocationClient.requestLocationUpdates(request, callback, Looper.myLooper()!!)
    }

    /**
     * This will be called by Android when the user responds to the permission request. If granted,
     * continue with the operation that the user gave us permission to do. First we call our super's
     * implementation of `onRequestPermissionsResult`. If our [requestCode] parameter is our
     * constant [LOCATION_PERMISSION_REQUEST] we check if the zeroth entry in the [grantResults]
     * parameter is [PackageManager.PERMISSION_GRANTED] and if so we call our method
     * [requestLastLocationOrStartLocationUpdates].
     *
     * @param requestCode request code of the call to [requestPermissions] whose result this is.
     * @param permissions the permissions requested.
     * @param grantResults the grant results for the corresponding permissions in [permissions]
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLastLocationOrStartLocationUpdates()
                }
            }
        }
    }
}


