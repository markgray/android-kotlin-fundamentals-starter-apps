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


package com.example.android.gdgfinder.search

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.gdgfinder.network.GdgChapter
import com.example.android.gdgfinder.search.GdgListAdapter.GdgListViewHolder
import com.example.android.gdgfinder.databinding.ListItemBinding

/**
 * The adapter we use for the [RecyclerView] in the layout file layout/fragment_gdg_list.xml (binding
 * property `gdgChapterList`, resource ID `R.id.gdg_chapter_list`) which is used as the content view
 * of `GdgListFragment`.
 *
 * @param clickListener the [GdgClickListener] whose `onClick` override will be called when any item
 * in our list is clicked.
 */
@Suppress("MemberVisibilityCanBePrivate") // I like to use kdoc [] references
class GdgListAdapter(
    /**
     * the [GdgClickListener] whose `onClick` override will be called when any item
     * in our list is clicked.
     */
    val clickListener: GdgClickListener
) : ListAdapter<GdgChapter, GdgListViewHolder>(DiffCallback) {
    /**
     * The [DiffUtil.ItemCallback] that is used by the framework to calculate the diff between two
     * non-null items in our list of [GdgChapter] objects in order to calculate the minimal number
     * of updates necessary to correctly display a new list.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<GdgChapter>() {
        /**
         * Called to check whether two objects represent the same item. For example, if your items
         * have unique ids, this method should check their id equality.
         *
         * Note: `null` items in the list are assumed to be the same as another `null`
         * item and are assumed to not be the same as a non-`null` item. This callback will
         * not be invoked for either of those cases.
         *
         * We just return the result of kotlin's referential equality operator ("===") for our two
         * parameters.
         *
         * @param oldItem The item in the old list.
         * @param newItem The item in the new list.
         * @return True if the two items represent the same object or false if they are different.
         */
        override fun areItemsTheSame(oldItem: GdgChapter, newItem: GdgChapter): Boolean {
            return oldItem === newItem
        }

        /**
         * Called to check whether two items have the same data. This information is used to detect
         * if the contents of an item have changed. We just return the result of kotlin's structural
         * equality operator ("==") for our two parameters.
         *
         * @param oldItem The item in the old list.
         * @param newItem The item in the new list.
         * @return True if the contents of the items are the same or false if they are different.
         */
        override fun areContentsTheSame(oldItem: GdgChapter, newItem: GdgChapter): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * The [RecyclerView.ViewHolder] we use to hold each [GdgChapter] items that are to be displayed
     * in our [RecyclerView].
     *
     * @param binding the [ListItemBinding] binding object for the view that this [GdgChapter] item
     * will be displayed in (it is inflated from our layout/list_item.xml layout file).
     */
    class GdgListViewHolder(private var binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * This is called by the [onBindViewHolder] override of this [GdgListAdapter] in order to
         * have us update the contents of our [GdgListViewHolder] to reflect the [GdgChapter] item
         * [gdgChapter]. We set the `chapter` variable of our [ListItemBinding] field [binding] to
         * our [GdgChapter] parameter [gdgChapter] and the `clickListener` variable of [binding] to
         * our [GdgClickListener] parameter [listener]. We then call the `executePendingBindings`
         * method of [binding] to have it evaluate the pending bindings, updating any Views that
         * have expressions bound to modified variables.
         *
         * @param listener this is used to set the `clickListener` variable of our [ListItemBinding]
         * field [binding], which is used in a binding expression lambda for the "android:onClick"
         * attribute of the `ConstraintLayout` displaying our item which calls `clickListener.onClick`
         * with the [GdgChapter] instance pointed to by the `chapter` variable of [binding].
         * @param gdgChapter this is used to set the `chapter` variable of [binding], which is used
         * in binding expression lambdas to display the chapter `name` and to pass to the `onClick`
         * method of the `clickListener` variable of [binding].
         */
        fun bind(listener: GdgClickListener, gdgChapter: GdgChapter) {
            binding.chapter = gdgChapter
            binding.clickListener = listener
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }

        companion object {
            /**
             * Called by the [onCreateViewHolder] override of this [GdgListAdapter] in order to have
             * us inflate the layout/list_item.xml layout file into a [ListItemBinding] binding
             * object which we then use to construct a [GdgListViewHolder] for it to return to its
             * caller. We initialize our [LayoutInflater] variable `val layoutInflater` with the
             * [LayoutInflater] for the context of our [ViewGroup] parameter [parent]. We then
             * initialize our [ListItemBinding] variable `val binding` by having the `inflate` method
             * of [ListItemBinding] use `layoutInflater` to inflate an instance of itself using our
             * [ViewGroup] parameter [parent] for layout params without attaching to it. We then
             * return a [GdgListViewHolder] constructed to use `binding` as the binding object of
             * the view it is to use.
             *
             * @param parent The [ViewGroup] into which the new `View` will be added after it is
             * bound to an adapter position.
             * @return A new [GdgListViewHolder].
             */
            fun from(parent: ViewGroup): GdgListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                return GdgListViewHolder(binding)
            }
        }
    }

    /**
     * Part of the [RecyclerView] adapter, called when [RecyclerView] needs a new `ViewHolder` of the
     * given [viewType]. We just return the [GdgListViewHolder] that the [GdgListViewHolder.from]
     * method constructs.
     *
     * @param parent The [ViewGroup] into which the new `View` will be added after it is bound to an
     * adapter position.
     * @param viewType The view type of the new View.
     * @return A new `ViewHolder` that holds a `View` of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GdgListViewHolder {
        return GdgListViewHolder.from(parent)
    }

    /**
     * Part of the RecyclerView adapter, called when RecyclerView needs to show an item. The
     * `ViewHolder` passed may be recycled, so make sure that this sets any properties that
     * may have been set previously. We just call the `bind` method of our [GdgListViewHolder]
     * parameter [holder] to have it bind itself to the [GdgChapter] that is to be found at
     * position [position] in our current dataset using our [clickListener] field as the
     * [GdgClickListener] to use if the item is clicked.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item
     * at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: GdgListViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position))
    }
}

/**
 * This class exists to invoke the lambda which is passed to its constructor when its [onClick]
 * method is called. The only instance is created in the `onCreateView` override of
 * `GdgListFragment`, and its [onClick] method is invoked by a binding expression for the
 * "android:onClick" attribute of the `ConstraintLayout` widget in the layout/list_item.xml
 * layout file.
 *
 * @param clickListener a function (or lambda) which takes a [GdgChapter] as its argument and
 * returns nothing.
 */
class GdgClickListener(
    /**
     * a function (or lambda) which takes a [GdgChapter] as its argument and
     * returns nothing.
     */
    val clickListener: (chapter: GdgChapter) -> Unit
) {
    /**
     * This method is invoked by a binding expression for the "android:onClick" attribute of the
     * `ConstraintLayout` widget in the layout/list_item.xml layout file. The instance created in
     * the `onCreateView` override of `GdgListFragment` creates a Uri from the [GdgChapter] passed
     * it and launches an [Intent] to view that Uri.
     *
     * @param chapter the [GdgChapter] of the item that was clicked.
     */
    fun onClick(chapter: GdgChapter): Unit = clickListener(chapter)
}
