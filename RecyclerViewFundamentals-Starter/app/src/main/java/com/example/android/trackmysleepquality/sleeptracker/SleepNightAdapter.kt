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

package com.example.android.trackmysleepquality.sleeptracker

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight

/**
 * The adapter we use for the [RecyclerView] with resource ID R.id.sleep_list in the layout file
 * layout/fragment_sleep_tracker.xml which displays the [SleepNight] records read from our database.
 */
@Suppress("MemberVisibilityCanBePrivate")
class SleepNightAdapter : RecyclerView.Adapter<SleepNightAdapter.ViewHolder>() {

    /**
     * Our dataset of [SleepNight] records read from our database. It is set by an `Observer` added
     * to the `nights` property of `SleepTrackerViewModel` in the `onCreateView` override of
     * `SleepTrackerFragment`, and so it gets set whenever that `LiveData` wrapped property gets
     * updated by the `getAllNights` method of the `SleepDatabaseDao`.
     *
     * Note that the `set` method uses the `field` identifier in order to set the backing field,
     * then calls the [notifyDataSetChanged] method to notify any registered observers that the
     * data set has changed.
     */
    var data: List<SleepNight> = listOf()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Returns the total number of items in the data set held by the adapter. We just return the
     * size of our [data] field.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int = data.size

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given position.
     * We initialize our [SleepNight] variable `val item` by retrieving the [SleepNight] at the
     * position [position] in our [List] of [SleepNight] dataset [data]. Then we call the `bind`
     * method of our [ViewHolder] parameter [holder] to have it update the views it holds with the
     * [SleepNight] data in `item`.
     *
     * @param holder The [ViewHolder] which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent an item.
     * We just return the result of calling the [ViewHolder.from] static method with our
     * [ViewGroup] parameter [parent].
     *
     * @param parent The [ViewGroup] into which the new [View] will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new [View].
     * @return A new [ViewHolder] that holds a [View] of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    /**
     * Our custom [RecyclerView.ViewHolder] designed to hold and display a [SleepNight].
     *
     * @param itemView the inflated [View] that we are to hold
     */
    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         * The [TextView] where we display the sleep duration.
         */
        val sleepLength: TextView = itemView.findViewById(R.id.sleep_length)

        /**
         * The [TextView] where we display a string describing the quality of sleep.
         */
        val quality: TextView = itemView.findViewById(R.id.quality_string)

        /**
         * The [ImageView] where we display the sleep quality icon.
         */
        val qualityImage: ImageView = itemView.findViewById(R.id.quality_image)

        /**
         * Binds `this` [ViewHolder] to the [SleepNight] parameter [item]. First we initialize our
         * [Resources] variable `val res` to a [Resources] instance for the application's package
         * retrieved from the context that our [View] field [itemView] is running in. We set the
         * text of our [TextView] field [sleepLength] to the formatted [String] that our method
         * [convertDurationToFormatted] creates from the difference between the `endTimeMilli`
         * field and the `startTimeMilli` field of our [SleepNight] parameter [item]. We set the
         * text of our [TextView] field [quality] to the formatted [String] describing the quality
         * of sleep based on the value of the `sleepQuality` field of our [SleepNight] parameter
         * [item] that our method [convertNumericQualityToString] creates. Finally we set one of
         * seven emoticon drawables as the content of our [ImageView] field [qualityImage] based
         * on the value of the `sleepQuality`
         *
         * @param item the [SleepNight] we are to display.
         */
        fun bind(item: SleepNight) {
            val res: Resources = itemView.context.resources
            sleepLength.text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
            quality.text = convertNumericQualityToString(item.sleepQuality, res)
            qualityImage.setImageResource(when (item.sleepQuality) {
                0 -> R.drawable.ic_sleep_0
                1 -> R.drawable.ic_sleep_1
                2 -> R.drawable.ic_sleep_2
                3 -> R.drawable.ic_sleep_3
                4 -> R.drawable.ic_sleep_4
                5 -> R.drawable.ic_sleep_5
                else -> R.drawable.ic_sleep_active
            })
        }

        companion object {
            /**
             * Factory method for a [ViewHolder] instance. We initialize our [LayoutInflater]
             * variable `val layoutInflater` to the [LayoutInflater] for the context of our
             * [ViewGroup] parameter [parent]. Then use `layoutInflater` to inflate our layout
             * file R.layout.list_item_sleep_night using [parent] for its LayoutParams in order
             * to initialize our [View] variable `val view`. Finally we return a new instance of
             * [ViewHolder] constructed to use `view` as its item view.
             *
             * @param parent the [ViewGroup] into which the new [View] will be added after it is
             * bound to an adapter position.
             */
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.list_item_sleep_night, parent, false)

                return ViewHolder(view)
            }
        }
    }
}