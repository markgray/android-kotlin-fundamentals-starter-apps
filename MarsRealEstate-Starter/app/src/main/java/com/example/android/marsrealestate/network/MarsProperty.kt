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
 *
 */

package com.example.android.marsrealestate.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

/**
 * This data class defines a Mars property which includes an ID, the image URL, the type (sale
 * or rental) and the price (monthly if it's a rental). The property names of this data class
 * are used by Moshi to match the names of values in JSON, and when the name of the JSON field
 * is different a `@Json` annotation provides the JSON "name"
 *
 * @param id a unique ID number [String].
 * @param imgSrcUrl the URL to a NASA jpg of the "property", JSON name is "img_src".
 * @param type one of the strings "rent" or "buy".
 * @param price the "price" of the property in dollars.
 */
@Parcelize
data class MarsProperty(
    val id: String,
    // used to map img_src from the JSON to imgSrcUrl in our class
    @Json(name = "img_src") val imgSrcUrl: String,
    val type: String,
    val price: Double) : Parcelable {
    /**
     * Returns `true` if the [type] of this property is "rent".
     */
    val isRental: Boolean
        get() = type == "rent"
}
