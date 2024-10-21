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


package com.example.android.gdgfinder.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * [ViewModel] "controlling" our `AddGdgFragment` fragment.
 */
class AddGdgViewModel : ViewModel() {

    /**
     * Request a `Snackbar` by setting this value to true. This is private because we don't want to
     * expose setting this value to the Fragment, the public read only version is our property
     * [showSnackBarEvent].
     */
    private var _showSnackbarEvent = MutableLiveData<Boolean?>()

    /**
     * If this is `true`, an `Observer` in `AddGdgFragment` will immediately `show()` a `Snackbar`
     * and then call `doneShowingSnackbar()` to reset it to `null`.
     */
    val showSnackBarEvent: LiveData<Boolean?>
        get() = _showSnackbarEvent

    /**
     * Call this immediately after calling `show()` on the `Snackbar` shown after [showSnackBarEvent]
     * toggles to `true`. It will clear the `Snackbar` request, so if the user rotates their phone
     * it won't show a duplicate `Snackbar`.
     */
    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = null
    }

    /**
     * Triggers the showing of a `Snackbar` by setting our [_showSnackbarEvent] property to `true`.
     * It is called by a binding expression for the "android:onClick" attribute of the button with
     * ID `R.id.button` (binding property `button`) in the layout/add_gdg_fragment.xml layout file.
     */
    fun onSubmitApplication() {
        _showSnackbarEvent.value = true
    }
}
