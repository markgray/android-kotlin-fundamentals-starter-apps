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
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.database.SleepNight
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * These functions create a formatted string that can be set in a TextView.
 */

/**
 * Number of milliseconds in one minute
 */
private val ONE_MINUTE_MILLIS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES)

/**
 * Number of milliseconds in one hour
 */
private val ONE_HOUR_MILLIS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)

/**
 * Convert a duration to a formatted string for display.
 *
 * Examples:
 *
 *     6 seconds on Wednesday
 *     2 minutes on Monday
 *     40 hours on Thursday
 *
 * We initialize our [Long] variable `val durationMilli` to our parameter [endTimeMilli] minus our
 * parameter [startTimeMilli]. We initialize our [String] variable `val weekdayString` to the result
 * of formatting [startTimeMilli] according to to a [SimpleDateFormat] constructed for the pattern
 * "EEEE" (Day name in week) for the default locale. Then we branch on the value of `durationMilli`:
 *
 *  - less than [ONE_MINUTE_MILLIS] we initialize our variable `val seconds` to the result of
 *  converting `durationMilli` to seconds and return the string formatted using the format
 *  [R.string.seconds_length] ("%d seconds on %s") for `seconds` and `weekdayString`.
 *
 *  - less than [ONE_HOUR_MILLIS] we initialize our variable `val minutes` to the result of
 *  converting `durationMilli` to minutes and return the string formatted using the format
 *  [R.string.minutes_length] ("%d minutes on %s") for `minutes` and `weekdayString`.
 *
 *  - For larger values we initialize our variable `val hours` to the result of converting
 *  `durationMilli` to hours and return the string formatted using the format
 *  [R.string.hours_length] ("%d hours on %s") for `hours` and `weekdayString`.
 *
 * Used to set the text of the `sleepLength` [TextView] of the `ViewHolder` used by our
 * `SleepNightAdapter` class.
 *
 * @param startTimeMilli the start of the interval
 * @param endTimeMilli the end of the interval
 * @param res resources used to load formatted strings
 * @return a [String] which displays the duration of sleep in idiomatic way.
 */
fun convertDurationToFormatted(startTimeMilli: Long, endTimeMilli: Long, res: Resources): String {
    val durationMilli = endTimeMilli - startTimeMilli
    val weekdayString = SimpleDateFormat("EEEE", Locale.getDefault()).format(startTimeMilli)
    return when {
        durationMilli < ONE_MINUTE_MILLIS -> {
            val seconds = TimeUnit.SECONDS.convert(durationMilli, TimeUnit.MILLISECONDS)
            res.getString(R.string.seconds_length, seconds, weekdayString)
        }
        durationMilli < ONE_HOUR_MILLIS -> {
            val minutes = TimeUnit.MINUTES.convert(durationMilli, TimeUnit.MILLISECONDS)
            res.getString(R.string.minutes_length, minutes, weekdayString)
        }
        else -> {
            val hours = TimeUnit.HOURS.convert(durationMilli, TimeUnit.MILLISECONDS)
            res.getString(R.string.hours_length, hours, weekdayString)
        }
    }
}

/**
 * Returns a string describing the quality of sleep based on the numeric quality rating. Used to set
 * the text of the `quality` [TextView] of the `ViewHolder` used by our `SleepNightAdapter` class,
 * and also used by our [formatNights] method.
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
 * Take the Long milliseconds returned by the system and stored in Room,
 * and convert it to a nicely formatted string for display.
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
    // fromHtml is deprecated for target API without a flag, but since our minSDK is 19, we
    // can't use the newer version, which requires minSDK of 24
    //https://developer.android.com/reference/android/text/Html#fromHtml(java.lang.String,%20int)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        return Html.fromHtml(sb.toString())
    }
}

/**
 * ViewHolder that holds a single [TextView].
 *
 * A ViewHolder holds a view for the [RecyclerView] as well as providing additional information
 * to the RecyclerView such as where on the screen it was last drawn during scrolling.
 *
 * @param textView the [TextView] to use as our view.
 */
@Suppress("unused", "CanBeParameter", "MemberVisibilityCanBePrivate")
class TextItemViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)