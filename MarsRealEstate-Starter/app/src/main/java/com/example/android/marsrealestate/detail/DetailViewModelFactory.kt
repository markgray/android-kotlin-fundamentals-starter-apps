/*
 *  Copyright 2019, The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.android.marsrealestate.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.marsrealestate.network.MarsProperty

/**
 * Simple ViewModel factory that provides the MarsProperty and context to the ViewModel.
 */
class DetailViewModelFactory(
    private val marsProperty: MarsProperty,
    private val application: Application) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given [Class]. We perform a sanity check to make sure that
     * our [Class] parameter [modelClass] is either the same as, or is a superclass or superinterface
     * of [DetailViewModel] and throw [IllegalArgumentException] if it is not. Otherwise we return
     * a new instance of [DetailViewModel] constructed to display our [MarsProperty] field
     * [marsProperty] using our [Application] field [application] to access any resources it might
     * need.
     *
     * @param modelClass a [Class] whose instance is requested
     * @param T          The type parameter for the ViewModel.
     * @return a newly created [DetailViewModel]
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // The above if statement checks the cast
            return DetailViewModel(marsProperty, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
