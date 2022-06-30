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

package com.example.android.devbyteviewer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.android.devbyteviewer.database.VideosDatabase
import com.example.android.devbyteviewer.database.asDomainModel
import com.example.android.devbyteviewer.domain.DevByteVideo
import com.example.android.devbyteviewer.network.DevByteNetwork
import com.example.android.devbyteviewer.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Repository for fetching devbyte videos from the network and storing them on disk
 *
 * @param database the [VideosDatabase] we are to when we write to or read from the disk.
 */
class VideosRepository(private val database: VideosDatabase) {

    /**
     * [LiveData] wrapped list of [DevByteVideo] domain objects, which are converted from the list
     * of `DatabaseVideo` database objects returned by the `getVideos` method of the `videoDao`
     * Room DAO when it reads all the entries in the database.
     */
    val videos: LiveData<List<DevByteVideo>> = Transformations.map(database.videoDao.getVideos()) {
        it.asDomainModel()
    }

    /**
     * Refresh the videos stored in the offline cache from the network.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * We use the [withContext] method to call a suspending lambda block on the [Dispatchers.IO]
     * `CoroutineContext` which logs the fact that we were called, then initializes the variable
     * `val playlist` to the `NetworkVideoContainer` returned by the `getPlaylist` method of our
     * [DevByteNetwork] service `devbytes` (it returns a `Deferred` containing it so we call the
     * `await` method of that `Deferred` to suspend until the network access completes). We then
     * call the `insertAll` method of the `videoDao` Room DAO to insert `playlist` converted to
     * a list of `DatabaseVideo` database objects by the `asDatabaseModel` extension function.
     */
    suspend fun refreshVideos() {
        withContext(Dispatchers.IO) {
            Timber.d("refresh videos is called")
            val playlist = (DevByteNetwork.devbytes ?: return@withContext).getPlaylist().await()
            database.videoDao.insertAll(playlist.asDatabaseModel())
        }
    }
}
