package com.example.android.guesstheword.screens.game

import android.util.Log
import androidx.lifecycle.ViewModel

/**
 * The [ViewModel] that holds all the data and business logic for the [GameFragment].
 */
@Suppress("MemberVisibilityCanBePrivate")
class GameViewModel : ViewModel() {
    /**
     * The current word
     */
    var word = ""
    /**
     * The current score
     */
    var score = 0
    /**
     * The list of words - the front of the list is the next word to guess
     */
    lateinit var wordList: MutableList<String>

    /**
     * We call our `resetList` method to initialize our `MutableList<String>` field `wordList` to
     * a shuffled List of words to guess, then call our `nextWord` method to initialize our `word`
     * field to the zeroth entry in `wordList` and remove that word from the list.
     */
    init {
        resetList()
        nextWord()
        Log.i("GameViewModel", "GameViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel destroyed!")
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
     * Moves to the next word in the list and removes it from the list.
     */
    fun nextWord() {
        if (wordList.isNotEmpty()) {
            //Select and remove a word from the list
            word = wordList.removeAt(0)
        }
    }

    /** Methods for buttons presses **/

    /**
     * Called by [GameFragment] when the user presses the `onSkip` button in the UI. If our
     * `MutableList<String>` field [wordList] is not empty we subtract one from our [score]
     * field. We then call our method [nextWord] to advance to the next word in [wordList]
     */
    fun onSkip() {
        if (wordList.isNotEmpty()) {
            score--
        }
        nextWord()
    }

    /**
     * Called by [GameFragment] when the user presses the `onCorrect` button in the UI. If our
     * `MutableList<String>` field [wordList] is not empty we add one to our [score] field.
     * We then call our method [nextWord] to advance to the next word in [wordList]
     */
    fun onCorrect() {
        if (wordList.isNotEmpty()) {
            score++
        }
        nextWord()
    }
}