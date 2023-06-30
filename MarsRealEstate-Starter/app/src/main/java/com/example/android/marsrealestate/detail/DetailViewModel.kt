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
import androidx.lifecycle.*
import com.example.android.marsrealestate.R
import com.example.android.marsrealestate.network.MarsProperty

/**
 * The [ViewModel] that is associated with the [DetailFragment].
 *
 * @param marsProperty the [MarsProperty] whose details we are to display
 * @param app the [Application] we are associated with, which we use to access resources
 */
class DetailViewModel(
    marsProperty: MarsProperty,
    app: Application
) : AndroidViewModel(app) {

    /**
     * The internal MutableLiveData that contains the [MarsProperty] we are to display
     */
    private val _selectedProperty = MutableLiveData<MarsProperty>()

    /**
     * The external immutable LiveData for the [MarsProperty] we are to display
     */
    val selectedProperty: LiveData<MarsProperty>
        get() = _selectedProperty

    /**
     * We just initialize the value of our `_selectedProperty` field to our constructor's
     * `MarsProperty` parameter `marsProperty`
     */
    init {
        _selectedProperty.value = marsProperty
    }

    /**
     * Transformation of the `isRental` [Boolean] property of our field [selectedProperty] and its
     * `price` field to an appropriate string for displaying the rental or sale price. Used by
     * the android:id="@+id/price_value_text" `TextView` in our fragment_detail.xml layout file
     * to specify the text displayed using an android:text="@{viewModel.displayPropertyPrice}"
     * attribute.
     */
    val displayPropertyPrice: LiveData<String> = selectedProperty.map {
        app.applicationContext.getString(
            when (it.isRental) {
                true -> R.string.display_price_monthly_rental
                false -> R.string.display_price
            }, it.price)
    }

    /**
     * Transformation of the `isRental` [Boolean] property of our field [selectedProperty] and its
     * `price` field to either the string "Rent" (for a rental property) or "Sale". Used by the
     * android:id="@+id/property_type_text" `TextView` in our fragment_detail.xml layout file
     * to specify the text displayed using an android:text="@{viewModel.displayPropertyType}"
     * attribute.
     */
    val displayPropertyType: LiveData<String> = selectedProperty.map {
        app.applicationContext.getString(R.string.display_type,
            app.applicationContext.getString(
                when (it.isRental) {
                    true -> R.string.type_rent
                    false -> R.string.type_sale
                }))
    }
}
