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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * View type of a [DataItem.Header] data item (the header at the beginning of our dataset).
 */
private const val ITEM_VIEW_TYPE_HEADER = 0

/**
 * View type of a [DataItem.SleepNightItem] data item (a real [SleepNight] in the `nights` list).
 */
private const val ITEM_VIEW_TYPE_ITEM = 1

/**
 * The adapter we use for the [RecyclerView] with resource ID R.id.sleep_list in the layout file
 * layout/fragment_sleep_tracker.xml which displays the [SleepNight] records read from our database,
 * as well as a "header" `TextView` displaying the string "Sleep Results" which occupies the entire
 * first row of the [RecyclerView].
 *
 * This class implements a [ListAdapter] for [RecyclerView]  which uses Data Binding to present
 * [List] data, including computing diffs between lists. Note that our [ListAdapter] super class
 * indirectly holds our dataset and we need to retrieve items using its `get` method rather than
 * directly from our [SleepTrackerViewModel] dataset field `nights`. An observer of `nights` calls
 * our [addHeaderAndSubmitList] method which prepends a [DataItem.Header] header item to the list
 * `nights` then calls the [submitList] method of [ListAdapter] with that list to have it diffed
 * and displayed whenever the `LiveData` list of [SleepNight] changes.
 *
 * @param clickListener the [SleepNightListener] each item view binding in our [RecyclerView] should
 * use as its `clickListener` variable. The binding expression for the "android:onClick" attribute
 * of the `ConstraintLayout` holding all the views of the layout/list_item_sleep_night.xml layout
 * file calls the `onClick` method of its `clickListener` variable with its `sleep` variable (the
 * [SleepNight] it displays).
 */
@Suppress("MemberVisibilityCanBePrivate")
class SleepNightAdapter(
    val clickListener: SleepNightListener
) : ListAdapter<DataItem, RecyclerView.ViewHolder>(SleepNightDiffCallback()) {

    /**
     * The [CoroutineScope] we use to launch a new coroutine in our [addHeaderAndSubmitList] method
     * which calls the [submitList] method of our [ListAdapter] super class using a suspend lambda
     * on the [Dispatchers.Main] `CoroutineDispatcher` ([submitList] needs to run on the UI thread).
     */
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    /**
     * Prepends a [DataItem.Header] data item to its [List] of [SleepNight] parameter [list] (after
     * converting each [SleepNight] in [list] to a [DataItem.SleepNightItem]) then submits that list
     * of [DataItem] to be diffed and displayed.
     *
     * We launch a new coroutine on the [adapterScope] `CoroutineScope` without blocking the current
     * thread. In the lambda of the coroutine we initialize our [List] of [DataItem] variable
     * `val items` to a list holding only a [DataItem.Header] if [list] is `null` or if it is not
     * `null` to a list holding a [DataItem.Header] to which we append a list containing the results
     * of constructing a [DataItem.SleepNightItem] from each of the [SleepNight] objects in our
     * parameter [list]. Then using the [Dispatchers.Main] coroutine context we start a suspending
     * lambda, suspending until it completes, which calls the [submitList] method of our [ListAdapter]
     * super class to submit `items` to be diffed and displayed.
     *
     * Called by an Observer of the `LiveData` wrapped list of [SleepNight]'s field `nights` of the
     * `SleepTrackerViewModel` view model which is added to `nights` in the `onCreateView` override
     * of `SleepTrackerFragment`.
     *
     * @param list the list of [SleepNight] entries read from our database.
     */
    fun addHeaderAndSubmitList(list: List<SleepNight>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + list.map { DataItem.SleepNightItem(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [RecyclerView.ViewHolder] `itemView` to reflect the item at the
     * given position. We check to see if our [RecyclerView.ViewHolder] parameter [holder] is an
     * instance of our custom [SleepNightAdapter.ViewHolder] subclass, and if it is we initialize
     * our [DataItem.SleepNightItem] variable `val nightItem` with the [DataItem] that our super's
     * [getItem] method returns for our [Int] parameter [position], then we call the `bind` method
     * of our [RecyclerView.ViewHolder] parameter [holder] to have it "bind" to the [SleepNight]
     * field `sleepNight` of `nightItem`, and to "bind" to our [SleepNightListener] field [clickListener]
     * (it sets the `sleep` variable of the [ListItemSleepNightBinding] it holds to its view to
     * the [SleepNight] we pass and the `clickListener` variable to the [SleepNightListener], then
     * calls the `executePendingBindings` method of its [ListItemSleepNightBinding] to have it
     * update the Views that have expressions bound to these modified variables).
     *
     * @param holder The [RecyclerView.ViewHolder] which should be updated to represent the contents
     * of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is ViewHolder -> {
                val nightItem = getItem(position) as DataItem.SleepNightItem
                holder.bind(nightItem.sleepNight, clickListener)
            }
        }
    }

    /**
     * Called when [RecyclerView] needs a new [ViewHolder] of the given type to represent an item.
     * When our [viewType] parameter is an [ITEM_VIEW_TYPE_HEADER] we return the [TextViewHolder]
     * returned by the [TextViewHolder.from] factory method when passed our [ViewGroup] parameter
     * [parent], and when our [viewType] parameter is an [ITEM_VIEW_TYPE_ITEM] we return the
     * [SleepNightAdapter.ViewHolder] returned by the [SleepNightAdapter.ViewHolder.from] factory
     * method when passed our [ViewGroup] parameter [parent] (they are both subclasses of
     * [RecyclerView.ViewHolder]). If [viewType] is neither of these we `throw` [ClassCastException].
     *
     * @param parent The [ViewGroup] into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     * @return A new [ViewHolder] that holds a View of the given view type.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    /**
     * Return the view type of the item at `position` for the purposes of view recycling.
     * The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. When the [DataItem] returned by our super's
     * [getItem] method is a [DataItem.Header] we return [ITEM_VIEW_TYPE_HEADER], and if
     * it is a [DataItem.SleepNightItem] we return [ITEM_VIEW_TYPE_ITEM].
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * `position`. Type codes need not be contiguous.
     */
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    /**
     * This is the [RecyclerView.ViewHolder] we use for the "Sleep Results" header which is the first
     * entry displayed by our [RecyclerView].
     *
     * @param view the [View] "itemView" that we display in, a `TextView` in our case.
     */
    class TextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            /**
             * Factory method for creating a [TextViewHolder]. We initialize our [LayoutInflater]
             * variable `val layoutInflater` to the [LayoutInflater] for the `Context` of our
             * [ViewGroup] parameter [parent], then use it to initialize our [View] variable
             * `val view` by having it inflate our layout file [R.layout.header] using [parent]
             * for the layout parameters into a [View] for its value. Finally we return a new
             * of [TextViewHolder] constructed to use `view` as its item view.
             *
             * @param parent the [ViewGroup] we should use for layout parameters.
             */
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.header, parent, false)
                return TextViewHolder(view)
            }
        }
    }

    /**
     * This is the [RecyclerView.ViewHolder] we use for the [SleepNight] entries in our dataset.
     *
     * @param binding the [ListItemSleepNightBinding] binding to the inflated layout file that the
     * [SleepNight] will be displayed in, its `root` field is the outermost View in the layout file
     * associated with the Binding and is our item view.
     */
    class ViewHolder private constructor(
        val binding: ListItemSleepNightBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds this [ViewHolder] to the [SleepNight] it is to display. We set the `sleep` variable
         * of our [binding] field to our [SleepNight] parameter [item], and its `clickListener`
         * variable to our [SleepNightListener] parameter `clickListener` then call the
         * `executePendingBindings` method of [binding] to have it evaluate the pending bindings,
         * updating any Views that have expressions bound to these modified variables.
         *
         * @param item the [SleepNight] object we are to display.
         * @param clickListener the [SleepNightListener] the item will use when it is clicked.
         */
        fun bind(item: SleepNight, clickListener: SleepNightListener) {
            binding.sleep = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            /**
             * Factory method for creating a [ViewHolder]. We initialize our [LayoutInflater]
             * variable `val layoutInflater` to the [LayoutInflater] for the `Context` of our
             * [ViewGroup] parameter [parent], then initialize our [ListItemSleepNightBinding]
             * variable `val binding` by having the [ListItemSleepNightBinding.inflate] method
             * use `layoutInflater` to inflate its associated layout file (layout/list_item_sleep_night.xml)
             * with [parent] supplying the layout params in order to produce the resulting binding
             * object. Finally we return a new instance of [ViewHolder] constructed to use `binding`
             * as its [ListItemSleepNightBinding] field `binding`.
             *
             * @param parent the [ViewGroup] we should use for layout parameters.
             */
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSleepNightBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }
    }
}

