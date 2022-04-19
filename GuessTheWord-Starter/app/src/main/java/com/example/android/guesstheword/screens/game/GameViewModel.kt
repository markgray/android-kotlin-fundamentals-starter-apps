package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

/**
 * The [ViewModel] that holds all the data and business logic for the [GameFragment].
 */
@Suppress("MemberVisibilityCanBePrivate")
class GameViewModel : ViewModel() {
    /**
     * The current word
     */
    private val _word = MutableLiveData<String>()
    val word: LiveData<String>
        get() = _word

    /**
     * The current score
     */
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    /**
     * The list of words - the front of the list is the next word to guess
     */
    lateinit var wordList: MutableList<String>

    /**
     * Event which triggers the end of the game
     */
    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish

    /**
     * Countdown time
     */
    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    /**
     * The String version of the current time, its value is set by the [Transformations.map] method
     * with a lambda which converts our [currentTime] field to a [String]
     */
    val currentTimeString = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time)
    }

    /**
     * The Hint for the current word
     */
    val wordHint = Transformations.map(word) { word ->
        val randomPosition = (1..word.length).random()
        "Current word has " + word.length + " letters" +
                "\nThe letter at position " + randomPosition + " is " +
            word[randomPosition - 1].uppercaseChar()
    }

    private val timer: CountDownTimer

    /**
     * We set the value of our `MutableLiveData` field `_word` to the empty string (it is
     * the backing field for our `LiveData` field `word`), and the value of our
     * `MutableLiveData` field `_score` to 0 (it is the backing field for our `LiveData`
     * field `score`). We then call our `resetList` method to initialize our
     * `MutableList<String>` field `wordList` to a shuffled List of words to guess, then call
     * our `nextWord` method to initialize our `word` field to the zeroth entry in `wordList`
     * and remove that word from the list. We then initialize our `CountDownTimer` field `timer`
     * to a new instance which will countdown from COUNTDOWN_TIME milliseconds to 0 using an
     * interval of ONE_SECOND milliseconds and override its `onTick` callback to update the value
     * of our `MutableLiveData<Long>` field `_currentTime`, and its `onFinish` callback to call
     * our method `onGameFinish`
     */
    init {
        _word.value = ""
        _score.value = 0
        resetList()
        nextWord()
        Log.i("GameViewModel", "GameViewModel created!")

        // Creates a timer which triggers the end of the game when it finishes
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished/ONE_SECOND
            }

            override fun onFinish() {
                _currentTime.value = DONE
                onGameFinish()
            }
        }

        timer.start()
    }

    /**
     * This method will be called when this [ViewModel] is no longer used and will be
     * destroyed. It is useful when [ViewModel] observes some data and you need to clear
     * this subscription to prevent a leak of this [ViewModel]. We call our super's
     * implementation of `onCleared` then call the `cancel` method of our [CountDownTimer]
     * field [timer] to cancel the countdown and free its resources.
     */
    override fun onCleared() {
        super.onCleared()
        // Cancel the timer
        timer.cancel()
    }

    /**
     * Resets the list of words and randomizes the order
     */
    fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list and removes it from the list. If our
     * `MutableList<String>` field [wordList] is empty (all words have been guessed at)
     * we call our method [resetList] to refill the list. Otherwise we set the value of our
     * `MutableLiveData<String>` field [_word] (private backing field of our public
     * `LiveData<String>` field [word]) to the zeroth entry in our `MutableList<String>`
     * field [wordList] removing that word from the list.
     */
    private fun nextWord() {
        // Shuffle the word list, if the list is empty
        if (wordList.isEmpty()) {
            resetList()
        } else {
            // Remove a word from the list
            _word.value = wordList.removeAt(0)
        }
    }

    /** Methods for buttons presses **/

    /**
     * Called by [GameFragment] when the user presses the `onSkip` button in the UI. If our
     * `MutableList<String>` field [wordList] is not empty we subtract one from our
     * `MutableLiveData<Int>` field [_score] (private backing field of our public
     * `LiveData<Int>` field [score]). We then call our method [nextWord] to advance to
     * the next word in [wordList].
     */
    fun onSkip() {
        if (wordList.isNotEmpty()) {
            _score.value = (score.value)?.minus(1)
        }
        nextWord()
    }

    /**
     * Called by [GameFragment] when the user presses the `onCorrect` button in the UI. If
     * our `MutableList<String>` field [wordList] is not empty we add one to our
     * `MutableLiveData<Int>` field [_score] (private backing field of our public
     * `LiveData<Int>` field [score]). We then call our method [nextWord] to advance to
     * the next word in [wordList]
     */
    fun onCorrect() {
        if (wordList.isNotEmpty()) {
            _score.value = (score.value)?.plus(1)
        }
        nextWord()
    }

    /**
     * Method for the game completed event. We just set the value of our
     * `MutableLiveData<Boolean>` field [_eventGameFinish] (private backing field of our
     * public `LiveData<Boolean>` field [eventGameFinish])to *true*.
     */
    fun onGameFinish() {
        _eventGameFinish.value = true
    }

    /**
     * Method for the game completed event. We just set the value of our
     * `MutableLiveData<Boolean>` field [_eventGameFinish] (private backing field of our
     * public `LiveData<Boolean>` field [eventGameFinish])to *false*.
     */
    fun onGameFinishComplete() {
        _eventGameFinish.value = false
    }

    /**
     * Our static constants
     */
    companion object {

        /**
         * Time when the game is over
         */
        private const val DONE = 0L

        /**
         * Countdown time interval
         */
        private const val ONE_SECOND = 1000L

        /**
         * Total time for the game
         */
        private const val COUNTDOWN_TIME = 10000L

    }
}