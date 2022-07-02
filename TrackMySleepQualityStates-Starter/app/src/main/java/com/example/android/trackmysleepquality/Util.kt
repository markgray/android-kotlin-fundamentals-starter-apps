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

package com.example.android.trackmysleepquality

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.example.android.trackmysleepquality.database.SleepNight
import java.text.SimpleDateFormat

/**
 * These functions create a formatted string that can be set in a TextView.
 */

/**
 * Returns a string describing the quality of sleep based on the numeric quality rating. We use a
 * `when` switch to set our [String] variable `var qualityString` to different strings from our
 * resources, then return `qualityString` to the caller.
 *
 * @param quality the numeric rating of the sleep quality.
 * @param resources [Resources] used to load formatted strings.
 * @return [String] describing the quality of sleep.
 */
fun convertNumericQualityToString(quality: Int, resources: Resources): String {
    var qualityString = resources.getString(R.string.three_ok)
    when (quality) {
        -1 -> qualityString = "--"
        0 -> qualityString = resources.getString(R.string.zero_very_bad)
        1 -> qualityString = resources.getString(R.string.one_poor)
        2 -> qualityString = resources.getString(R.string.two_soso)
        4 -> qualityString = resources.getString(R.string.four_pretty_good)
        5 -> qualityString = resources.getString(R.string.five_excellent)
    }
    return qualityString
}


/**
 * Takes the Long milliseconds returned by the system and stored in Room, and converts it to a
 * nicely formatted string for display.
 *
 *     EEEE - Display the long letter version of the weekday
 *     MMM - Display the letter abbreviation of the month
 *     dd-yyyy - day in month and full year numerically
 *     HH:mm - Hours and minutes in 24hr format
 *
 * @param systemTime the difference, measured in milliseconds, between the time and midnight,
 * January 1, 1970 UTC.
 * @return the date and time of [systemTime] in the format: "Tuesday Aug-11-2020 Time: 22:42"
 */
@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("EEEE MMM-dd-yyyy' Time: 'HH:mm")
        .format(systemTime).toString()
}

/**
 * Takes a list of SleepNights and converts and formats it into one string for display.
 *
 * For display in a TextView, we have to supply one string, and styles are per TextView, not
 * applicable per word. So, we build a formatted string using HTML. This is handy, but we will
 * learn a better way of displaying this data in a future lesson.
 *
 * We initialize our [StringBuilder] variable `val sb` with a new instance. Then we use the `apply`
 * extension function on `sb` to perform a block of operations on `sb`, starting by appending a
 * `<h3>` header follow by looping through each of the [SleepNight] objects in the [nights] list
 * and appending html formatting strings and formatted representations of the fields in the
 * [SleepNight] being processed.
 *
 * When done building the [StringBuilder] we convert `sb` to a [String] and return the [Spanned]
 * returned by the [Html.fromHtml] method for that [String], using the two parameter version if our
 * device is running Android greater than or equal to SDK 24, or the one parameter version if our
 * device is older.
 *
 * Used  to set the `nightsString` field of `SleepTrackerViewModel`.
 *
 * @param   nights List of all SleepNights in the database.
 * @param   resources Resources object for all the resources defined for our app.
 *
 * @return  [Spanned] An interface for text that has formatting attached to it.
 * See: https://developer.android.com/reference/android/text/Spanned
 */
fun formatNights(nights: List<SleepNight>, resources: Resources): Spanned {
    val sb = StringBuilder()
    sb.apply {
        append(resources.getString(R.string.title))
        nights.forEach {
            append("<br>")
            append(resources.getString(R.string.start_time))
            append("\t${convertLongToDateString(it.startTimeMilli)}<br>")
            if (it.endTimeMilli != it.startTimeMilli) {
                append(resources.getString(R.string.end_time))
                append("\t${convertLongToDateString(it.endTimeMilli)}<br>")
                append(resources.getString(R.string.quality))
                append("\t${convertNumericQualityToString(it.sleepQuality, resources)}<br>")
                append(resources.getString(R.string.hours_slept))
                // Hours
                append("\t ${it.endTimeMilli.minus(it.startTimeMilli) / 1000 / 60 / 60}:")
                // Minutes
                append("${it.endTimeMilli.minus(it.startTimeMilli) / 1000 / 60}:")
                // Seconds
                append("${it.endTimeMilli.minus(it.startTimeMilli) / 1000}<br><br>")
            }
        }
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}
