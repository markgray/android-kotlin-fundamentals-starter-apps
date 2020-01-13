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
 * This is the View type we use for our header, there is one and only one header in our dataset with
 * the ID [Long.MIN_VALUE]. We return a `TextViewHolder` for this entry which is a three column wide
 * `TextView` displaying the string "Sleep Results".
 */
private const val ITEM_VIEW_TYPE_HEADER = 0
/**
 * This is the View type we use for the individual [SleepNight] data items in our dataset. We return
 * a `ViewHolder` whose `binding` is the [ListItemSleepNightBinding] that is automatically generated
 * from our layout/list_item_sleep_night.xml layout file (the [ListItemSleepNightBinding] Impl class
 * provides an `inflate` method for that layout file auto-magically).
 */
private const val ITEM_VIEW_TYPE_ITEM = 1

/**
 * The [ListAdapter] we use to fill our [RecyclerView] with [SleepNight] data (and Header)
 */
@Suppress("MemberVisibilityCanBePrivate")
class SleepNightAdapter(val clickListener: SleepNightListener):
        ListAdapter<DataItem, RecyclerView.ViewHolder>(SleepNightDiffCallback()) {

    /**
     * The is the [CoroutineScope] we use in our [addHeaderAndSubmitList] method to run the
     * suspending call to submit a new list to be diffed, and displayed. That method is called
     * by an `Observer` which is placed on the `LiveData` field `nights` of the [SleepTrackerViewModel]
     * when the `onCreateView` method is called in [SleepTrackerFragment].
     */
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder] to reflect the item at the given position. We branch
     * on whether our [RecyclerView.ViewHolder] parameter [holder] is a [ViewHolder] (our holder for
     * a [SleepNight] from the database) and if it is we initialize our [DataItem.SleepNightItem]
     * variable `val nightItem` to the [DataItem] in position [position] that [getItem] returns from
     * the list that our [addHeaderAndSubmitList] method has submitted to our [ListAdapter] super to
     * be diffed and displayed (an observer which the `onCreateView` method of [SleepTrackerFragment]
     * added to the `LiveData` `nights` property of [SleepTrackerViewModel] calls [addHeaderAndSubmitList]
     * when the list of [SleepNight] in the database has changed). We then call the `bind` method
     * of [holder] to have it set its binding `<variable>` `sleep` to the `sleepNight` field of
     * `nightItem` and the `clickListener` binding `<variable>` to our [SleepNightListener] field
     * [clickListener] (it then calls the `executePendingBindings` method of `binding` to have it
     * evaluate the pending bindings, updating any Views that have expressions bound to modified
     * variables.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val nightItem = getItem(position) as DataItem.SleepNightItem
                holder.bind(nightItem.sleepNight, clickListener)
            }
        }
    }

    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent an item.
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file. We branch on our parameter [viewType]:
     *
     *  * ITEM_VIEW_TYPE_HEADER - the [ViewHolder] holding our header: we return a [TextViewHolder]
     *  which contains the [View] that the [TextViewHolder.from] method inflates from our header
     *  layout file R.layout.header
     *
     *  * ITEM_VIEW_TYPE_ITEM - the [ViewHolder] holding an individual [SleepNight]: we return a
     *  [ViewHolder] that the [ViewHolder.from] method creates which holds the binding that the
     *  [ListItemSleepNightBinding.inflate] method returns.
     *
     *  * All other values of [viewType]: we *throw* an [ClassCastException]
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        @Suppress("RemoveCurlyBracesFromTemplate")
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    /**
     * Adds the [DataItem.Header] datum to the beginning of a [List] it constructs of datums which
     * hold [DataItem.SleepNightItem] objects constructed to hold the `nightId` PrimaryKey of every
     * [SleepNight] in the `List<SleepNight>` parameter [list], then calls [submitList] to submit
     * the new list to be diffed, and displayed. We launch a co-routine on the [CoroutineScope] of
     * [adapterScope] which first initializes the `List<DataItem>` variable `val items` to either
     * a [List] holding only the [DataItem.Header] header datum if our parameter list is *null* or
     * one formed by concatenating [DataItem.Header] to the beginning of a [DataItem] list that is
     * constructed by using the `map` method of our parameter [list] to construct and add a
     * [DataItem.SleepNightItem] holding the `nightId` PrimaryKey of every [SleepNight] that is in
     * our parameter [list]. Then using the [Dispatchers.Main] coroutine dispatcher we launch a
     * co-routine on the UI thread which calls the [submitList] method to submit the new list to be
     * diffed, and displayed. We are called from an `Observer` that the `onCreateView` method of
     * [SleepTrackerFragment] added to the `LiveData` `nights` property of [SleepTrackerViewModel]
     * which gets notified when the `getAllNights` method of our database signals that the `LiveData`
     * list of [SleepNight]'s in the database has changed.
     *
     * @param list the [List] of all [SleepNight] datums retrieved from the room database by the
     * `getAllNights` method of our `SleepDatabaseDao`
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
     * Return the view type of the item at [position] for the purposes of view recycling. We branch
     * on the type of the datum at position [position] in our dataset:
     *
     *  * [DataItem.Header]: we return ITEM_VIEW_TYPE_HEADER
     *
     *  * [DataItem.SleepNightItem]: we return ITEM_VIEW_TYPE_ITEM
     *
     *  * all other classes: we *throw* [IllegalArgumentException]
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * position [position]. Type codes need not be contiguous.
     */
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
            else -> throw(IllegalArgumentException("Illegal item view type"))
        }
    }

    /**
     * The [RecyclerView.ViewHolder] used to hold the header `TextView` inflated from the layout
     * file R.layout.header
     *
     * @param view the [View] we are to hold
     */
    class TextViewHolder(view: View): RecyclerView.ViewHolder(view) {
        companion object {
            /**
             * Constructs a [TextViewHolder] to hold the `TextView` inflated from the header layout
             * file R.layout.header
             *
             * @param parent the [ViewGroup] to use for our `LayoutParams`
             * @return a [TextViewHolder] holding the header `TextView` in its `view` field.
             */
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.header, parent, false)
                return TextViewHolder(view)
            }
        }
    }

    /**
     * The [RecyclerView.ViewHolder] constructed to hold the `binding` to a [ViewGroup] which can
     * display the data that an individual [SleepNight] datum represents.
     *
     * @param binding the [ListItemSleepNightBinding] binding we are to hold
     */
    class ViewHolder private constructor(
            val binding: ListItemSleepNightBinding
    ) : RecyclerView.ViewHolder(binding.root){

        /**
         * "Binds" the `<variable>`'s of our [ViewGroup] to the parameters passed us. We set the
         * `sleep` `<variable>` of [binding] to our [SleepNight] parameter [item] and its
         * `clickListener` `<variable>` to our [SleepNightListener] parameter [clickListener].
         * Then we call the `executePendingBindings` method of [binding] to have if evaluate the
         * pending bindings, updating any Views that have expressions bound to modified variables.
         *
         * @param item the [SleepNight] data that should be displayed by our [ViewGroup]
         * @param clickListener the [SleepNightListener] that will be called when our [ViewGroup] is
         * clicked.
         */
        fun bind(item: SleepNight, clickListener: SleepNightListener) {
            binding.sleep = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            /**
             * Factory method to create a [ViewHolder] which will hold the [ListItemSleepNightBinding]
             * binding to a newly inflated [View] auto-magically inflated from the layout file
             * list_item_sleep_night.xml by the generated [ListItemSleepNightBinding.inflate] method.
             * We initialize our [LayoutInflater] variable `val layoutInflater` to the [LayoutInflater]
             * for the context of our [ViewGroup] parameter [parent], then initialize our variable
             * `val binding` to the [ListItemSleepNightBinding] binding that the `inflate` method
             * returns when it uses `layoutInflater` to inflate the list_item_sleep_night.xml layout
             * file. Finally we return a [ViewHolder] constructed to hold `binding`.
             *
             * @param parent the [ViewGroup] we can use for its `Context` and `LayoutParams`
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
 * [DiffUtil.ItemCallback] callback that our [ListAdapter] will use to determine if our dataset has
 * changed.
 */
class SleepNightDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    /**
     * Called to check whether two objects represent the same item. For example, if your items have
     * unique ids, this method should check their id equality. And that is indeed what we do.
     *
     * @param oldItem The item in the old list.
     * @param newItem The item in the new list.
     * @return *true* if the two items represent the same object or *false* if they are different.
     */
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    /**
     * Called to check whether two items have the same data. This information is used to detect if
     * the contents of an item have changed. This method is used to check equality instead of
     * `equals` so that you can change its behavior depending on your UI. We just return the result
     * of comparing the two items using the "==" operator.
     *
     * @param oldItem The item in the old list.
     * @param newItem The item in the new list.
     * @return *true* if the contents of the items are the same or *false* if they are different.
     */
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

/**
 * The [onClick] method of this class is used whenever a [SleepNight] icon in the [RecyclerView] is
 * clicked. An instance is passed to the constructor of [SleepNightAdapter], and this instance is
 * bound to the `clickListener` `<variable>` of each `ViewHolder` holding a [SleepNight] by the
 * `bind` method of `ViewHolder`, and the `ConstraintLayout` displaying the [SleepNight] calls
 * [onClick] because of the attribute: android:onClick="@{() -> clickListener.onClick(sleep)}".
 * The [clickListener] passed the constructor of [SleepNightAdapter] in our case is a lambda which
 * calls the `onSleepNightClicked` method of [SleepTrackerViewModel] with the `nightId` field of
 * the [SleepNight] that the `ViewHolder` holds causing a navigation to a `SleepDetailFragment`
 * displaying the details of that [SleepNight].
 *
 * @param clickListener a lambda which will be invoked when our [onClick] method is called.
 */
class SleepNightListener(val clickListener: (sleepId: Long) -> Unit) {
    /**
     * Called using the binding magic when a view in the `RecyclerView` is clicked. Each view holds
     * an instance of the [SleepNightListener] passed our constructor in its `clickListener` variable
     * and the android:onClick attribute calls its [onClick] method with its [SleepNight] variable
     * `sleep`.
     *
     * @param night the [SleepNight] datum displayed in the icon which was cicked
     */
    fun onClick(night: SleepNight) = clickListener(night.nightId)
}

/**
 * The parent class of a datum in our dataset, which can hold either a [SleepNightItem] or a
 * [Header] subclass of [DataItem]
 */
sealed class DataItem {
    /**
     * The ID of this [DataItem], must be overridden by subclass
     */
    abstract val id: Long

    /**
     * A [DataItem] holding a [SleepNight] entry from the database
     */
    data class SleepNightItem(val sleepNight: SleepNight): DataItem()      {
        override val id = sleepNight.nightId
    }

    /**
     * The singleton [DataItem] for our header "entry"
     */
    object Header: DataItem() {
        override val id = Long.MIN_VALUE
    }
}