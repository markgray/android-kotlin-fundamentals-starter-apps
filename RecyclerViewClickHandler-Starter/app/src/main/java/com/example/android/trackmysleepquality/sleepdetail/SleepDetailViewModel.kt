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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.Job

/**
 * ViewModel for SleepDetailFragment.
 *
 * @param sleepNightKey The key of the current night we are working on.
 * @param dataSource Handle to the [SleepDatabaseDao] to use to call its Room SQLite methods.
 */
class SleepDetailViewModel(
    private val sleepNightKey: Long = 0L,
    dataSource: SleepDatabaseDao
) : ViewModel() {

    /**
     * Hold a reference to `SleepDatabase` via its [SleepDatabaseDao].
     */
    val database: SleepDatabaseDao = dataSource

    /** Coroutine setup variables */

    /**
     * [viewModelJob] allows us to cancel all coroutines started by this ViewModel ... BUT we don't
     * start any coroutines so it is not actually of any use.
     */
    private val viewModelJob = Job()

    /**
     * The [SleepNight] whose primary key is [sleepNightKey] whose detail the `SleepDetailFragment`
     * is displaying. Set by a call to the `getNightWithId` method of our [SleepDatabaseDao] field
     * [database] in our `init` block. It is used in the layout/fragment_sleep_detail.xml layout
     * file in a binding expression for the "app:sleepImage" attribute of the `ImageView` with ID
     * R.id.quality_image (the BindingAdapter for this attribute is the extension function for
     * `ImageView` in BindingUtils.kt `setSleepImage` which sets the drawable of the `ImageView` to
     * one chosen based on the `sleepQuality` property of [night]), in a binding expression for the
     * "app:sleepQualityString" attribute of the `TextView` with ID R.id.quality_string (the
     * BindingAdapter for this attribute is the extension function for `TextView` in BindingUtils.kt
     * `setSleepQualityString` which sets the text of the `TextView` to one formatted based on the
     * `sleepQuality` property of [night]), and in a binding expression for the "app:sleepDurationFormatted"
     * attribute of the `TextView` with ID R.id.sleep_length (the BindingAdapter for this attribute
     * is the extension function for `TextView` in BindingUtils.kt `setSleepDurationFormatted` which
     * sets the text of the `TextView` to one formatted based on the `startTimeMilli` and `endTimeMilli`
     * properties of [night]).
     */
    @Suppress("JoinDeclarationAndAssignment") // It is easier to breakpoint if separated
    private val night: LiveData<SleepNight>

    /**
     * Getter for our [night] field, it is used by binding expressions in the layout file
     * layout/fragment_sleep_detail.xml
     */
    fun getNight(): LiveData<SleepNight> = night


    init {
        /**
         * Initialize our `SleepNight` field `night` from the Room database.
         */
        night = database.getNightWithId(sleepNightKey)
    }

    /**
     * Variable that tells the fragment whether it should navigate to `SleepTrackerFragment`. This
     * is `private` because we don't want to expose the ability to set [MutableLiveData] to the
     * `Fragment`. Set to `true` by our [onClose] method which is called by a binding expression for
     * the "android:onClick" attribute of the "CLOSE" `Button` in the layout/fragment_sleep_detail.xml
     * layout file. Set to `null` by our [doneNavigating] method which is called by an Observer of
     * [navigateToSleepTracker] added to it in the `onCreateView` override of `SleepDetailFragment`
     * after it navigates to `SleepTrackerFragment`. Public read-only access is provided by our
     * [navigateToSleepTracker] property.
     */
    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the `SleepTrackerFragment`. Public read-only access to
     * our [_navigateToSleepTracker] property. An Observer is added to it in the `onCreateView`
     * override of `SleepDetailFragment` which navigates to `SleepTrackerFragment` when it transitions
     * to `true`.
     */
    val navigateToSleepTracker: LiveData<Boolean?>
        get() = _navigateToSleepTracker

    /**
     * Cancels all coroutines when the ViewModel is cleared, to cleanup any pending work.
     * onCleared() gets called when the ViewModel is destroyed. We do not start any
     * coroutines so this is not really necessary
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Call this immediately after navigating to `SleepTrackerFragment`, we just set our [LiveData]
     * wrapped [Boolean] field [_navigateToSleepTracker] to `null`. Called by the Observer added
     * to [navigateToSleepTracker] in the `onCreateView` override of `SleepDetailFragment` after
     * it navigates to `SleepTrackerFragment`.
     */
    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

    /**
     * Sets our [LiveData] wrapped [Boolean] field [_navigateToSleepTracker] to `true` to trigger
     * the navigation to `SleepTrackerFragment` by the Observer added to [navigateToSleepTracker] in
     * the `onCreateView` override of `SleepDetailFragment`. It is called by the binding expression
     * for the "android:onClick" attribute of the "CLOSE" `Button` in the layout file
     * layout/fragment_sleep_detail.xml.
     */
    fun onClose() {
        _navigateToSleepTracker.value = true
    }

}