/**
 * The custom [DiffUtil.ItemCallback] that our [ListAdapter] super should use to evaluate changes to
 * the dataset that it holds. It is used to calculate updates for our RecyclerView.
 */
class SleepNightDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    /**
     * Called to check whether two objects represent the same item. For example, if your items have
     * unique ids, this method should check their id equality. We just return the result of comparing
     * the `id` property of our two [DataItem] parameters for equality.
     *
     * @param oldItem The [DataItem] in the old list.
     * @param newItem The [DataItem] in the new list.
     * @return `true` if the two items represent the same object or `false` if they are different.
     */
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    /**
     * Called to check whether two items have the same data. This information is used to detect if
     * the contents of an item have changed. We just return the result of comparing our two [DataItem]
     * parameters for equality.
     *
     * @param oldItem The item in the old list.
     * @param newItem The item in the new list.
     * @return `true` if the contents of the items are the same or `false` if they are different.
     */
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
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
     *
     */
    fun onClick(night: SleepNight): Unit = clickListener(night.nightId)
}

/**
 * The superclass of our two types of data items.
 */
sealed class DataItem {
    /**
     * The [DataItem] subclass which holds [SleepNight] objects, it overides its super's [id] field
     * and its constructor sets it to the `nightId` primary key of the [SleepNight] it is constructed
     * to hold.
     *
     * @param sleepNight the [SleepNight] we are intended to represent.
     */
    data class SleepNightItem(val sleepNight: SleepNight) : DataItem() {
        override val id: Long = sleepNight.nightId
    }

    /**
     * The [DataItem] subclass which holds our "Header" pseudo object, it overides its super's [id]
     * field and sets it to [Long.MIN_VALUE] (the minimum value an instance of [Long] can have).
     */
    object Header : DataItem() {
        override val id: Long = Long.MIN_VALUE
    }

    /**
     * The "id" of our [DataItem], subclasses must override this with a unique value that can be
     * used by [DiffUtil] to determine if two objects represent the same item.
     */
    abstract val id: Long
}

