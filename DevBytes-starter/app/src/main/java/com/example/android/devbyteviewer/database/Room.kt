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

package com.example.android.devbyteviewer.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * The Data Access Object we use to read and write to our Room database.
 */
@Dao
interface VideoDao {
    /**
     * Query method for reading all of the `DatabaseVideo` entries in our "databasevideo" table into
     * a [LiveData] of a `List` of `DatabaseVideo`. It is used by the initializer of the `videos`
     * field of `VideosRepository` where it is mapped from a `LiveData<List<DatabaseVideo>>` to
     * a `LiveData<List<DevByteVideo>>` using the `Transformations.map` function and the
     * `asDomainModel` extension of `List<DatabaseVideo>`.
     *
     * @return a [LiveData] list of all of the [DatabaseVideo] objects in our database
     */
    @Query("select * from databasevideo")
    fun getVideos(): LiveData<List<DatabaseVideo>>

    /**
     * Insert method for inserting a List of [DatabaseVideo] entities into our database, it uses
     * the [OnConflictStrategy.REPLACE] strategy to replace the old data if there is a conflict.
     * It is used by the `refreshVideos` method of `VideosRepository` to write a list of
     * `NetworkVideo` objects encapsulated in a `NetworkVideoContainer` to our database after it
     * converts the objects to `DatabaseVideo` entities.
     *
     * @param videos the `List` of [DatabaseVideo] objects we are to write to our database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(videos: List<DatabaseVideo>)
}

/**
 * Marks this class as a [RoomDatabase] with one table: [DatabaseVideo] and one DAO class to use to
 * access it, the [VideoDao] field [videoDao] (the table name is the default "databasevideo" since
 * the `@Entity` annotation lacks a "tableName" parameter).
 */
@Database(entities = [DatabaseVideo::class], version = 1, exportSchema = false)
abstract class VideosDatabase : RoomDatabase() {
    /**
     * The [VideoDao] to use to run "Room" queries.
     */
    abstract val videoDao: VideoDao
}

/**
 * Our cached [VideosDatabase] singleton instance, which is lazily built the first time our factory
 * method [getDatabase] is called.
 */
// It is sort of a const
private lateinit var INSTANCE: VideosDatabase

/**
 * Factory method to retrieve a singleton instance of [VideosDatabase] which is only built the first
 * time we are called. Synchronized on a Java Class instance corresponding to our [VideosDatabase]
 * KClass, we check if our lateinit field [INSTANCE] has not yet been initialized and if so we
 * initialize it to the [VideosDatabase] that a [Room.databaseBuilder] instance creates and
 * initializes whose name is "videos". Now that we know [INSTANCE] is initialized we return it to
 * the caller.
 *
 * @param context the [Context] of the application.
 * @return our singleton instance of [VideosDatabase]
 */
fun getDatabase(context: Context): VideosDatabase {
    synchronized(VideosDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                VideosDatabase::class.java,
                "videos").build()
        }
    }
    return INSTANCE
}