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
import com.example.android.trackmysleepquality.sleepquality.SleepQualityFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for SleepTrackerFragment.
 *
 * @param dataSource handle to the [SleepDatabaseDao] for our Room database
 * @param application used to fetch resources from our apk.
 */
class SleepTrackerViewModel(
    dataSource: SleepDatabaseDao,
    application: Application) : ViewModel() {

    /**
     * Hold a reference to SleepDatabase via SleepDatabaseDao.
     */
    val database: SleepDatabaseDao = dataSource

    /** Coroutine variables */

    /**
     * [viewModelJob] allows us to cancel all coroutines started by this ViewModel, this is because
     * our [CoroutineScope] field [uiScope] appends [viewModelJob] to [Dispatchers.Main] to create
     * its context and we use [uiScope] to launch all of our background co-routines.
     */
    private var viewModelJob = Job()

    /**
     * A [CoroutineScope] keeps track of all coroutines started by this ViewModel.
     *
     * Because we pass it [viewModelJob], any coroutine started in this uiScope can be cancelled
     * by calling `viewModelJob.cancel()`
     *
     * By default, all coroutines started in uiScope will launch in [Dispatchers.Main] which is
     * the main thread on Android. This is a sensible default because most coroutines started by
     * a [ViewModel] update the UI after performing some processing.
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /**
     * Holds tonight's [SleepNight] retrieved from the database by our [getTonightFromDatabase]
     * method by calling the `getTonight` method of the [SleepDatabaseDao] of the [database] handle
     * to our database.
     */
    private var tonight = MutableLiveData<SleepNight?>()

    /**
     * Contains all rows in the table, sorted by `nightId` PrimaryKey in descending order.
     */
    val nights: LiveData<List<SleepNight>> = database.getAllNights()

    /**
     * Converted nights to Spanned for displaying.
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
     * Request a `SnackBar` by setting this value to true.
     *
     * This is private because we don't want to expose setting this value to the Fragment.
     */
    private var _showSnackbarEvent = MutableLiveData<Boolean?>()

    /**
     * If this is true, immediately `show()` a `SnackBar` and call `doneShowingSnackbar()`.
     */
    val showSnackBarEvent: LiveData<Boolean?>
        get() = _showSnackbarEvent

    /**
     * Variable that tells the Fragment to navigate to [SleepQualityFragment] to select a quality
     * ICON for the [SleepNight] being edited.
     *
     * This is private because we don't want to expose setting this value to the Fragment.
     */
    private val _navigateToSleepQuality = MutableLiveData<SleepNight?>()

    /**
     * If this is non-null, immediately navigate to [SleepQualityFragment] and call [doneNavigating]
     */
    val navigateToSleepQuality: LiveData<SleepNight?>
        get() = _navigateToSleepQuality

    /**
     * Call this immediately after calling `show()` on a `SnackBar`.
     *
     * It will clear the `SnackBar` request, so if the user rotates their phone it won't show a
     * duplicate `SnackBar`.
     */
    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = null
    }

    /**
     * Call this immediately after navigating to [SleepQualityFragment]
     *
     * It will clear the navigation request, so if the user rotates their phone it won't navigate
     * twice.
     */
    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }

    /**
     * Private Navigation for the SleepDetail fragment.
     */
    private val _navigateToSleepDetail = MutableLiveData<Long?>()

    /**
     * Read only access to [_navigateToSleepDetail]
     */
    val navigateToSleepDetail: MutableLiveData<Long?>
        get() = _navigateToSleepDetail

    /**
     * Setter for the value of our [MutableLiveData] property [_navigateToSleepDetail]. It is called
     * by a lambda which calls us with the `nightId` PrimaryKey of a [SleepNight] item in the
     * `RecyclerView` of `SleepTrackerFragment` which the user has clicked.
     */
    fun onSleepNightClicked(id: Long) {
        _navigateToSleepDetail.value = id
    }

    /**
     * Resets the value of our [MutableLiveData] property [_navigateToSleepDetail] to *null*. Needs
     * to be called immediately after navigating to `SleepDetailFragment` to prevent navigating
     * again when a configuration change occurs.
     */
    fun onSleepDetailNavigated() {
        _navigateToSleepDetail.value = null
    }

    init {
        initializeTonight()
    }

    /**
     * Called to initialize the value of our [MutableLiveData] property [tonight] (holds the result
     * of calling the `getTonight` method of [database] to retrieve the [SleepNight] for "tonight").
     * We use our [uiScope]  to launch on the UI [CoroutineScope] a call to our suspending method
     * [getTonightFromDatabase] and set the value of [tonight] to the [SleepNight] it returns.
     */
    private fun initializeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    /**
     * Handling the case of the stopped app or forgotten recording, the start and end times will be
     * the same. If the start time and end time are not the same, then we do not have an unfinished
     * recording. We launch a lambda on the background [CoroutineScope] of [Dispatchers.IO]. The
     * lambda initializes the [SleepNight] variable `var night` to the [SleepNight] returned by the
     * `getTonight` method of our [database], and if the `endTimeMilli` field of `night` is not equal
     * to its `startTimeMilli` field (the entry for the last [SleepNight] is completed already) we
     * set `night` to *null*. Finally we return `night` to the caller.
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
     * Inserts a new [SleepNight] into the database. We launch a suspending lambda on the background
     * [CoroutineScope] of [Dispatchers.IO] which calls the `insert` method of [database] to have
     * Room insert our [SleepNight] parameter [night] into the database.
     *
     * @param night the [SleepNight] to insert into the database.
     */
    private suspend fun insert(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    /**
     * Updates a [SleepNight] in the database. We launch a suspending lambda on the background
     * [CoroutineScope] of [Dispatchers.IO] which calls the `update` method of [database] to have
     * Room update the entry for our [SleepNight] parameter [night] in the database.
     *
     * @param night the [SleepNight] to insert into the database.
     */
    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }

    /**
     * Clears the database. We launch a suspending lambda on the background [CoroutineScope] of
     * [Dispatchers.IO] which calls the `clear` method of [database] to have Room clear all
     * entries in the database.
     */
    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    /**
     * Executes when the START button is clicked. This is because the android:id="@+id/start_button"
     * button in our layout file fragment_sleep_tracker.xml has the attribute:
     *
     * android:onClick="@{() -> sleepTrackerViewModel.onStart()}"
     *
     * We launch a lambda on the [CoroutineScope] of [uiScope] which initializes our [SleepNight]
     * variable `val newNight` with a new instance then calls our *suspending* method [insert] to
     * insert it into our database. We then set the *value* of [tonight] to the [SleepNight] which
     * our *suspending* method [getTonightFromDatabase] retrieves from the database (should be the
     * same on we just inserted of course).
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
     * Executes when the STOP button is clicked. This is because the android:id="@+id/stop_button"
     * button in our layout file fragment_sleep_tracker.xml has the attribute:
     *
     * android:onClick="@{() -> sleepTrackerViewModel.onStop()}"
     *
     * We launch a lambda on the [CoroutineScope] of [uiScope] which initializes our [SleepNight]
     * variable `val oldNight` with the *value* of [tonight] if it is not *null* (or returns from
     * the *launch* if it is *null*). We set the `endTimeMilli` field of `oldNight` to the current
     * system time, then call our *suspending* method [update] to update the `oldNight` entry in
     * the database. Finally we set the *value* of [_navigateToSleepQuality] to `oldNight` (this
     * will trigger navigation to [SleepQualityFragment] to allow the user to select a sleep quality
     * for this [SleepNight] due to an `Observer` of this field which is added in the `onCreateView`
     * method of [SleepTrackerFragment]).
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
     * Executes when the CLEAR button is clicked. This is because the android:id="@+id/clear_button"
     * button in our layout file fragment_sleep_tracker.xml has the attribute:
     *
     * android:onClick="@{() -> sleepTrackerViewModel.onClear()}"
     *
     * We launch a lambda on the [CoroutineScope] of [uiScope] which calls our *suspending* method
     * [clear] to clear all entries from our database, set the *value* of [tonight] to *null*, and
     * set the *value* of [_showSnackbarEvent] to *true* (this will trigger a `SnackBar` message
     * announcing the deletion of all data due to an `Observer` of this field which is added in the
     * `onCreateView` method of [SleepTrackerFragment]).
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
     * Called when the ViewModel is dismantled.
     * At this point, we want to cancel all coroutines;
     * otherwise we end up with processes that have nowhere to return to
     * using memory and resources.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}