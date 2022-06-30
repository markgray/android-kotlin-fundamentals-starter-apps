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
import com.example.android.gdgfinder.network.GdgApiService
import com.example.android.gdgfinder.network.GdgChapter
import com.example.android.gdgfinder.network.GdgResponse
import com.example.android.gdgfinder.network.LatLong
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/**
 * The Repository for our app. There is no offline cache for simplicity and the result of the
 * network is just cached in memory.
 */
class GdgChapterRepository(gdgApiService: GdgApiService) {

    /**
     * A single network request, the results won't change. For this lesson we did not add an offline
     * cache for simplicity and the result will be cached in memory.
     */
    private val request = gdgApiService.getChapters()

    /**
     * An in-progress (or potentially completed) sort, this may be null or cancelled at any time.
     * If this is non-null, calling await will get the result of the last sorting request.
     * This will be cancelled whenever location changes, as the old results are no longer valid.
     */
    private var inProgressSort: Deferred<SortedData>? = null

    /**
     * Set to `true` in our [onLocationChanged] method when the devices location has been found.
     * The `init` block of `GdgListViewModel` launches a coroutine which waits 5 seconds then sets
     * the value of its `MutableLiveData` wrapped [Boolean] property `_showNeedLocation` to the
     * inverse of [isFullyInitialized] which triggers an observer of `showNeedLocation` created
     * in the `onCreateView` override of `GdgListFragment` to pop up a `Snackbar` to tell the user
     * to enable location for the app if it is `true` (ie. [isFullyInitialized] is `false`).
     */
    var isFullyInitialized: Boolean = false
        private set


    /**
     * Get the chapters list for a specified filter. This will be cancel if a new location is sent
     * before the result is available. This works by first waiting for any previously in-progress
     * sorts, and if a sort has not yet started it will start a new sort (which may happen if
     * location is disabled on the device). First we initialize our [SortedData] variable `val data`
     * to the [SortedData] instance returned by our method [sortedData]. When our [String] parameter
     * [filter] is `null` we return the `chapters` field of `data` (this is the entire list of
     * [GdgChapter] objects sorted by distance from our devices current location if that is
     * available or the unsorted list if it is not). When our [String] parameter [filter] is not
     * `null` we return the list of [GdgChapter] objects stored under the [filter] key in the
     * `chaptersByRegion` field of `data` or an empty read-only list if there was none there).
     *
     * @param filter the region key of the `chaptersByRegion` map of the [SortedData] created from
     * our `Deferred<GdgResponse>` field [request] that we wish to filter by.
     * @return the [GdgChapter] list for the specified region [filter].
     */
    suspend fun getChaptersForFilter(filter: String?): List<GdgChapter> {
        val data = sortedData()
        return when (filter) {
            null -> data.chapters
            else -> data.chaptersByRegion.getOrElse(filter) { emptyList() }
        }
    }

    /**
     * Get the filters sorted by distance from the last location. This will cancel if a new location
     * is sent before the result is available. This works by first waiting for any previously
     * in-progress sorts, and if a sort has not yet started it will start a new sort (which may
     * happen if location is disabled on the device). We return the `filters` field of the
     * [SortedData] instance returned by the [sortedData] method.
     *
     * @return the `filters` field of the [SortedData] instance created by our [sortedData] method.
     */
    suspend fun getFilters(): List<String> = sortedData().filters

