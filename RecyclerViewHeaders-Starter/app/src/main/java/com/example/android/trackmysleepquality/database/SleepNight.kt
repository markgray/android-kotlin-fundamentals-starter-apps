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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents one night's sleep through start, end times, and the sleep quality.
 */
@Entity(tableName = "daily_sleep_quality_table")
data class SleepNight(
    /**
     * The PrimaryKey of our table, must be unique so we have room automatically generate it.
     */
    @PrimaryKey(autoGenerate = true)
    var nightId: Long = 0L,

    /**
     * The start time of the [SleepNight] entry, it is set when the START button is clicked.
     */
    @ColumnInfo(name = "start_time_milli")
    var startTimeMilli: Long = System.currentTimeMillis(),

    /**
     * The stop time of the [SleepNight] entry, it is set when the STOP button is clicked.
     */
    @ColumnInfo(name = "end_time_milli")
    var endTimeMilli: Long = startTimeMilli,

    /**
     * The subjective quality rating of the night's sleep, it is set by clicking one of the
     * icons in the SleepQualityFragment. The minus 1 initial value denotes a "sleep in
     * progress" state.
     */
    @ColumnInfo(name = "quality_rating")
    var sleepQuality: Int = -1
)
