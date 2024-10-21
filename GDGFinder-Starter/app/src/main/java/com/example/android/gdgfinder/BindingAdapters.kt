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


package com.example.android.gdgfinder

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.gdgfinder.network.GdgChapter
import com.example.android.gdgfinder.search.GdgListAdapter

/**
 * This binding adapter is used in layout/fragment_gdg_list.xml for the [RecyclerView] with the ID
 * [R.id.gdg_chapter_list] using the attribute app:listData="@{viewModel.gdgList}". The `gdgList`
 * field of the `GdgListViewModel` variable `viewModel` is a `LiveData` wrapped list of [GdgChapter]
 * objects. First we initialize our [GdgListAdapter] variable `val adapter` by retrieving the
 * adapter of our [RecyclerView] parameter [recyclerView]. We then call the `submitList` method of
 * `adapter` to have it set the new list to be displayed to our [data] parameter supplying a lambda
 * for the runnable to be executed when the List is committed which will scroll the list to the top
 * after the diffs are calculated and posted.
 *
 * @param recyclerView the [RecyclerView] which is using the attribute "app:listData".
 * @param data the list of [GdgChapter] objects passed as the attribute value of "app:listData".
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<GdgChapter>?) {
    val adapter = recyclerView.adapter as GdgListAdapter
    adapter.submitList(data) {
        // scroll the list to the top after the diffs are calculated and posted
        recyclerView.scrollToPosition(0)
    }
}

/**
 * This binding adapter is used in layout/fragment_gdg_list.xml for the "Waiting for location and
 * network result..." `TextView` using the attribute app:showOnlyWhenEmpty="@{viewModel.gdgList}".
 * If the [data] list of [GdgChapter] value of the attribute is `null` or empty it sets the
 * visibility of the view with our attribute to [View.VISIBLE], otherwise it sets the visibility to
 * [View.GONE].
 *
 * @param data the list of [GdgChapter] objects passed as the attribute value of app:showOnlyWhenEmpty
 */
@BindingAdapter("showOnlyWhenEmpty")
fun View.showOnlyWhenEmpty(data: List<GdgChapter>?) {
    visibility = when {
        data.isNullOrEmpty() -> View.VISIBLE
        else -> View.GONE
    }
}