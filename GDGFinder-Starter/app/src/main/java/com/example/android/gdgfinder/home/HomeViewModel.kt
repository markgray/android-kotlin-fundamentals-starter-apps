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


package com.example.android.gdgfinder.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * [ViewModel] used to control the `HomeFragment` fragment. It consists of just one [LiveData]
 * wrapped [Boolean] property [_navigateToSearch] which is set to `true` by our [onFabClicked]
 * method (called by the binding expression value of the "android:onClick" attribute of the
 * `FloatingActionButton` widget in the layout/home_fragment.xml layout file), and which is set
 * to `false` by our [onNavigatedToSearch] method (called by the `Observer` lambda of the
 * [navigateToSearch] property after it navigates to the `GdgListFragment` fragment in response
 * to the property toggling to `true`).
 */
class HomeViewModel : ViewModel() {

    /**
     * This private property is used to trigger navigation to the `GdgListFragment` by an `Observer`
     * of its publicly exposed immutable "getter" [navigateToSearch]. Our method [onFabClicked] is
     * used to set it to `true`, and our method [onNavigatedToSearch] is used to set it to `false`.
     */
    private val _navigateToSearch = MutableLiveData<Boolean>()

    /**
     * Users interested in the state of [_navigateToSearch] should use this to get immutable access
     * to it. An `Observer` is set on it in the `onCreateView` override of `HomeFragment` whose
     * lambda will trigger navigation to the `GdgListFragment` fragment when it transitions to
     * `true`.
     */
    val navigateToSearch: LiveData<Boolean>
        get() = _navigateToSearch

    /**
     * Sets our private [MutableLiveData] wrapped [Boolean] property [_navigateToSearch] to `true`.
     * It is called by a binding expression value of the "android:onClick" attribute of the
     * `FloatingActionButton` widget in the layout/home_fragment.xml layout file.
     */
    fun onFabClicked() {
        _navigateToSearch.value = true
    }

    /**
     * Sets our private [MutableLiveData] wrapped [Boolean] property [_navigateToSearch] to `false`.
     * It is called by the `Observer` lambda of the [navigateToSearch] property created in the
     * `onCreateView` override of `HomeFragment` after it navigates to the `GdgListFragment`
     * fragment in response to the property toggling to `true`
     */
    fun onNavigatedToSearch() {
        _navigateToSearch.value = false
    }
}