    /**
     * Get the computed sorted data after it completes, or start a new sort if none are running.
     * This will always cancel if the location changes while the sort is in progress. If our
     * [Deferred] wrapped [SortedData] field [inProgressSort] is not `null` a sort is in progress
     * so call its [Deferred.await] method to wait for the result and return that, otherwise we
     * return the [SortedData] instance returned by our [doSortData] method.
     *
     * @return the computed sorted verison of our GDG list in a [SortedData] instance
     */
    private suspend fun sortedData(): SortedData = withContext(Dispatchers.Main) {
        /**
         * We need to ensure we're on Dispatchers.Main so that this is not running on multiple
         * Dispatchers and we modify the member inProgressSort. Since this was called from
         * viewModelScope, that will always be a simple if check (not expensive), but by specifying
         * the dispatcher we can protect against incorrect usage. If there's currently a sort
         * running (or completed) wait for it to complete and return that value otherwise, start a
         * new sort with no location (the user has likely not given us permission to use location
         * yet)
         */
        inProgressSort?.await() ?: doSortData()
    }

    /**
     * Call this to force a new sort to start. This will start a new coroutine to perform the sort.
     * Future requests for sorted data can use the deferred in [inProgressSort] to get the result of
     * the last sort without sorting the data again. This guards against multiple sorts being
     * performed on the same data, which is inefficient. This will always cancel if the location
     * changes while the sort is in progress. To initialize our [SortedData] variable `val result`
     * we create a `CoroutineScope` which launches a suspend block which initializes a [Deferred]
     * wrapped [SortedData] variable `val deferred` to the result returned by the coroutine that
     * the [async] method creates whose coroutine code block calls the [SortedData.from] method
     * to process the [GdgResponse] object that results from calling the `await` method of [request]
     * using our [Location] parameter [location] as the location to sort for. The `deferred` value
     * is then cached in our filed [inProgressSort] before we call the [Deferred.await] method of
     * `deferred` to produce the final [SortedData] instance to that we initialize `result` to.
     * Finally we return `result` to the caller.
     *
     * @param location the current [Location] of the device we are running on.
     * @return the result of the started sort stored in a [SortedData] instance
     */
    private suspend fun doSortData(location: Location? = null): SortedData {
        /**
         * Since we'll need to launch a new coroutine for the sorting use coroutineScope.
         * coroutineScope will automatically wait for anything started via async {} or await{}
         * in it's block to complete.
         */
        val result = coroutineScope {
            /**
             * launch a new coroutine to do the sort (so other requests can wait for this sort to
             * complete)
             */
            val deferred = async { SortedData.from(request.await(), location) }
            // cache the Deferred so any future requests can wait for this sort
            inProgressSort = deferred
            // and return the result of this sort
            deferred.await()
        }
        return result
    }

    /**
     * Call when location changes. This will cancel any previous queries, so it's important to
     * re-request the data after calling this function. We call a suspending block on the
     * [Dispatchers.Main] coroutine context suspending until it completes. The block sets our
     * [isFullyInitialized] property to `true` then cancels the [inProgressSort] job if it is not
     * `null`, and calls our [doSortData] method with our [Location] parameter [location] to
     * start a new sort.
     *
     * @param location the location to sort by
     */
    suspend fun onLocationChanged(location: Location) {
        /**
         * We need to ensure we're on Dispatchers.Main so that this is not running on multiple
         * Dispatchers and we modify the member [inProgressSort]. Since this was called from
         * viewModelScope, that will always be a simple `if` check (not expensive), but by
         * specifying the dispatcher we can protect against incorrect usage.
         */
        withContext(Dispatchers.Main) {
            isFullyInitialized = true

            // cancel any in progress sorts, their result is not valid anymore.
            inProgressSort?.cancel()

            doSortData(location)
        }
    }

