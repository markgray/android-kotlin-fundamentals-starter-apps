/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.dessertclicker

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber

/**
 * This is a class representing a timer that you can start or stop. The secondsCount outputs a
 * count of how many seconds since it started, once a second.
 *
 * -----
 *
 * Handler and Runnable are beyond the scope of this lesson. This is in part because they deal with
 * threading, which is a complex topic that will be covered in a later lesson.
 *
 * If you want to learn more now, you can take a look on the Android Developer documentation on
 * threading:
 *
 * https://developer.android.com/guide/components/processes-and-threads
 *
 */
class DessertTimer(lifecycle: Lifecycle) : DefaultLifecycleObserver {

    /**
     * The number of seconds counted since the timer started
     */
    var secondsCount: Int = 0

    /**
     * [Handler] is a class meant to process a queue of messages (known as [android.os.Message]s)
     * or actions (known as [Runnable]s)
     */
    private var handler = Handler(Looper.myLooper()!!)

    /**
     * This [Runnable] is a lambda created in our [onStart] override which increments [secondsCount]
     * then logs the timer value and re-adds itself to the queue of the [Handler] field [handler]
     * with a delay of 1000 milliseconds.
     */
    private lateinit var runnable: Runnable

    init {
        /**
         * Add this as a lifecycle Observer, which allows for the class to react to changes in this
         * activity's lifecycle state using `OnLifecycleEvent` annotations on methods.
         */
        lifecycle.addObserver(this)
    }

    /**
     * The `OnLifecycleEvent` annotation for the [Lifecycle.Event.ON_START] event causes this method
     * to be run when the [Lifecycle] we are observing emits an `onStart` event. We initialize our
     * [Runnable] field [runnable] with a new instance whose `run` override is a lambda which
     * increments our field [secondsCount], logs the timer value, then re-adds [runnable] to the
     * queue of our [Handler] field [handler] with a delay of 1000 milliseconds. Finally we add
     * [runnable] to the queue of our [Handler] field [handler] with a delay of 1000 milliseconds
     * to start the timer running.
     */
    override fun onStart(owner: LifecycleOwner) {
        // Create the runnable action, which prints out a log and increments the seconds counter
        runnable = Runnable {
            secondsCount++
            Timber.i("Timer is at : $secondsCount")
            // postDelayed re-adds the action to the queue of actions the Handler is cycling
            // through. The delayMillis param tells the handler to run the runnable in
            // 1 second (1000ms)
            handler.postDelayed(runnable, 1000)
        }

        // This is what initially starts the timer
        handler.postDelayed(runnable, 1000)

        // Note that the Thread the handler runs on is determined by a class called Looper.
        // In this case, no looper is defined, and it defaults to the main or UI thread.
    }

    /**
     * The `OnLifecycleEvent` annotation for the [Lifecycle.Event.ON_STOP] event causes this method
     * to be run when the [Lifecycle] we are observing emits an `onStop` event. We removed all
     * pending posts of [Runnable] field [runnable] from our [Handler] field [handler] stopping
     * the timer until the next time we receive a [Lifecycle.Event.ON_START] event and our
     * [onStart] override is run.
     */
    override fun onStop(owner: LifecycleOwner) {
        // Removes all pending posts of runnable from the handler's queue, effectively stopping the
        // timer
        handler.removeCallbacks(runnable)
    }
}
