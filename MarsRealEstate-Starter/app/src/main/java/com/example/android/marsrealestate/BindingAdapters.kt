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

package com.example.android.marsrealestate

import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.marsrealestate.network.MarsProperty
import com.example.android.marsrealestate.overview.MarsApiStatus
import com.example.android.marsrealestate.overview.PhotoGridAdapter

/**
 * This is the BindingAdapter for the "app:imageUrl" attribute. A binding expression for that
 * attribute on [ImageView]'s in the layout files layout/grid_view_item.xml and
 * layout/fragment_detail.xml observes the `imgSrcUrl` property of the [MarsProperty] it is
 * displaying and when that changes value calls this BindingAdapter with that [String]. If our
 * [imgUrl] parameter is not `null` we initialize our [Uri] variable `val imgUri` to the [Uri]
 * parsed from our [imgUrl] parameter from which we use to construct a new builder, copying the
 * attributes from that [Uri], setting the scheme to "https" then building the [Uri]. We call the
 * [Glide.with] method to begin a load using the context of our [ImageView] parameter [imgView],
 * creating a RequestBuilder to load `imgUri`, to which we apply the [RequestOptions] to use
 * R.drawable.loading_animation as the place holder and R.drawable.ic_broken_image as the resource
 * to display if the load fails. Finally we set the [ImageView] the resource will be loaded into to
 * our [imgView] parameter, canceling any existing loads into the view.
 * (Short version: Uses the Glide library to load an image by URL into an [ImageView].)
 *
 * @param imgView the [ImageView] that uses the "app:imageUrl" attribute.
 * @param imgUrl the `imgSrcUrl` property of the [MarsProperty] passed us by the binding expression.
 */
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri: Uri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image))
            .into(imgView)
    }
}

/**
 * This is the BindingAdapter for the "app:listData" attribute. A binding expression for that
 * attribute on a [RecyclerView] in the layout file layout/fragment_overview.xml observes the
 * `properties` property of the `OverviewViewModel` and when that `LiveData` wrapped [List]
 * of [MarsProperty] changes this BindingAdapter is invoked. We retrieve the [PhotoGridAdapter]
 * of our [RecyclerView] parameter [recyclerView] to initialize our variable `val adapter`, then
 * call the `submitList` method of `adapter` to submit our [data] parameter to be diffed, and
 * displayed.
 *
 * @param recyclerView the [RecyclerView] which uses the "app:listData" attribute.
 * @param data the new list passed by the binding expression of the "app:listData" attribute.
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<MarsProperty>?) {
    val adapter = recyclerView.adapter as PhotoGridAdapter
    adapter.submitList(data)
}

/**
 * This binding adapter displays the [MarsApiStatus] of the network request in an image view. When
 * the request is loading, it displays a loading_animation (R.drawable.loading_animation). If the
 * request has an error, it displays a broken image to reflect the connection error
 * (R.drawable.ic_connection_error). When the request is finished, it hides the image view. It is
 * invoked when the binding expression of an "app:marsApiStatus" attribute on an [ImageView] observes
 * a change in the "viewModel.status" property, as happens for the [ImageView] with ID R.id.status_image
 * in the layout/fragment_overview.xml layout file.
 *
 * @param statusImageView the [ImageView] which has an "app:marsApiStatus" attribute
 * @param status the value of the "app:marsApiStatus" attribute, which is the result of evaluating
 * a binding expression which is observing the `status` property of the `OverviewViewModel` variable
 * `viewModel` in the layout file.
 */
@BindingAdapter("marsApiStatus")
fun bindStatus(statusImageView: ImageView, status: MarsApiStatus?) {
    when (status) {
        MarsApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }

        MarsApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }

        MarsApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }

        else -> {}
    }
}