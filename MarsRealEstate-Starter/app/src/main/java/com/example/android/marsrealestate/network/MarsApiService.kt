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

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * The values of this enum are used to append a "Query" parameter to the URL. Which [MarsApiFilter]
 * to use is selected in the `onOptionsItemSelected` override of `OverViewFragment` when the user
 * uses the option menu to select a filter. The `updateFilter` method of `OverviewViewModel` is
 * then called with the new [MarsApiFilter] to have the `getMarsRealEstateProperties` method reload
 * the [List] of [MarsProperty] from the internet using the new filter as the query.
 */
enum class MarsApiFilter(
    /**
     * The [String] that is the value of the enum constant.
     */
    val value: String) {
    /**
     * Causes the `getMarsRealEstateProperties` method to reload the [List] of [MarsProperty] from
     * the internet with only properties which can be rented.
     */
    SHOW_RENT("rent"),

    /**
     * Causes the `getMarsRealEstateProperties` method to reload the [List] of [MarsProperty] from
     * the internet with only properties which can be bought.
     */
    SHOW_BUY("buy"),

    /**
     * Causes the `getMarsRealEstateProperties` method to reload the [List] of [MarsProperty] from
     * the internet with all properties.
     */
    SHOW_ALL("all")
}

/**
 * The API base URL for our Retrofit query.
 */
private const val BASE_URL = " https://android-kotlin-fun-mars-server.appspot.com/"

/**
 * The [Moshi] instance we use to create a converter factory for serialization and deserialization
 * of objects in our Retrofit query. It uses a `KotlinJsonAdapter` to encode Kotlin classes using
 * their property names as keys to the Json object, and the values of the Json object are assigned
 * to the corresponding property in the Kotlin class when the Json is converted.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * The Refrofit instance we use for our queries. It uses a converter factory created from our field
 * [moshi] to make a `KotlinJsonAdapter` when it needs to convert Json to [MarsProperty] instances,
 * uses a `CoroutineCallAdapterFactory` to use a Co-routine service method return type rather than
 * a CallBack one, and uses [BASE_URL] as its base url.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

/**
 * The interface we use to fetch the [List] of [MarsProperty] objects from the REST service.
 */
@Suppress("DeferredIsResult")
interface MarsApiService {
    /**
     * Makes a GET request of our Retrofit service with the "filter" [type] added to the end of
     * the [BASE_URL] of the query.
     *
     * @param type The "filter" value to be added to the query
     * @return a [Deferred] list of [MarsProperty] objects retrieved from the REST server.
     */
    @GET("realestate")
    fun getProperties(@Query("filter") type: String):
        Deferred<List<MarsProperty>>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object MarsApi {
    /**
     * The lazy-initialized [MarsApiService] Retrofit service that is an implementation of the API
     * endpoints defined by the service interface in this [MarsApiService]
     */
    val retrofitService: MarsApiService by lazy { retrofit.create(MarsApiService::class.java) }
}