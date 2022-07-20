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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for SleepTrackerFragment.
 *
 * @param dataSource the [SleepDatabaseDao] to use to access the database
 * @param application the Application to use to access resources
 */
class SleepTrackerViewModel(
    dataSource: SleepDatabaseDao,
    application: Application
) : ViewModel() {

    /**
     * Hold a reference to SleepDatabase via SleepDatabaseDao.
     */
    val database: SleepDatabaseDao = dataSource

    /** Coroutine variables */

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */
    private var viewModelJob = Job()

    /**
     * A [CoroutineScope] keeps track of all coroutines started by this ViewModel. Because we pass
     * it [viewModelJob], any coroutine started in this scope can be cancelled by calling
     * `viewModelJob.cancel()`. By default, all coroutines started in [uiScope] will launch in
     * [Dispatchers.Main] which is the main thread on Android. This is a sensible default because
     * most coroutines started by a [ViewModel] update the UI after performing some processing.
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /**
     * The latest [SleepNight] read back from the database if a sleep recording is in progress (the
     * `endTimeMilli` and `startTimeMilli` fields are equal), or `null` if we are not recording a
     * sleep quality (the last [SleepNight] entered that we read by calling the `getTonight` method
     * of [database] had different `endTimeMilli` and `startTimeMilli` fields). When non-null we
     * update it in our [onStop] method when the STOP button is clicked.
     */
    private var tonight = MutableLiveData<SleepNight?>()

    /**
     * The [LiveData] wrapped list of all of the [SleepNight] entries read from the database.
     */
    val nights: LiveData<List<SleepNight>> = database.getAllNights()

    /**
     * Converted nights to Spanned for displaying (used before the RecyclerView was added).
     */
    @Suppress("unused")
    val nightsString: LiveData<Spanned> = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }

    /**
     * If tonight has not been set, then the START button should be visible.
     */
    val startButtonVisible: LiveData<Boolean> = Transformations.map(tonight) {
        null == it
    }

    /**
     * If tonight has been set, then the STOP button should be visible.
     */
    val stopButtonVisible: LiveData<Boolean> = Transformations.map(tonight) {
        null != it
    }

    /**
     * If there are any nights in the database, show the CLEAR button.
     */
    val clearButtonVisible: LiveData<Boolean?> = Transformations.map(nights) {
        it?.isNotEmpty()
    }


    /**
     * Request a Snackbar by setting this value to true. This is private because we don't want to
     * expose setting this value to the Fragment, publicly available read-only access is provided
     * by [showSnackBarEvent]. This is set to `true` by our [onClear] method and set to `null` by
     * our [doneShowingSnackbar] method. [onClear] is called by a binding expression for the
     * "android:onClick" attribute of the "Clear" button, and [doneShowingSnackbar] is called by
     * the `Observer` added to [showSnackBarEvent] after it shows the Snackbar.
     */
    private var _showSnackbarEvent = MutableLiveData<Boolean?>()

    /**
     * If this is true, immediately `show()` a Snackbar and call [doneShowingSnackbar] to reset to
     * `null`. An `Observer` is added to it in the `onCreateView` override of `SleepTrackerFragment`
     * which shows a Snackbar informing the user "All your data is gone forever"
     */
    val showSnackBarEvent: LiveData<Boolean?>
        get() = _showSnackbarEvent

    /**
     * Variable that tells the Fragment to navigate to `SleepQualityFragment` using the specified
     * [SleepNight] as the safe args for the fragment. This is private because we don't want to
     * expose setting this value to the Fragment. Set to the value of our [tonight] if it is not
     * `null` by our [onStop] method which is called by a binding expression for the "android:onClick"
     * attribute of the "Stop" button. Set to `null` by our [doneNavigating] method which is called
     * after navigating to the `SleepQualityFragment` to prevent repeated navigating.
     */
    private val _navigateToSleepQuality = MutableLiveData<SleepNight?>()

    /**
     * If this is non-null, immediately navigate to `SleepQualityFragment` and call [doneNavigating].
     * An `Observer` is added to it in the `onCreateView` override of `SleepTrackerFragment` which
     * navigates to `SleepTrackerFragment` using the `nightId` primary key of the [SleepNight] as
     * the safe args to pass.
     */
    val navigateToSleepQuality: MutableLiveData<SleepNight?>
        get() = _navigateToSleepQuality

    /**
     * Call this immediately after calling `show()` on a Snackbar. It will clear the Snackbar request,
     * so if the user rotates their phone it won't show a duplicate Snackbar.
     */
    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = null
    }

    /**
     * Call this immediately after navigating to `SleepQualityFragment`. It will clear the navigation
     * request, so if the user rotates their phone it won't navigate twice.
     */
    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }

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
     * of [database]. It the `endTimeMilli` field of `night` is not equal to its `startTimeMilli`
     * field we set `night` to `null`. When the lambda completes we return its `night` variable to
     * the caller. Called by our [initializeTonight] method and by our [onStart] method when the
     * user clicks the "Start" button (a binding expression for the "android:onClick" attribute of
     * the button calls [onStart]).
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
     * Inserts its [SleepNight] parameter [night] into the database. We call a suspending block with
     * the coroutine context of [Dispatchers.IO], suspending until it completes. The suspending block
     * lambda calls the `insert` method of [database] to insert our [SleepNight] parameter [night]
     * into the database. Called by our [onStart] method with a newly constructed [SleepNight]. A
     * binding expression for the "android:onClick" attribute of the "Start" button calls [onStart].
     *
     * @param night the [SleepNight] to insert into the database.
     */
    private suspend fun insert(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    /**
     * Updates its [SleepNight] parameter [night] in the database if it already exists (checked by
     * primary key). We call a suspending block with the coroutine context of [Dispatchers.IO],
     * suspending until it completes. The suspending block lambda calls the `update` method of
     * [database] to update our [SleepNight] parameter [night]'s entry in the database. Called by
     * our [onStop] method with a modified copy of the [tonight] field (its `endTimeMilli` field
     * has been updated to the current time in milliseconds). [onStop] is called by a binding
     * expression for the "android:onClick" attribute of the "STOP" button in the layout file
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
     * itself. We call a suspending block with the coroutine context of [Dispatchers.IO],
     * suspending until it completes. The suspending block lambda calls the `clear` method of
     * [database] to delete all values from the "daily_sleep_quality_table" table. Called by our
     * [onClear] method which is called by a binding expression for the "android:onClick" attribute
     * of the "CLEAR" button in the layout file layout/fragment_sleep_tracker.xml when the user
     * clicks that button.
     */
    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    /**
     * Starts the recording of a new [SleepNight] entry. We launch a new coroutine without blocking
     * the current thread using the [CoroutineScope] of [uiScope], initializing our [SleepNight]
     * variable `val newNight` with a new instance of [SleepNight] (the constructor captures the
     * current time in both its `startTimeMilli` and `endTimeMilli` fields). We then call our
     * suspend function [insert] to have it insert `newNight` into the database. We then set the
     * valuse of our `MutableLiveData<SleepNight?>` field [tonight] to the value that our suspend
     * function [getTonightFromDatabase] reads back from the database (it reads the last [SleepNight]
     * inserted into the database, it is necessary to do this because Room auto-generates the primary
     * key `nightId` when it inserts a [SleepNight] into the database). Executes when the START
     * button is clicked because of a binding expression for the "android:onClick" attribute of the
     * button R.id.start_button in the layout file layout/fragment_sleep_tracker.xml
     */
    fun onStart() {
        uiScope.launch {
            // Create a new night, which captures the current time,
            // and insert it into the database.
            val newNight = SleepNight()

            insert(newNight)

            tonight.value = getTonightFromDatabase()
        }
    }

    /**
     * Stops the recording of the present [SleepNight] entry. We launch a new coroutine without
     * blocking the current thread using the [CoroutineScope] of [uiScope], initializing our
     * [SleepNight] variable `val oldNight` with the value of our `MutableLiveData<SleepNight?>`
     * field [tonight] if it is not `null` and returning from the `launch` having done nothing if
     * it is `null`. Continuing with a non-null `oldNight` we set the `endTimeMilli` field of
     * `oldNight` to the current time. We then call our suspend function [update] to have it update
     * the `oldNight` entry in the database. Upon resuming we set the value of our [MutableLiveData]
     * wrapped [SleepNight] field [_navigateToSleepQuality] to `oldNight` which will trigger the
     * navigation to the `SleepQualityFragment` thanks to an `Observer` of [navigateToSleepQuality].
     * Executes when the STOP button is clicked because of a binding expression for the
     * "android:onClick" attribute of the button R.id.stop_button in the layout file
     * layout/fragment_sleep_tracker.xml
     */
    fun onStop() {
        uiScope.launch {
            // In Kotlin, the return@label syntax is used for specifying which function among
            // several nested ones this statement returns from.
            // In this case, we are specifying to return from launch().
            val oldNight = tonight.value ?: return@launch

            // Update the night in the database to add the end time.
            oldNight.endTimeMilli = System.currentTimeMillis()

            update(oldNight)

            // Set state to navigate to the SleepQualityFragment.
            _navigateToSleepQuality.value = oldNight
        }
    }

    /**
     * Deletes all values from the "daily_sleep_quality_table" table without deleting the table
     * itself. We launch a new coroutine without blocking the current thread using the [CoroutineScope]
     * of [uiScope] and call our suspend function [clear] to clear the database table. On resuming
     * we set the value of our `MutableLiveData<SleepNight?>` field [tonight] to `null` (since it's
     * no longer in the database), and set our [MutableLiveData] wrapped [Boolean] field [_showSnackbarEvent]
     * to `true` causing an `Observer` of [showSnackBarEvent] to post a `SnackBar` informing the user
     * that his data is gone. Executes when the CLEAR button is clicked because of a binding expression
     * for the "android:onClick" attribute of the button R.id.clear_button in the layout file
     * layout/fragment_sleep_tracker.xml
     */
    fun onClear() {
        uiScope.launch {
            // Clear the database table.
            clear()

            // And clear tonight since it's no longer in the database
            tonight.value = null

            // Show a snackbar message, because it's friendly.
            _showSnackbarEvent.value = true
        }
    }

    /**
     * Called when the ViewModel is dismantled. After calling our super's implementation of `onCleared`
     * we cancel all coroutines started using our [CoroutineScope] field [uiScope] by calling the
     * `cancel` method of our [Job] field [viewModelJob] (recall that [uiScope] uses [viewModelJob]
     * as part of its coroutine context). If we don't do this we might end up with processes that
     * have nowhere to return to using memory and resources.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}