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
 *
 */

package com.example.android.marsrealestate.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsrealestate.databinding.GridViewItemBinding
import com.example.android.marsrealestate.network.MarsProperty

@Suppress("MemberVisibilityCanBePrivate")
class PhotoGridAdapter( private val onClickListener: OnClickListener ) :
        ListAdapter<MarsProperty,
                PhotoGridAdapter.MarsPropertyViewHolder>(DiffCallback) {
    /**
     * Called when RecyclerView needs a new `ViewHolder` of the given type to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarsPropertyViewHolder {
        return MarsPropertyViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the `ViewHolder` to reflect the item at the given position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: MarsPropertyViewHolder, position: Int) {
        val marsProperty = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(marsProperty)
        }
        holder.bind(marsProperty)
    }

    class MarsPropertyViewHolder(var binding: GridViewItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(marsProperty: MarsProperty) {
            binding.property = marsProperty
            binding.executePendingBindings()
        }
    }

    class OnClickListener(val clickListener: (marsProperty:MarsProperty) -> Unit) {
        fun onClick(marsProperty:MarsProperty) = clickListener(marsProperty)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MarsProperty>() {
        /**
         * Called to check whether two objects represent the same item. For example, if your items
         * have unique ids, this method should check their id equality.
         *
         * Note: `null` items in the list are assumed to be the same as another `null` item and are
         * assumed to not be the same as a non-`null` item. This callback will not be invoked for
         * either of those cases.
         *
         * @param oldItem The item in the old list.
         * @param newItem The item in the new list.
         * @return *true* if the two items represent the same object or *false* if they are different.
         */
        override fun areItemsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
            return oldItem === newItem
        }

        /**
         * Called to check whether two items have the same data. This information is used to detect
         * if the contents of an item have changed. This method to check equality instead of
         * [Object.equals] so that you can change its behavior depending on your UI. For example,
         * if you are using DiffUtil with a [RecyclerView.Adapter], you should return whether the
         * items' visual representations are the same. This method is called only if [areItemsTheSame]
         * returns `true` for these items.
         *
         * Note: Two `null` items are assumed to represent the same contents. This callback will not
         * be invoked for this case.
         *
         * @param oldItem The item in the old list.
         * @param newItem The item in the new list.
         * @return *true* if the contents of the items are the same or *false* if they are different.
         */
        override fun areContentsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
            return oldItem.id == newItem.id
        }
    }
}