    /**
     * Holds data sorted by the distance from the last location. Note, by convention this class
     * won't sort on the Main thread. This is not a public API and should only be called by
     * [doSortData].
     *
     * @param chapters the list of [GdgChapter] objects sorted by distance from the devices location
     * @param filters the list of regions that one can filter by objects sorted by distance from the
     * devices location
     * @param chaptersByRegion a map whose key is the region of the [GdgChapter] and whose value is
     * the list of [GdgChapter] objects that share that region.
     */
    private class SortedData private constructor(
        val chapters: List<GdgChapter>,
        val filters: List<String>,
        val chaptersByRegion: Map<String, List<GdgChapter>>
    ) {

        companion object {
            /**
             * Sort the data from a [GdgResponse] by the specified location. We call a suspend block
             * with the [Dispatchers.Default] coroutine context, suspending until it completes. In
             * that block we initialize our [List] of [GdgChapter] variable `val chapters` to the
             * list that our `sortByDistanceFrom` extension method creates from the `chapters` field
             * of our [GdgResponse] parameter [response] for our [Location] parameter [location].
             * We then initialize our [List] of [String] variable `val filters` by extracting all
             * the unique `region` fields of the [GdgChapter] objects in `chapters`. We initialize
             * our [Map] of [String] to [List] of [GdgChapter] variable `val chaptersByRegion` to
             * the [Map] created by grouping all of the [GdgChapter] objects in `chapters` under
             * the key of their `region` field. Finally we return a [SortedData] instance constructed
             * from `chapters`, `filters`, and `chaptersByRegion`.
             *
             * @param response the response to sort
             * @param location the location to sort by, if null the data will not be sorted.
             * @return a [SortedData] instance constructed to hold the results of sorting the list
             * of [GdgChapter] objects by their distance from the devices current location.
             */
            suspend fun from(response: GdgResponse, location: Location?): SortedData {
                return withContext(Dispatchers.Default) {
                    // this sorting is too expensive to do on the main thread, so do thread confinement here.
                    val chapters: List<GdgChapter> = response.chapters.sortByDistanceFrom(location)
                    // use distinctBy which will maintain the input order - this will have the effect of making
                    // a filter list sorted by the distance from the current location
                    val filters: List<String> = chapters.map { it.region }.distinctBy { it }
                    // group the chapters by region so that filter queries don't require any work
                    val chaptersByRegion: Map<String, List<GdgChapter>> =
                        chapters.groupBy { it.region }
                    // return the sorted result
                    SortedData(chapters, filters, chaptersByRegion)
                }

            }


            /**
             * Sort a list of [GdgChapter] by their distance from the specified location. If our
             * [Location] parameter [currentLocation] is `null` we just return `this`. Otherwise
             * we return the list returned by the [sortedBy] method when it sorts `this` using a
             * selector lambda which uses the value that our [distanceBetween] method returns
             * when it calculates the distance between the `geo` [LatLong] field of the list item
             * being compared and our [Location] parameter [currentLocation].
             *
             * @param currentLocation returned list will be sorted by the distance from this
             * [Location], or unsorted if null
             * @return `this` [List] of [GdgChapter] objects sorted by the distance between their
             * `geo` [Location] field and our [Location] parameter [currentLocation], or unsorted if
             * [currentLocation] is `null`.
             */
            private fun List<GdgChapter>.sortByDistanceFrom(currentLocation: Location?): List<GdgChapter> {
                currentLocation ?: return this

                return sortedBy { distanceBetween(it.geo, currentLocation) }
            }

            /**
             * Calculate the distance (in meters) between a LatLong and a Location. We initialize
             * our [FloatArray] variable `val results` with an instance for 3 float values. Then
             * we call the [Location.distanceBetween] to have it compute the approximate distance
             * in meters between the location whose latitude is the `lat` field of our [LatLong]
             * parameter [start], and longitude is the `long` field of [start], and the location
             * whose latitude is the `latitude` field of our [Location] parameter [currentLocation],
             * and longitude is the `longitude` field of [currentLocation]. The results of this
             * computation is stored in our `results` array, and we return the contents of the
             * zeroth entry of `results` (the computed distance) to the caller.
             */
            private fun distanceBetween(start: LatLong, currentLocation: Location): Float {
                val results = FloatArray(3)
                Location.distanceBetween(
                    start.lat,
                    start.long,
                    currentLocation.latitude,
                    currentLocation.longitude,
                    results
                )
                return results[0]
            }
        }
    }
}