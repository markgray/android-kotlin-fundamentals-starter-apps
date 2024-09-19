/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.dessertclicker

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ShareCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import com.example.android.dessertclicker.databinding.ActivityMainBinding
import timber.log.Timber

/** onSaveInstanceState Bundle Keys **/
/**
 * Key under which we store our `revenue` field in the bundle passed to our
 * `onSaveInstanceState` override.
 */
const val KEY_REVENUE: String = "revenue_key"

/**
 * Key under which we store our `dessertsSold` field in the bundle passed to our
 * `onSaveInstanceState` override.
 */
const val KEY_DESSERT_SOLD: String = "dessert_sold_key"

/**
 * Key under which we store the `secondsCount` field of our `dessertTimer` timer instance
 * in the bundle passed to our `onSaveInstanceState` override.
 */
const val KEY_TIMER_SECONDS: String = "timer_seconds_key"

/**
 * This is the main activity of our DessertClicker app.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Running total of the price of all desserts sold so far.
     */
    private var revenue = 0

    /**
     * Total number of desserts sold so far.
     */
    private var dessertsSold = 0

    /**
     * Handle to our [DessertTimer] instance.
     */
    private lateinit var dessertTimer: DessertTimer

    /**
     * Contains binding links to all the views in our layout file which have ID's
     */
    private lateinit var binding: ActivityMainBinding

    /** Dessert Data **/

    /**
     * Simple data class that represents a dessert. Includes the resource id integer associated with
     * the image, the price it's sold for, and the startProductionAmount, which determines when
     * the dessert starts to be produced.
     */
    data class Dessert(
        /**
         * Resource id for the image of the dessert
         */
        val imageId: Int,
        /**
         * Price of the dessert
         */
        val price: Int,
        /**
         * Number of desserts sold when we start to produce more expensive desserts. We search thru
         * the list until we see a dessert who's "startProductionAmount" is greater than the amount
         * sold and then switch to that dessert.
         */
        val startProductionAmount: Int)

    // Create a list of all desserts, in order of when they start being produced
    private val allDesserts = listOf(
        Dessert(R.drawable.cupcake, 5, 0),
        Dessert(R.drawable.donut, 10, 5),
        Dessert(R.drawable.eclair, 15, 20),
        Dessert(R.drawable.froyo, 30, 50),
        Dessert(R.drawable.gingerbread, 50, 100),
        Dessert(R.drawable.honeycomb, 100, 200),
        Dessert(R.drawable.icecreamsandwich, 500, 500),
        Dessert(R.drawable.jellybean, 1000, 1000),
        Dessert(R.drawable.kitkat, 2000, 2000),
        Dessert(R.drawable.lollipop, 3000, 4000),
        Dessert(R.drawable.marshmallow, 4000, 8000),
        Dessert(R.drawable.nougat, 5000, 16000),
        Dessert(R.drawable.oreo, 6000, 20000)
    )

    /**
     * The current [Dessert] we are selling (and displaying in our UI).
     */
    private var currentDessert = allDesserts[0]

    /**
     * Called when the activity is starting. First we call our super's implementation of `onCreate`,
     * and then we log the fact that we were called. We initialize our [ActivityMainBinding] field
     * [binding] to the binding to the view that the [DataBindingUtil.setContentView] creates when
     * it inflates our layout file [R.layout.activity_main] and sets the view to be our content view.
     * We set the `OnClickListener` of the `dessertButton` ImageButton property of [binding] to a
     * lambda which calls our [onDessertClicked] method. We set our [DessertTimer] field [dessertTimer]
     * to a new instance constructed to use the `Lifecycle` of `this`. If our [Bundle] parameter
     * [savedInstanceState] is not null we are being restarted after a configuration change so we
     * initialize our [revenue] field from the [Int] stored under the key [KEY_REVENUE], our
     * [dessertsSold] field from the [Int] stored under the key [KEY_DESSERT_SOLD], and the
     * `secondsCount` field of [dessertTimer] from the [Int] stored under the key [KEY_TIMER_SECONDS]
     * in [savedInstanceState] and then call our [showCurrentDessert] method to determine which
     * dessert to show.
     *
     * Restarting or not, we set the `revenue` variable of [binding] to our [revenue] field, and
     * the `amountSold` variable of [binding] to our [dessertsSold] field. We then set the image
     * displayed by the `dessertButton` ImageButton in [binding] to the drawable whose resource ID
     * is the `imageId` field of our [Dessert] field [currentDessert].
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this [Bundle] contains the data it most recently supplied in [onSaveInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Timber.i("onCreate called")

        // Use Data Binding to get reference to the views
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view.
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
                topMargin = insets.top
            }

            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }

        binding.dessertButton.setOnClickListener {
            onDessertClicked()
        }

        dessertTimer = DessertTimer(this.lifecycle)

        if (savedInstanceState != null) {
            revenue = savedInstanceState.getInt(KEY_REVENUE, 0)
            dessertsSold = savedInstanceState.getInt(KEY_DESSERT_SOLD, 0)
            dessertTimer.secondsCount = savedInstanceState.getInt(KEY_TIMER_SECONDS, 0)
            showCurrentDessert()
        }

        // Set the TextViews to the right values
        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Make sure the correct dessert is showing
        binding.dessertButton.setImageResource(currentDessert.imageId)
    }

    /**
     * Called when the user navigates away from the app but might come back. Called to retrieve
     * per-instance state from an activity before it's killed so that the state can be restored in
     * [onCreate] or [onRestoreInstanceState] (the [Bundle] populated by this method will be passed
     * to both). First we call our super's implementation of [onSaveInstanceState] and then log the
     * fact that we were called. We store our [revenue] field under the key [KEY_REVENUE] in our
     * [Bundle] parameter [outState], our [dessertsSold] field under the key [KEY_DESSERT_SOLD] in
     * our [Bundle] parameter [outState] and the `secondsCount` field of our [dessertTimer] field
     * under the key [KEY_TIMER_SECONDS] in our [Bundle] parameter [outState].
     *
     * @param outState [Bundle] in which to place your saved state.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_REVENUE, revenue)
        outState.putInt(KEY_DESSERT_SOLD, dessertsSold)
        outState.putInt(KEY_TIMER_SECONDS, dessertTimer.secondsCount)

        Timber.i("onSaveInstanceState Called")
    }

    /** Lifecycle Methods **/

    /**
     * Called after [onCreate] or after [onRestart] when the activity had been stopped, but is now
     * again being displayed to the user. It will usually be followed by [onResume]. This is a good
     * place to begin drawing visual elements, running animations, etc. Derived classes must call
     * through to the super class's implementation of this method. If they do not, an exception
     * will be thrown. First we call our super's implementation of `onStart`, then we log the fact
     * that we were called.
     */
    override fun onStart() {
        super.onStart()
        Timber.i("onStart called")
    }

    /**
     * Called after [onRestoreInstanceState], [onRestart] or [onPause], for your activity to start
     * interacting with the user. This is an indicator that the activity became active and ready to
     * receive input. It is on top of an activity stack and visible to user. Derived classes must
     * call through to the super class's implementation of this method. If they do not, an exception
     * will be thrown. First we call our super's implementation of `onResume`, then we log the fact
     * that we were called.
     */
    override fun onResume() {
        super.onResume()
        Timber.i("onResume Called")
    }

    /**
     * Called as part of the activity lifecycle when the user no longer actively interacts with the
     * activity, but it is still visible on screen. The counterpart to [onResume]. This callback is
     * mostly used for saving any persistent state the activity is editing, to present a "edit in
     * place" model to the user and making sure nothing is lost if there are not enough resources to
     * start the new activity without first killing this one. This is also a good place to stop
     * things that consume a noticeable amount of CPU in order to make the switch to the next
     * activity as fast as possible. Derived classes must call through to the super class's
     * implementation of this method. If they do not, an exception will be thrown. First we call our
     * super's implementation of `onPause`, then we log the fact that we were called.
     */
    override fun onPause() {
        super.onPause()
        Timber.i("onPause Called")
    }

    /**
     * Called when you are no longer visible to the user. You will next receive either [onRestart],
     * [onDestroy], or nothing, depending on later user activity. This is a good place to stop
     * refreshing UI, running animations and other visual things. Derived classes must call through
     * to the super class's implementation of this method. If they do not, an exception will be
     * thrown. First we call our super's implementation of `onStop`, then we log the fact that we
     * were called.
     */
    override fun onStop() {
        super.onStop()
        Timber.i("onStop Called")
    }

    /**
     * Perform any final cleanup before an activity is destroyed. This can happen either because the
     * activity is finishing (someone called [finish] on it), or because the system is temporarily
     * destroying this instance of the activity to save space. You can distinguish between these two
     * scenarios with the [isFinishing] method.
     *
     * Note: do not count on this method being called as a place for saving data! For example, if an
     * activity is editing data in a content provider, those edits should be committed in either
     * [onPause] or [onSaveInstanceState], not here.
     *
     * Derived classes must call through to the super class's implementation of this method. If they
     * do not, an exception will be thrown. First we call our super's implementation of `onDestroy`,
     * then we log the fact that we were called.
     */
    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy Called")
    }

    /**
     * Called after [onStop] when the current activity is being re-displayed to the user (the user
     * has navigated back to it). It will be followed by [onStart] and then [onResume].
     *
     * Derived classes must call through to the super class's implementation of this method. If they
     * do not, an exception will be thrown. First we call our super's implementation of `onRestart`,
     * then we log the fact that we were called.
     */
    override fun onRestart() {
        super.onRestart()
        Timber.i("onRestart Called")
    }

    /**
     * Updates the score when the dessert is clicked. Possibly shows a new dessert. We add the
     * `price` field of our [Dessert] field [currentDessert] to our field [revenue], and increment
     * our [dessertsSold] field. We set the `revenue` variable of [binding] to our [revenue] field
     * and the `amountSold` variable of [binding] to our [dessertsSold] field. Finally we call our
     * [showCurrentDessert] method to show the next dessert to the user.
     */
    private fun onDessertClicked() {

        // Update the score
        revenue += currentDessert.price
        dessertsSold++

        binding.revenue = revenue
        binding.amountSold = dessertsSold

        // Show the next dessert
        showCurrentDessert()
    }

    /**
     * Determine which dessert to show. We initialize our [Dessert] variable `var newDessert` to the
     * 0'th entry in our [allDesserts] list of desserts field. Then we loop through all the `dessert`
     * in [allDesserts] and if our [dessertsSold] field is greater than or equal to the
     * `startProductionAmount` field of `dessert` we set `newDessert` to `dessert` and loop around
     * for the next `dessert`. When we reach a `dessert` whose `startProductionAmount` field is less
     * than [dessertsSold] we break out of the loop leaving `newDessert` pointing to the last `dessert`
     * we found whose `startProductionAmount` field was less than or equal to our [dessertsSold] field.
     *
     * Finally if `newDessert` is not equal to our [currentDessert] we set [currentDessert] to
     * `newDessert` and set the image displayed by the `dessertButton` ImageButton in [binding] to
     * the drawable whose resource ID is the `imageId` field of `newDessert`.
     */
    private fun showCurrentDessert() {
        var newDessert = allDesserts[0]
        for (dessert in allDesserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                newDessert = dessert
            }
            // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
            // you'll start producing more expensive desserts as determined by startProductionAmount
            // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
            // than the amount sold.
            else break
        }

        // If the new dessert is actually different than the current dessert, update the image
        if (newDessert != currentDessert) {
            currentDessert = newDessert
            binding.dessertButton.setImageResource(newDessert.imageId)
        }
    }

    /**
     * Menu method called when the [MenuItem] with ID [R.id.shareMenuButton] is selected. We create
     * an `Intent` for our variable `val shareIntent` which has the action `ACTION_SEND` whose
     * literal text data to be sent as part of the share is a formatted string displaying our
     * [dessertsSold] and [revenue] field and whose type is "text/plain". Then wrapped in a `try`
     * block intended to catch [ActivityNotFoundException] we launch the activity requested by the
     * `shareIntent`.
     */
    private fun onShare() {
        val shareIntent = ShareCompat.IntentBuilder(this)
            .setText(getString(R.string.share_text, dessertsSold, revenue))
            .setType("text/plain")
            .intent
        try {
            startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.sharing_not_available),
                Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu. You should place your menu
     * items in [menu]. We use a `MenuInflater` with this context to inflate our menu layout file
     * [R.menu.main_menu] into our [Menu] parameter [menu]. Then we return the value returned by
     * our super's implementation of `onCreateOptionsMenu`.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return `true` for the menu to be displayed, if you return `false` it will
     * not be shown.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * This hook is called whenever an item in your options menu is selected. When the `itemId`
     * of our [MenuItem] parameter [item] is [R.id.shareMenuButton] we call our [onShare] method
     * to have it "share" our [dessertsSold] and [revenue] fields. In any case we return the value
     * returned by our super's implementation of `onOptionsItemSelected` to the caller.
     *
     * @param item The menu item that was selected.
     * @return boolean Return `false` to allow normal menu processing to proceed, `true` to
     * consume it here.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.shareMenuButton -> onShare()
        }
        return super.onOptionsItemSelected(item)
    }
}
