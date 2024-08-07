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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.trackmysleepquality.database.SleepDatabaseDao

/**
 * This is pretty much boiler plate code for a ViewModel Factory.
 *
 * Provides the key for the night and the SleepDatabaseDao to the ViewModel.
 *
 * @param sleepNightKey the `nightId` primary key of the `SleepNight` we are interested in.
 * @param dataSource the [SleepDatabaseDao] the [SleepQualityViewModel] should use to access the
 * Room database.
 */
class SleepQualityViewModelFactory(
    private val sleepNightKey: Long,
    private val dataSource: SleepDatabaseDao
) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given [Class].
     *
     * @param modelClass a [Class] whose instance is requested
     * @param T          The type parameter for the [ViewModel].
     * @return a newly created [SleepQualityViewModel] constructed to use [sleepNightKey] as the
     * primary key to the `SleepNight` of interest, and to use [dataSource] as the [SleepDatabaseDao]
     * to access the Room database.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepQualityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // It is checked by above if statement
            return SleepQualityViewModel(sleepNightKey, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}