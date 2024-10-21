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

import android.location.Location
import androidx.lifecycle.*
import com.example.android.gdgfinder.network.GdgApi
import com.example.android.gdgfinder.network.GdgChapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException


/**
 * The [ViewModel] which controls our `GdgListFragment`.
 */
class GdgListViewModel : ViewModel() {
    /**
     * Handle to the Repository for our app.
     */
    private val repository = GdgChapterRepository(GdgApi.retrofitService)

    /**
     * The `currentValue` property of this [FilterHolder] is used to filter the list of downloaded
     * [GdgChapter] objects when the user chooses to apply a filter by selecting a `Chip`.
     */
    private var filter = FilterHolder()

    /**
     * This [Job] is used to cancel any background coroutines that are already running for a previous
     * call when our [onQueryChanged] method is called.
     */
    private var currentJob: Job? = null

    /**
     * The results of calling the `getChaptersForFilter` method of [repository] for the current filter,
     * it is updated by our [onQueryChanged] method, and the immutable external [LiveData] interface
     * to this property is our [gdgList] property.
     */
    private val _gdgList = MutableLiveData<List<GdgChapter>>()

    /**
     * The results of calling the `getFilters` method of [repository], it contains the `filters`
     * field of the `SortedData` instance created by its `sortedData` method and is a list of all
     * of the unique `region` fields of the downloaded list of [GdgChapter] objects.
     */
    private val _regionList = MutableLiveData<List<String>>()

    /**
     * When this is `true` a `Snackbar` requesting the user to enable location in settings is shown
     * by an `Observer` of the publicly exposed immutable getter [showNeedLocation] which is created
     * in the `onCreateView` override of `GdgListFragment`. Our `init` block launches a coroutine
     * which after a 5,000 millisecond delay sets this to the inverse of the `isFullyInitialized`
     * field of our [GdgChapterRepository] field [repository].
     */
    private val _showNeedLocation = MutableLiveData<Boolean>()

    /**
     * The external [LiveData] interface to the [_gdgList] property. [gdgList] is immutable, so
     * only this class can modify [_gdgList]. This property is referenced by 2 binding expressions
     * in the layout/fragment_gdg_list.xml layout file. Once as the value of the "app:listData"
     * attribute of its `RecyclerView`, and once as the value of the "app:showOnlyWhenEmpty"
     * attribute of the `TextView` displaying the [String] "Waiting for location and network result".
     * "app:listData" is a binding adapter for `RecyclerView` in `BindingApaters.kt` which submits
     * the `gdgList` to the `GdgListAdapter` of the `RecyclerView` to set the new list to be
     * displayed. "app:showOnlyWhenEmpty" is a binding adapter for `View` in `BindingApaters.kt`
     * which sets the visibility of the `View` to visible only while the list is empty (or `null`).
     */
    val gdgList: LiveData<List<GdgChapter>>
        get() = _gdgList

    /**
     * The external [LiveData] interface to the [_regionList] property. [regionList] is immutable,
     * so only this class can modify [_regionList]. An `Observer` created in the `onCreateView`
     * override of `GdgListFragment` updates the `Chip`'s in the filter selector `ChipGroup` to
     * display this list whenever it changes.
     */
    val regionList: LiveData<List<String>>
        get() = _regionList

    /**
     * The external [LiveData] interface to the [_showNeedLocation] property. An `Observer` created
     * in the `onCreateView` override of `GdgListFragment` shows a `Snackbar` requesting the user to
     * enable location in settings when this is `true`.
     */
    val showNeedLocation: LiveData<Boolean>
        get() = _showNeedLocation

    init {
        // process the initial filter
        onQueryChanged()
        /**
         * Launches a coroutine which delays for 5,000 milliseconds, then sets our `_showNeedLocation`
         * property to the inverse of the `isFullyInitialized` property of `repository`
         */
        viewModelScope.launch {
            delay(5_000)
            _showNeedLocation.value = !repository.isFullyInitialized
        }
    }

    /**
     * This is called whenever it is necessary to reread the list of [GdgChapter] objects because of
     * a location or filter change. It is called by our `init` block to process the initial filter,
     * by our [onFilterChanged] method when the user changes the filter, and by our [onLocationUpdated]
     * method when the location of the device changes.
     *
     * If our [Job] field [currentJob] is not `null` we call its `cancel` method to cancel the running
     * of the previous query. Then we set [currentJob] to the [Job] returned when we launch a coroutine
     * on the `viewModelScope`. The lambda block of this coroutine consists of a `try` block which
     * sets the `value` of [_gdgList] to the chapters list for the `currentValue` filter of our
     * [FilterHolder] field [filter] which the `getChaptersForFilter` method of [GdgChapterRepository]
     * field [repository] returns. Then we retrieve the list of filters returned by the `getFilters`
     * method of [repository] and if it is different than our [_regionList] field we update the
     * contents of [_regionList] with it. If the `try` block catches [IOException] we set [_gdgList]
     * to an empty list.
     */
    private fun onQueryChanged() {
        currentJob?.cancel() // if a previous query is running cancel it before starting another
        currentJob = viewModelScope.launch {
            try {
                _gdgList.value = repository.getChaptersForFilter(filter.currentValue)
                repository.getFilters().let {
                    // only update the filters list if it's changed since the last time
                    if (it != _regionList.value) {
                        _regionList.value = it
                    }
                }
            } catch (e: IOException) {
                _gdgList.value = listOf()
            }
        }
    }

    /**
     * Called from the `requestLastLocationOrStartLocationUpdates` method and the `startLocationUpdates`
     * method of `GdgListFragment` with the new [Location] of the device. We launch a coroutine on
     * the [viewModelScope] `CoroutineScope` whose lambda calls the `onLocationChanged` suspend
     * method of [GdgChapterRepository] field [repository] with the new [location], then calls our
     * [onQueryChanged] method when the thread resumes.
     *
     * @param location the new [Location] of the device.
     */
    fun onLocationUpdated(location: Location) {
        viewModelScope.launch {
            repository.onLocationChanged(location)
            onQueryChanged()
        }
    }

    /**
     * Called from the `OnCheckedChangeListener` of each of the `Chip` widgets in the region filter
     * `ChipGroup`. We call the `update` method of our [FilterHolder] field [filter] with our
     * parameters and if the value of the its `currentValue` field has changed as a result of the
     * selection change we call our method [onQueryChanged] to reread the list of [GdgChapter]
     * objects based on the new filter.
     *
     * @param filter the [String] value of the tag of the `Chip`
     * @param isChecked `true` if the `Chip` is selected.
     */
    fun onFilterChanged(filter: String, isChecked: Boolean) {
        if (this.filter.update(filter, isChecked)) {
            onQueryChanged()
        }
    }

    /**
     * Class we use to hold the current region filter.
     */
    private class FilterHolder {
        var currentValue: String? = null
            private set

        /**
         * Called to update the contents of our [currentValue] field when one of the region filter
         * `Chip`'s changes its checked state. If [isChecked] is `true` we set our [currentValue]
         * field to our [changedFilter] parameter and return `true`, and if [isChecked] is `false`
         * we set our [currentValue] field to `null` and return `true`. Otherwise we return `false`.
         *
         * @param changedFilter the region that the `Chip` represents.
         * @param isChecked the new "checked" state of the `Chip`
         * @return `true` if the event caused us to update the contents of our [currentValue] field.
         */
        fun update(changedFilter: String, isChecked: Boolean): Boolean {
            if (isChecked) {
                currentValue = changedFilter
                return true
            } else if (currentValue == changedFilter) {
                currentValue = null
                return true
            }
            return false
        }
    }
}

