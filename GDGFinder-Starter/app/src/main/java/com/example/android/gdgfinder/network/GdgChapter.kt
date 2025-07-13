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


package com.example.android.gdgfinder.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

/**
 *  Moshi parses the JSON objects in the "data" array of our JSON file into a list of these kotlin
 * objects. The @Json annotation supplies the JSON field name when it differs from the kotlin one.
 *
 * @param name The name of the GDG Chapter: "GDG Bordj Bou-Arréridj"
 * @param city The city that the Chapter is in: "Burj Bu Arririj"
 * @param country Country that the Chapter is in: "Algeria"
 * @param region Region that the Chapter is in: "Africa"
 * @param website Website URL for the Chapter: "https://www.meetup.com/GDG-BBA"
 * @param geo The [LatLong] latitude and longitude of the Chapter:
 *
 *     "geo": {
 *         "lat": 36.06000137,
 *         "lng": 4.630000114
 *     }
 */
@Parcelize
data class GdgChapter(
    /**
     * The name of the GDG Chapter: "GDG Bordj Bou-Arréridj"
     */
    @param:Json(name = "chapter_name") val name: String,
    /**
     * The city that the Chapter is in: "Burj Bu Arririj"
     */
    @param:Json(name = "cityarea") val city: String,
    /**
     * Country that the Chapter is in: "Algeria"
     */
    val country: String,
    /**
     * Region that the Chapter is in: "Africa"
     */
    val region: String,
    /**
     * Website URL for the Chapter: "https://www.meetup.com/GDG-BBA"
     */
    val website: String,
    /**
     *The [LatLong] latitude and longitude of the Chapter:
     *
     *     "geo": {
     *         "lat": 36.06000137,
     *         "lng": 4.630000114
     *     }
     */
    val geo: LatLong
) : Parcelable

/**
 * This class is the class Moshi parses the contents of the "geo" field's JSON object into.
 *
 * @param lat the latitude of the location
 * @param long the longitude of the location
 */
@Parcelize
data class LatLong(
    /**
     * the latitude of the location
     */
    val lat: Double,
    /**
     * the longitude of the location
     */
    @param:Json(name = "lng")
    val long: Double
) : Parcelable

/**
 * This class holds both the parsed "filters_" JSON array (a [List] of [String]) and the parsed
 * "data" JSON array (a [List] of [GdgChapter] objects) that are parsed from the JSON file when the
 * `getChapters` method of `GdgApiService` is called.
 *
 * @param filters the [Filter] list of region strings
 * @param chapters the [List] of [GdgChapter] objects parsed from the JSON
 */
@Parcelize
data class GdgResponse(
    /**
     * the [Filter] list of region strings
     */
    @param:Json(name = "filters_") val filters: Filter,
    /**
     * the [List] of [GdgChapter] objects parsed from the JSON
     */
    @param:Json(name = "data") val chapters: List<GdgChapter>
) : Parcelable

/**
 * Moshi parses the "region" array of the JSON object "filters_" into this kotlin class.
 *
 * @param regions the list of region strings parsed from the JSON
 */
@Parcelize
data class Filter(
    /**
     * the list of region strings parsed from the JSON
     */
    @param:Json(name = "region") val regions: List<String>
) : Parcelable

//"chapter_name": "GDG Bordj Bou-Arréridj",
//"cityarea": "Burj Bu Arririj",
//"country": "Algeria",
//"region": "Africa",
//"website": "https://www.meetup.com/GDG-BBA",
//"geo": {
//    "lat": 36.06000137,
//    "lng": 4.630000114
//}