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

@file:Suppress("JoinDeclarationAndAssignment")

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
 * @param dataSource the [SleepDatabaseDao] that provides access to our database.
 */
class SleepDetailViewModel(
        private val sleepNightKey: Long = 0L,
        dataSource: SleepDatabaseDao) : ViewModel() {

    /**
     * Hold a reference to SleepDatabase via its SleepDatabaseDao.
     */
    val database = dataSource

    /** Coroutine setup variables */

    /**
     * [viewModelJob] allows us to cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = Job()

    /**
     * The [SleepNight] entry in our database with the `nightId` PrimaryKey.
     */
    private val night: LiveData<SleepNight>

    /**
     * Getter for our [night] field.
     */
    fun getNight() = night

    /**
     * We just initialize our `night` field by querying the database for the night whose `nightId`
     * PrimaryKey is the same as our field `sleepNightKey`
     */
    init {
        night=database.getNightWithId(sleepNightKey)
    }

    /**
     * Variable that tells the fragment whether it should navigate to `SleepTrackerFragment`.
     * This is `private` because we don't want to expose the ability to set [MutableLiveData] to
     * the `Fragment`. It is set by our [onClose] method which is called by the android:onClick
     * attribute of the "Close" button in our UI, and set to *null* by our [doneNavigating] method
     * by the `SleepDetailFragment` after it navigates back to the `SleepTrackerFragment`
     */
    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()

    /**
     * When *true* immediately navigate back to the `SleepTrackerFragment`, it is observed by a
     * lambda which is created in the `onCreateView` method of `SleepDetailFragment`.
     */
    val navigateToSleepTracker: LiveData<Boolean?>
        get() = _navigateToSleepTracker

    /**
     * Cancels all coroutines when the ViewModel is cleared, to cleanup any pending work,
     * onCleared() gets called when the ViewModel is destroyed. After calling our super's
     * implementation of `onCleared` we call the `cancel` method of our [Job] field [viewModelJob]
     * to cancel all coroutines started by this ViewModel.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Call this immediately after navigating to `SleepTrackerFragment`
     */
    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

    /**
     * Causes this fragment to be closed, and has the app navigate to the `SleepTrackerFragment`.
     * This is called by the android:onClick attribute of the "Close" button in our UI.
     */
    fun onClose() {
        _navigateToSleepTracker.value = true
    }
}