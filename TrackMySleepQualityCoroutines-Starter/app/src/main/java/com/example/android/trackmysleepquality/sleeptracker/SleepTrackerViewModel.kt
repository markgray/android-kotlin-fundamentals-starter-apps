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
import android.text.Spanned
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for `SleepTrackerFragment`.
 *
 * @param database the [SleepDatabaseDao] to use to access the database
 * @param application the [Application] to use to access resources
 */
class SleepTrackerViewModel(
    val database: SleepDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    /**
     * The [Job] used for the [CoroutineScope] field [uiScope], it allows us to cancel all
     * coroutines started by this [AndroidViewModel] using [uiScope]
     */
    private var viewModelJob = Job()

    /**
     * A [CoroutineScope] that keeps track of all coroutines started by this ViewModel. Because we
     * pass it [viewModelJob], any coroutine started in this scope can be cancelled by calling
     * `viewModelJob.cancel()`. By default, all coroutines started in [uiScope] will launch in
     * [Dispatchers.Main] which is the main thread on Android. This is a sensible default because
     * most coroutines started by a `ViewModel` update the UI after performing some processing.
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /**
     * The [LiveData] wrapped list of all of the [SleepNight] entries read from the database.
     */
    private val nights = database.getAllNights()

    /**
     * Converted nights to Spanned for displaying. We use the [map] method to
     * convert our [LiveData] wrapped list of [SleepNight]'s field [nights] into a [LiveData]
     * wrapped [Spanned] by applying our [formatNights] method to the value in [nights].
     */
    val nightsString: LiveData<Spanned> = nights.map { nights ->
        formatNights(nights, application.resources)
    }

    /**
     * The latest [SleepNight] read back from the database if a sleep recording is in progress (the
     * `endTimeMilli` and `startTimeMilli` fields are equal), or `null` if we are not recording a
     * sleep quality (the last [SleepNight] entered that we read by calling the `getTonight` method
     * of [database] had different `endTimeMilli` and `startTimeMilli` fields).
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
     * Called by our `init` block to initialize our [tonight] field to the last entry added to the
     * database iff it represents a [SleepNight] in progress (`endTimeMilli` == `startTimeMilli`),
     * or to `null` if they are different (the last entry is a completed [SleepNight]). We just
     * launch a coroutine on the [uiScope] `CoroutineScope` which sets the value of [tonight] to
     * the value returned by our suspend function [getTonightFromDatabase].
     */
    private fun initializeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    /**
     * Handling the case of the stopped app or forgotten recording, when the start and end times
     * will be the same. If the start time and end time are not the same, then we do not have an
     * unfinished recording so we return `null`. We call a suspending block with the coroutine
     * context of [Dispatchers.IO], suspending until it completes. The suspending block lambda sets
     * the [SleepNight] variable `var night` to the [SleepNight] returned by the `getTonight` method
     * of [database]. If the `endTimeMilli` field of `night` is not equal to its `startTimeMilli`
     * field we set `night` to `null`. When the lambda completes we return its `night` variable to
     * the caller. Called by our [initializeTonight] method, and by our [onStartTracking] method
     * which is called by a binding expression for the "android:onClick" attribute of the `Button`
     * with ID R.id.start_button in the file layout/fragment_sleep_tracker.xml which is the layout
     * file for `SleepTrackerFragment`.
     *
     * @return the last [SleepNight] added to the database if its `endTimeMilli` and `startTimeMilli`
     * are the same (a sleep recording in progress) or `null` if they differ.
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
     * Starts the recording of a new [SleepNight] entry. We launch a new coroutine without blocking
     * the current thread using the [CoroutineScope] of [uiScope], initializing our [SleepNight]
     * variable `val newNight` with a new instance of [SleepNight] (the constructor captures the
     * current time in both its `startTimeMilli` and `endTimeMilli` fields). We then call our
     * suspend function [insert] to have it insert `newNight` into the database. We then set the
     * value of our `MutableLiveData<SleepNight?>` field [tonight] to the value that our suspend
     * function [getTonightFromDatabase] reads back from the database (it reads the last [SleepNight]
     * inserted into the database, it is necessary to do this because Room auto-generates the primary
     * key `nightId` when it inserts a [SleepNight] into the database). Executes when the START
     * button is clicked because of a binding expression for the "android:onClick" attribute of the
     * button R.id.start_button in the layout file layout/fragment_sleep_tracker.xml
     */
    fun onStartTracking() {
        uiScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }

    /**
     * Inserts its [SleepNight] parameter [night] into the database. We call a suspending block with
     * the coroutine context of [Dispatchers.IO], suspending until it completes. The suspending block
     * lambda calls the `insert` method of [database] to insert our [SleepNight] parameter [night]
     * into the database. Called by our [onStartTracking] method with a newly constructed [SleepNight].
     * A binding expression for the "android:onClick" attribute of the "Start" button calls
     * [onStartTracking].
     *
     * @param night the [SleepNight] to insert into the database.
     */
    private suspend fun insert(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    /**
     * Stops the recording of the present [SleepNight] entry. We launch a new coroutine without
     * blocking the current thread using the [CoroutineScope] of [uiScope], initializing our
     * [SleepNight] variable `val oldNight` with the value of our `MutableLiveData<SleepNight?>`
     * field [tonight] if it is not `null` and returning from the `launch` having done nothing if
     * it is `null`. Continuing with a non-null `oldNight` we set the `endTimeMilli` field of
     * `oldNight` to the current time. We then call our suspend function [update] to have it update
     * the `oldNight` entry in the database.
     *
     * Executes when the STOP button is clicked because of a binding expression for the
     * "android:onClick" attribute of the button R.id.stop_button in the layout file
     * layout/fragment_sleep_tracker.xml
     */
    fun onStopTracking() {
        uiScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
        }
    }

    /**
     * Updates its [SleepNight] parameter [night] in the database if it already exists (checked by
     * primary key). We call a suspending block with the coroutine context of [Dispatchers.IO],
     * suspending until it completes. The suspending block lambda calls the `update` method of
     * [database] to update our [SleepNight] parameter [night]'s entry in the database. Called by
     * our [onStopTracking] method with a modified copy of the [tonight] field (its `endTimeMilli`
     * field has been updated to the current time in milliseconds). [onStopTracking] is called by a
     * binding expression for the "android:onClick" attribute of the "STOP" button in the layout file
     * layout/fragment_sleep_tracker.xml when the user clicks that button.
     *
     * @param night the [SleepNight] object whose entry in the database should be updated.
     */
    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }

    /**
     * Deletes all values from the "daily_sleep_quality_table" table without deleting the table
     * itself. We launch a new coroutine without blocking the current thread using the [CoroutineScope]
     * of [uiScope] and call our suspend function [clearDatabase] to clear the database table. On resuming
     * we set the value of our `MutableLiveData<SleepNight?>` field [tonight] to `null` (since it's
     * no longer in the database). Executes when the CLEAR button is clicked because of a binding
     * expression for the "android:onClick" attribute of the button R.id.clear_button in the layout
     * file layout/fragment_sleep_tracker.xml
     */
    fun onClear() {
        uiScope.launch {
            clearDatabase()
            tonight.value = null
        }
    }

    /**
     * Deletes all values from the "daily_sleep_quality_table" table without deleting the table
     * itself. We call a suspending block with the coroutine context of [Dispatchers.IO],
     * suspending until it completes. The suspending block lambda calls the `clear` method of
     * [database] to delete all values from the "daily_sleep_quality_table" table. Called by our
     * [onClear] method which is called by a binding expression for the "android:onClick" attribute
     * of the "CLEAR" button in the layout file layout/fragment_sleep_tracker.xml when the user
     * clicks that button.
     */
    @Suppress("MemberVisibilityCanBePrivate") // I like to use kdoc [] references
    suspend fun clearDatabase() {
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

