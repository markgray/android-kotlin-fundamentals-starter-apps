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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.*

/**
 * ViewModel for SleepQualityFragment.
 *
 * @param sleepNightKey The `nightId` primary key of the [SleepNight] we are currently working on.
 * @param database Handle to the [SleepDatabaseDao] to use to call its Room SQLite methods.
 */
class SleepQualityViewModel(
    private val sleepNightKey: Long = 0L,
    val database: SleepDatabaseDao
) : ViewModel() {

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = Job()

    /**
     * A [CoroutineScope] keeps track of all coroutines started by this ViewModel. Because we pass
     * it [viewModelJob], any coroutine started in this scope can be cancelled by calling
     * `viewModelJob.cancel()`. By default, all coroutines started in [uiScope] will launch in
     * [Dispatchers.Main] which is the main thread on Android. This is a sensible default because
     * most coroutines started by a [ViewModel] update the UI after performing some processing
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /**
     * Variable that tells the fragment whether it should navigate to `SleepTrackerFragment`.
     * This is `private` because we don't want to expose the ability to set [MutableLiveData] to
     * the `Fragment`. Publicly visible read-only access is provided by [navigateToSleepTracker].
     *
     * Set to `true` by our [onSetSleepQuality] method (which is called by binding expressions for
     * the "android:onClick" attribute of each of the "sleep quality" image views in the layout file
     * layout/fragment_sleep_quality.xml), and set to `null` by our [doneNavigating] method (which
     * is called by the `Observer` of our `navigateToSleepTracker` property added in the `onCreateView`
     * override of `SleepQualityFragment` after it navigates to `SleepTrackerFragment`
     */
    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()

    /**
     * Publicly visible read-only access to our [_navigateToSleepTracker] property.
     * When `true` immediately navigate back to the `SleepTrackerFragment`. An `Observer` is added
     * to it in the `onCreateView` override of `SleepQualityFragment` which navigates to the
     * `SleepTrackerFragment` when it transitions to `true`.
     */
    val navigateToSleepTracker: LiveData<Boolean?>
        get() = _navigateToSleepTracker

    /**
     * Call this immediately after navigating to `SleepTrackerFragment`. It just sets the value of
     * our [_navigateToSleepTracker] property to `null`.
     */
    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

    /**
     * Sets the sleep quality of the [SleepNight] with primary key [sleepNightKey] (the `nightId`
     * column) to our parameter [quality], and updates the database. Then it navigates back to the
     * `SleepTrackerFragment` by setting the value of our [_navigateToSleepTracker] property to
     * `true`.
     *
     * We launch a new coroutine without blocking the current thread on the [uiScope] `CoroutineScope`.
     * In the lambda of the coroutine we call a suspending blocksuspending block with the coroutine context of
     * [Dispatchers.IO] (suspending until it completes). In the suspending block we call the `get`
     * method of our [SleepDatabaseDao] field [database] to retrieve the [SleepNight] with `nightId`
     * column equal to [sleepNightKey] in order to initialize our [SleepNight] variable `val tonight`,
     * but if it is `null` we return from the `withContext` suspending block. If it is not `null`
     * we set the `sleepQuality` property of `tonight` to our parameter [quality] and call the
     * `update` method of [database] to update the `tonight` record on disk. When the [uiScope]
     * coroutine resumes we set the value of our [_navigateToSleepTracker] property to `true` which
     * will cause the `Observer` of our [navigateToSleepTracker] property added in the `onCreateView`
     * override of `SleepQualityFragment` to navigate back to the `SleepTrackerFragment`.
     *
     * Called by binding expressions for the "android:onClick" attribute of each of the "sleep quality"
     * image views in the layout file layout/fragment_sleep_quality.xml with the numeric rating value
     * that the icon represents.
     *
     * @param quality the numeric sleep quality rating.
     */
    fun onSetSleepQuality(quality: Int) {
        uiScope.launch {
            // IO is a thread pool for running operations that access the disk, such as
            // our Room database.
            withContext(Dispatchers.IO) {
                val tonight = database.get(sleepNightKey) ?: return@withContext
                tonight.sleepQuality = quality
                database.update(tonight)
            }

            // Setting this state variable to true will alert the observer and trigger navigation.
            _navigateToSleepTracker.value = true
        }
    }

    /**
     * This method will be called when this [ViewModel] is no longer used and will be destroyed.
     * First we call our super's implementation of `onCleared`, then we cancel our [Job] field
     * [viewModelJob] in order to cancel all coroutines started using [uiScope] to cleanup any
     * pending backgroud work.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}