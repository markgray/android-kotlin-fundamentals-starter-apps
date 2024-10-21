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

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

/**
 * The original URL was:
 *
 *      private const val BASE_URL = "https://developers.google.com/community/gdg/groups/"
 *
 *  and the relative URL for this was:
 *
 *      @GET("directory.json")
 *
 * But this crashes so we use the alternative URL which is for a server with a recent snapshot:
 *
 *     "https://android-kotlin-fun-mars-server.appspot.com/"
 *
 *  and the relative URL:
 *
 *      @GET("gdg-directory.json")
 */
private const val BASE_URL = "https://android-kotlin-fun-mars-server.appspot.com/"
//private const val BASE_URL = "https://developers.google.com/community/gdg/directory/"

/**
 * A retrofit service to fetch and parse the JSON list of Google Development Groups into [GdgResponse]
 * objects for display.
 */
@Suppress("DeferredIsResult") // A rose is a rose is a rose
interface GdgApiService {
    /**
     * Called from `GdgChapterRepository` in order to fetch the JSON list of Google Development
     * Groups from the network, parsed into a [GdgResponse] (which contains a `Filter` field and
     * a list of [GdgChapter] objects field).
     */
    @GET("gdg-directory.json")
    //@GET("directory.json")
    fun getChapters():
        /**
         * The Coroutine Call Adapter allows us to return a [Deferred], a Job with a result
         */
        Deferred<GdgResponse>
}

/**
 * The [Moshi] instance we use to coordinate binding between JSON values and Java objects. We
 * create an instance of [Moshi.Builder] and add a [KotlinJsonAdapterFactory] instance to convert
 * our Kotlin classes to and from JSON, then `build` that `Builder` in order to create a [Moshi]
 * instance to initialize our variable `val moshi`.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * The [Retrofit] instance we use to access the network. We construct an instance of [Retrofit.Builder],
 * add the converter factory for serialization and deserialization of objects that the `create`
 * method of [MoshiConverterFactory] creates from our [Moshi] variable [moshi], add a
 * [CoroutineCallAdapterFactory] call adapter factory for supporting the return of [Deferred] return
 * types from our service methods, set the API base URL to [BASE_URL] and then `build` that `Builder`
 * in order to create a [Retrofit] instance to initialize our variable `val retrofit`.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

/**
 * Main entry point for network access. Call like `GdgApi.retrofitService.getChapters()`. It is
 * used by the `GdgListViewModel` to access its `retrofitService` field when it constructs the
 * `GdgChapterRepository` instance for its field `repository`.
 */
object GdgApi {
    /**
     * This is the singleton [GdgApiService] implementation created by [Retrofit].
     */
    val retrofitService: GdgApiService by lazy { retrofit.create(GdgApiService::class.java) }
}
