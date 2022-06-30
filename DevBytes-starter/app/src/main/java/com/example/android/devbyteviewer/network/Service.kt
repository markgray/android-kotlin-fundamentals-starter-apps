/*
 * Copyright (C) 2019 Google Inc.
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

package com.example.android.devbyteviewer.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

// Since we only have one service, this can all go in one file.
// If you add more services, split this to multiple files and make sure to share the retrofit
// object between services.

/**
 * A retrofit service to fetch a devbyte playlist.
 */
@Suppress("DeferredIsResult")
interface DevbyteService {
    /**
     * Called from the `refreshVideos` method of `VideosRepository` to retrieve the list of videos
     * from the server. The GET("devbytes") annotation adds "devbytes" to the base Url of the
     * `devbytes` retrofit implementation of our interface in our [DevByteNetwork] object.
     *
     * @return a [Deferred] (non-blocking cancellable future) holding a [NetworkVideoContainer]
     * parsed from the Json retrieved from the server, its value is retrieved using its
     * `await` suspending function.
     */
    @GET("devbytes")
    fun getPlaylist(): Deferred<NetworkVideoContainer>
}

/**
 * Main entry point for network access. Call like `DevByteNetwork.devbytes.getPlaylist()`
 */
object DevByteNetwork {

    /**
     * Configure retrofit to parse JSON and use coroutines. We use an instance of [Retrofit.Builder]
     * to build a [Retrofit] instance with base URL "https://android-kotlin-fun-mars-server.appspot.com/",
     * a [MoshiConverterFactory] converter factory for serialization and deserialization of objects
     * from JSON, a CallAdapter.Factory for use with Kotlin coroutines (for supporting service method
     * return types other than `Call`).
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://android-kotlin-fun-mars-server.appspot.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    /**
     * This is the [DevbyteService] implementation created by retrofit.
     */
    @Suppress("HasPlatformType")
    val devbytes: DevbyteService? = retrofit.create(DevbyteService::class.java)

}


