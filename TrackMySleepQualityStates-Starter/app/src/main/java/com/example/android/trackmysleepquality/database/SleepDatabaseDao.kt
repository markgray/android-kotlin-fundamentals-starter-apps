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

package com.example.android.trackmysleepquality.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * Defines methods for using the [SleepNight] class with Room. The `@Dao` annotation marks the class
 * as a Data Access Object. Data Access Objects are the main classes where you define your database
 * interactions. They can include a variety of query methods. The class marked with `@Dao` should
 * either be an interface or an abstract class. At compile time, Room will generate an implementation
 * of this class when it is referenced by a Database. An abstract `@Dao` class can optionally have a
 * constructor that takes a Database as its only parameter. It is recommended to have multiple Dao
 * classes in your codebase depending on the tables they touch.
 */
@Dao
interface SleepDatabaseDao {

    /**
     * The `@Insert` annotation marks this as an insert method. It just inserts its parameter into
     * the database. Called by the `insert` method of `SleepTrackerViewModel`, which is called by
     * its `onStartTracking` method, which is called by a binding expression for the "android:onClick"
     * attribute of the `Button` with ID R.id.start_button in the file layout/fragment_sleep_tracker.xml
     * which is the layout file for `SleepTrackerFragment`.
     *
     * @param night the [SleepNight] to insert into the database.
     */
    @Insert
    fun insert(night: SleepNight)

    /**
     * The `@Update` annotation marks this as an update method. The implementation of the method will
     * update its parameter in the database if it already exists (checked by primary key). If it
     * doesn't already exist, this method will not change the database. Called by the `onSetSleepQuality`
     * method of `SleepQualityViewModel`, which is called by binding expressions for the "android:onClick"
     * attribute of 6 `ImageView` widgets in the layout file layout/fragment_sleep_quality.xml. Also
     * called by the `update` method of `SleepTrackerViewModel` which is called by its `onStop` method,
     * which executes when the "STOP" button is clicked thanks to a binding expression for the
     * "android:onClick" attribute of that button.
     *
     * @param night [SleepNight] row value to update.
     */
    @Update
    fun update(night: SleepNight)

    /**
     * The `@Query` annotation marks this as an query method. The value of the annotation includes
     * the query that will be run when this method is called. This query is verified at compile time
     * by Room to ensure that it compiles fine against the database. The arguments of the method will
     * be bound to the bind arguments in the SQL statement when they are prefixed by ":".
     *
     * Selects and returns the row that matches the supplied `nightId`, which is our primary key.
     * Called by the `onSetSleepQuality` method of `SleepQualityViewModel`, which is called by
     * binding expressions for the "android:onClick" attribute of 6 `ImageView` widgets in the
     * layout file layout/fragment_sleep_quality.xml.
     *
     * @param key `nightId` primary key to match.
     * @return the [SleepNight] whose `nightId` column primary key is our parameter [key].
     */
    @Query("SELECT * from daily_sleep_quality_table WHERE nightId = :key")
    fun get(key: Long): SleepNight?

    /**
     * The `@Query` annotation marks this as an query method. The value of the annotation includes
     * the query that will be run when this method is called.
     *
     * Deletes all values from the table. This does not delete the table, only its contents.
     * Called by the `clear` method of `SleepTrackerViewModel`, which is called by its `onClear`
     * method, which is called by a binding expression for the "android:onClick" attribute of the
     * `Button` with ID R.id.clear_button in the file layout/fragment_sleep_tracker.xml which is
     * the layout file for `SleepTrackerFragment`.
     */
    @Query("DELETE FROM daily_sleep_quality_table")
    fun clear()

    /**
     *  Selects and returns the latest night. Called by the `getTonightFromDatabase` method of
     * `SleepTrackerViewModel`, which is called by its `initializeTonight` method, which is called
     * in its `init` block, and by its `onStartTracking` method which is called by a binding
     * expression for the "android:onClick" attribute of the `Button` with ID R.id.start_button in
     * the file layout/fragment_sleep_tracker.xml (the layout file for `SleepTrackerFragment`)
     *
     * @return the newest [SleepNight] added to the database.
     */
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    fun getTonight(): SleepNight?

    /**
     * Selects and returns all rows in the table, sorted by `nightId` in descending order. Called by
     * the initializer for the `nights` field of `SleepTrackerViewModel`.
     *
     * @return a [LiveData] wrapped list of all of the [SleepNight] entries in our database sorted
     * by the `nightId` column primary key in descending order.
     */
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    fun getAllNights(): LiveData<List<SleepNight>>
}