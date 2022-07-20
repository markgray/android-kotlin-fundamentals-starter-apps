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

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight

/**
 * This is the BindingAdapter for the "app:sleepDurationFormatted" attribute. A binding expression
 * for that attribute on the [TextView] with ID R.id.sleep_length in the layout file used by the
 * `SleepDetailFragment` (layout/fragment_sleep_detail.xml) calls this with the [SleepNight] in the
 * `night` field of its `sleepDetailViewModel` variable (the `SleepDetailViewModel` for the fragment).
 * It was also once used by a binding expression for that attribute on the [TextView] with ID
 * R.id.sleep_length in the layout file that was once used for the items in our LinearLayout
 * `RecylclerView` (the file layout/list_item_sleep_night.xml).
 *
 * If our [SleepNight] parameter [item] is not `null` we set the text of the [TextView] with our
 * attribute to the formatted string that our [convertDurationToFormatted] method produces from the
 * `startTimeMilli` and `endTimeMilli` fields of our [SleepNight] parameter [item].
 *
 * @param item the [SleepNight] whose sleep duration the [TextView] needs to display.
 */
@BindingAdapter("sleepDurationFormatted")
fun TextView.setSleepDurationFormatted(item: SleepNight?) {
    item?.let {
        text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, context.resources)
    }
}

/**
 * This is the BindingAdapter for the "app:sleepQualityString" attribute. A binding expression
 * for that attribute on the [TextView] with ID R.id.quality_string in the layout file used for
 * the items in our `RecylclerView` (the file layout/list_item_sleep_night.xml) calls this with
 * the [SleepNight] in its "sleep" variable. It is also used in the layout/fragment_sleep_detail.xml
 * layout file where the binding expression for the "app:sleepQualityString" attribute or the
 * [TextView] with ID R.id.quality_string calls this method with the [SleepNight] in the `night`
 * field of its `sleepDetailViewModel` variable (the `SleepDetailViewModel` for the fragment).
 * (The attribute also used to be used by the layout/list_item_sleep_night_linear.xml layout file).
 *
 * If our [SleepNight] parameter [item] is not `null` this method sets the text of any [TextView]
 * with the "app:sleepQualityString" attribute to the formatted string that our
 * [convertNumericQualityToString] method produces from the `sleepQuality` field of our [SleepNight]
 * parameter [item].
 *
 * @param item the [SleepNight] whose sleep quality the [TextView] needs to describe.
 */
@BindingAdapter("sleepQualityString")
fun TextView.setSleepQualityString(item: SleepNight?) {
    item?.let {
        text = convertNumericQualityToString(item.sleepQuality, context.resources)
    }
}

/**
 * This is the BindingAdapter for the "app:sleepImage" attribute. A binding expression
 * for that attribute on the [ImageView] with ID R.id.quality_image in the layout file used for
 * the items in our `RecylclerView` (the file layout/list_item_sleep_night.xml) calls this with
 * the [SleepNight] in its "sleep" variable. The attribute is also used for the [ImageView] with
 * the ID R.id.quality_image in the layout/fragment_sleep_detail.xml layout file where the binding
 * expression for the attribute calls this method with the [SleepNight] in the `night` field of its
 * `sleepDetailViewModel` variable (the `SleepDetailViewModel` for the fragment). (The attribute
 * also used to be used by the layout/list_item_sleep_night_linear.xml layout file).
 *
 * If our [SleepNight] parameter [item] is not `null` this method sets as the content of any
 * [ImageView] with the "app:sleepImage" attribute to a drawable whose resource ID is chosen based
 * on the value of the `sleepQuality` property of our [SleepNight] parameter [item].
 *
 * @param item the [SleepNight] whose sleep quality icon the [ImageView] needs to display.
 */
@BindingAdapter("sleepImage")
fun ImageView.setSleepImage(item: SleepNight?) {
    item?.let {
        setImageResource(when (item.sleepQuality) {
            0 -> R.drawable.ic_sleep_0
            1 -> R.drawable.ic_sleep_1
            2 -> R.drawable.ic_sleep_2
            3 -> R.drawable.ic_sleep_3
            4 -> R.drawable.ic_sleep_4
            5 -> R.drawable.ic_sleep_5
            else -> R.drawable.ic_sleep_active
        })
    }
}
