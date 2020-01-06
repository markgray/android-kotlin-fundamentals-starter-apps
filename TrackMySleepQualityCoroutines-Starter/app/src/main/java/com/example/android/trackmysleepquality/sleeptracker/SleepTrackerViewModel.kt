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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

    /**
     * The [Job] used for the [CoroutineScope] field [uiScope]
     */
    private var viewModelJob = Job()
    /**
     * The [CoroutineScope] used for running coroutines on the Main (UI) thread
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    /**
     * The `LiveData` which holds the automatically updated List of [SleepNight]'s read from the
     * database.
     */
    private val nights = database.getAllNights()
    /**
     * The `LiveData` holding the formatted string version of our [nights] field, it is automatically
     * updated when [nights] is updated
     */
    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }
    /**
     * [MutableLiveData] which holds the automatically updated data for "tonight's" [SleepNight]
     */
    private var tonight = MutableLiveData<SleepNight?>()

    /**
     * Our init block just needs to call our `initializeTonight` method to initialize our
     * automatically updated field `tonight` by launching a database query using the `IO`
     * dispatchers pool of background threads.
     */
    init {
        initializeTonight()
    }

    /**
     * Initializes the value of our [tonight] field by launching a suspend call to our method
     * [getTonightFromDatabase] which returns the [SleepNight] that results when it in turn
     * launches a suspending block with the [Dispatchers.IO] coroutine context which calls the
     * `getTonight` method of our [SleepDatabaseDao] field [database].
     */
    private fun initializeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    /**
     * Launches a suspending block with the [Dispatchers.IO] coroutine context which calls the
     * `getTonight` method of our [SleepDatabaseDao] field [database] in order to retrieve the
     * [SleepNight] for "tonight" on a background thread, returning that [SleepNight] or *null*
     * if that [SleepNight] is not found or if its `endTimeMilli` field is not equal to its
     * `startTimeMilli` field.
     *
     * @return "Tonight's" [SleepNight] read from the database or *null* if it is not found or
     * *null* or if its `endTimeMilli` field is not equal to its `startTimeMilli` field.
     */
    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            night
        }
    }

    /**
     * Starts tracking the sleep for this night by inserting a new instance of [SleepNight] into the
     * database. We launch a suspending block on our [uiScope] Main thread which initializes our
     * [SleepNight] variable `val newNight` with a new instance (its constructor sets both time
     * fields `startTimeMilli` and `endTimeMilli` to the current time in milliseconds and its
     * `sleepQuality` field to an invalid value of minus one). Then we call our suspending method
     * [insert] to have it insert `newNight` into the database using a background IO thread. When
     * that method has completed its task and our method resumes execution we set the value of
     * our [tonight] field to the [SleepNight] that our suspending method [getTonightFromDatabase]
     * returns after re-fetching the [SleepNight] we just inserted using a background IO thread.
     * We are specified as the `OnClickListener` for the "Start" button of the UI using its
     * android:onClick attribute.
     */
    fun onStartTracking() {
        uiScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }

    /**
     * Launches a suspending block with the [Dispatchers.IO] coroutine context which calls the
     * `insert` method of our [SleepDatabaseDao] field [database] in order to insert our [SleepNight]
     * parameter [night] into the database on a background thread.
     *
     * @param night the [SleepNight] we want to insert into the database.
     */
    private suspend fun insert(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    /**
     * Stops the current sleep quality tracking by updating the `endTimeMilli` field of "tonights's"
     * [SleepNight] with the current time in milliseconds. We launch a suspending block on our
     * [uiScope] Main thread which initializes our [SleepNight] variable `val oldNight` to the
     * [SleepNight] `value` field of our [MutableLiveData] field [tonight] if it is not *null* or
     * we return to our caller if is *null*. We set the `endTimeMilli` field of `oldNight` to the
     * current time in milliseconds then call our suspending method [update] to have it update the
     * database with the new value of `oldNight` on a background IO thread. We are specified as the
     * `OnClickListener` for the "Stop" button of the UI using its android:onClick attribute.
     */
    fun onStopTracking() {
        uiScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
        }
    }

    /**
     * Launches a suspending block with the [Dispatchers.IO] coroutine context which calls the
     * `update` method of our [SleepDatabaseDao] field [database] in order to update the value
     * stored in our database for our [SleepNight] parameter [night] on a background thread.
     *
     * @param night the [SleepNight] whose database entry we want to update
     */
    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }

    /**
     * Launches a suspending block on our [uiScope] Main thread which calls our suspending method
     * [clear] to have it clear all entries in the database using a background IO thread call to
     * the `clear` method of our [SleepDatabaseDao] field [database]. When we resume execution
     * after it is done we set the `value` field of our [MutableLiveData] field [tonight] to *null*.
     * We are specified as the `OnClickListener` for the "Clear" button of the UI using its
     * android:onClick attribute.
     */
    fun onClear() {
        uiScope.launch {
            clear()
            tonight.value = null
        }
    }

    /**
     * Launches a suspending block on the [Dispatchers.IO] coroutine scope which calls the `clear`
     * method of our [SleepDatabaseDao] field [database] on a background thread to have it clear
     * all entries from the database.
     */
    suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    /**
     * This method will be called when this `ViewModel` is no longer used and will be destroyed.
     * First we call our super's implementation of `onCleared`, then we call the `cancel` method
     * of our [Job] field [viewModelJob] to cancel it and all of the [Job]'s that might be still
     * running which were started on the [CoroutineScope] of our field [uiScope] (which includes
     * those running on background threads since they are all launched by suspend methods called
     * from the [uiScope]).
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

