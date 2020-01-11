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
 * This is the [BindingAdapter] for the app:sleepDurationFormatted attribute of [TextView] which is
 * used to set the text of the android:id="@+id/sleep_length" [TextView] of the fragment_sleep_detail
 * layout file used by the `SleepDetailFragment`. If our [SleepNight] parameter [item] is not *null*
 * we set the text of the [TextView] that uses our attribute to the string returned by our method
 * [convertDurationToFormatted] when it is passed the `startTimeMilli` and `endTimeMilli` fields of
 * [item].
 */
@BindingAdapter("sleepDurationFormatted")
fun TextView.setSleepDurationFormatted(item: SleepNight?) {
    item?.let {
        text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, context.resources)
    }
}

/**
 * This is the [BindingAdapter] for the app:sleepQualityString attribute of [TextView] which is
 * used to set the text of the android:id="@+id/quality_string" [TextView] of the fragment_sleep_detail
 * layout file used by the `SleepDetailFragment`. If our [SleepNight] parameter [item] is not *null*
 * we set the text of the [TextView] that uses our attribute to the string returned by our method
 * [convertNumericQualityToString] when it is passed the `sleepQuality` field of [item].
 */
@BindingAdapter("sleepQualityString")
fun TextView.setSleepQualityString(item: SleepNight?) {
    item?.let {
        text = convertNumericQualityToString(item.sleepQuality, context.resources)
    }
}

/**
 * This is the [BindingAdapter] for the app:sleepImage attribute of [ImageView] which is used to set
 * the image of the android:id="@+id/quality_image" [ImageView] of the fragment_sleep_detail layout
 * file used by the `SleepDetailFragment`. If our [SleepNight] parameter [item] is not *null* we
 * branch on the value of its `sleepQuality` in a *when* expression which returns the resource ID
 * for the icon drawable that depicts that sleep quality and use the value returned as the resource
 * ID for the `setImageResource` method to use as the content of the [ImageView] that uses our
 * attribute.
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