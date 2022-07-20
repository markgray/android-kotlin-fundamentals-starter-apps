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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding

/**
 * The adapter we use for the [RecyclerView] with resource ID R.id.sleep_list in the layout file
 * layout/fragment_sleep_tracker.xml which displays the [SleepNight] records read from our database.
 * This class implements a [ListAdapter] for [RecyclerView]  which uses Data Binding to present
 * [List] data, including computing diffs between lists. Note that our [ListAdapter] super class
 * indirectly holds our dataset and we need to retrieve items using its `get` method rather than
 * directly from our [SleepTrackerViewModel] dataset field `nights`. An observer of `nights` calls
 * the `submitList` method of [ListAdapter] with `nights` to have it diffed and displayed whenever
 * the `LiveData` list of [SleepNight] changes.
 *
 * @param clickListener the [SleepNightListener] each item view binding in our [RecyclerView] should
 * use as its `clickListener` variable. The binding expression for the "android:onClick" attribute
 * of the `ConstraintLayout` holding all the views of the layout/list_item_sleep_night.xml layout
 * file calls the `onClick` method of its `clickListener` variable with its `sleep` variable (the
 * [SleepNight] it displays).
 */
@Suppress("MemberVisibilityCanBePrivate")
class SleepNightAdapter(val clickListener: SleepNightListener
) : ListAdapter<SleepNight, SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()) {

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder] `itemView` to reflect the item at the given position.
     *
     * We call the `bind` method of our [ViewHolder] parameter [holder] to have it bind the
     * [ViewHolder] to the  [SleepNight] returned  by the [ListAdapter.getItem] method for our
     * [Int] parameter [position], as well as our [SleepNightListener] field [clickListener].
     * It will set the `sleep` variable of its [ListItemSleepNightBinding] to its [SleepNight]
     * parameter and the `clickListener` variable to its [SleepNightListener] parameter then call
     * the `executePendingBindings` of that binding to have it update the view held by the
     * [ViewHolder] to reflect the changes to its variables.
     *
     * @param holder The [ViewHolder] which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        @Suppress("UNUSED_VARIABLE")
        val item = getItem(position)
        holder.bind(getItem(position) ?: return, clickListener)
    }

    /**
     * Called when [RecyclerView] needs a new [ViewHolder] of the given type to represent an item.
     * We just return the [ViewHolder] returned by the [ViewHolder.from] factory method for our
     * [ViewGroup] parameter [parent].
     *
     * @param parent The [ViewGroup] into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     * @return A new [ViewHolder] that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    /**
     * The ViewHolder constructor takes the binding variable from the associated layout file
     * layout/list_item_sleep_night.xml, which nicely gives it access to the full [SleepNight]
     * information display just by setting its `sleep` variable.
     *
     * @param binding the [ListItemSleepNightBinding] for the view we are to display our item in
     */
    class ViewHolder private constructor(
        val binding: ListItemSleepNightBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds this [ViewHolder] instance to its [SleepNight] parameter [item] by setting the
         * `sleep` variable of our [ListItemSleepNightBinding] field [binding] to [item] and the
         * `clickListener` variable to [clickListener] then calling the `executePendingBindings`
         * method of [binding] to have it evaluate the pending binding, updating any Views that
         * have expressions bound to the modified variables.
         *
         * @param item the [SleepNight] whose information that we are to display.
         * @param clickListener the [SleepNightListener] whose `onClick` method should be called
         * with our [SleepNight] variable `sleep` when our item view is clicked.
         */
        fun bind(item: SleepNight, clickListener: SleepNightListener) {
            binding.sleep = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            /**
             * Factory method to create a new instance of [ViewHolder]. We initialize our variable
             * `val layoutInflater` with the [LayoutInflater] for the context of our [ViewGroup]
             * parameter [parent]. Then we initialize our [ListItemSleepNightBinding] variable
             * `val binding` by having the [ListItemSleepNightBinding.inflate] use `layoutInflater`
             * to inflate its associated layout file layout/list_item_sleep_night.xml with [parent]
             * supplying the layout params. Finally we return a new instance of [ViewHolder] which
             * is constructed to use `binding` as its [ListItemSleepNightBinding].
             *
             * @param parent The [ViewGroup] into which the new View will be added after it is bound
             * to an adapter position.
             */
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

/**
 * Callback for calculating the diff between two non-null [SleepNight] objects in our list.
 * It is used to calculate updates for our RecyclerView [SleepNightAdapter]
 */
class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>() {

    /**
     * Called to check whether two objects represent the same item. For example, if your items have
     * unique ids, this method should check their id equality. We just return the result of comparing
     * the primary key `nightId` property of the two [SleepNight] parameters for equality.
     *
     * @param oldItem The [SleepNight] in the old list.
     * @param newItem The [SleepNight] in the new list.
     * @return `true` if the two items represent the same object or `false` if they are different.
     */
    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem.nightId == newItem.nightId
    }

    /**
     * Called to check whether two items have the same data. This information is used to detect if
     * the contents of an item have changed. We just return the result of comparing our two [SleepNight]
     * parameters for equality.
     *
     * @param oldItem The item in the old list.
     * @param newItem The item in the new list.
     * @return `true` if the contents of the items are the same or `false` if they are different.
     */
    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem == newItem
    }
}

/**
 * This class is intended to be constructed with a lambda which will be called with the `nightId`
 * primary key of the [SleepNight] argument of its [SleepNightListener.onClick] method when the
 * method is called. [SleepNightListener.onClick] is called by a binding expression for the
 * "android:onClick" attribute of the `ConstraintLayout` holding all the views of the
 * layout/list_item_sleep_night.xml layout file with its [SleepNight] variable `sleep`.
 *
 * @param clickListener a function type which accepts the `nightId` primary key of a [SleepNight]
 * and returns `Unit`. The `onCreateView` override of `SleepTrackerFragment` uses a lambda when it
 * constructs its [SleepNightAdapter] which calls the `onSleepNightClicked` method of its
 * `SleepTrackerViewModel` field `sleepTrackerViewModel` with the `nightId`. `onSleepNightClicked`
 * sets the value of its `_navigateToSleepDetail` field to `nightId` which will trigger navigation
 * to the `SleepDetailFragment` to display the [SleepNight] details.
 */
class SleepNightListener(val clickListener: (sleepId: Long) -> Unit) {
    /**
     * Called by a binding expression for the "android:onClick" attribute of the `ConstraintLayout`
     * holding all the views of the layout/list_item_sleep_night.xml layout file with its [SleepNight]
     * variable `sleep`.
     *
     * @param night the [SleepNight] whose `nightId` primary key we are to pass as an argument to our
     * [clickListener] function type field (a lambda in our case).
     */
    fun onClick(night: SleepNight): Unit = clickListener(night.nightId)
}
