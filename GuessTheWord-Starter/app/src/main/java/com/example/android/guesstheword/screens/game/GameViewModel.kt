package com.example.android.guesstheword.screens.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
     * We set the value of our `MutableLiveData` field `_word` to the empty string (it is
     * the backing field for our `LiveData` field `word`), and the value of our
     * `MutableLiveData` field `_score` to 0 (it is the backing field for our `LiveData`
     * field `score`). We then call our `resetList` method to initialize our
     * `MutableList<String>` field `wordList` to a shuffled List of words to guess, then call
     * our `nextWord` method to initialize our `word` field to the zeroth entry in `wordList`
     * and remove that word from the list.
     */
    init {
        _word.value = ""
        _score.value = 0
        resetList()
        nextWord()
        Log.i("GameViewModel", "GameViewModel created!")
    }

    /**
     * This method will be called when this [ViewModel] is no longer used and will be
     * destroyed. It is useful when [ViewModel] observes some data and you need to clear
     * this subscription to prevent a leak of this [ViewModel]. We just call our super's
     * implementation of `onCleared`.
     */
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
     * Moves to the next word in the list and removes it from the list. If our
     * `MutableList<String>` field [wordList] is empty (all words have been guessed at)
     * we call our method [onGameFinish]. Otherwise we set the value of our
     * `MutableLiveData<String>` field [_word] (private backing field of our public
     * `LiveData<String>` field [word]) to the zeroth entry in our `MutableList<String>`
     * field [wordList] removing that word from the list.
     */
    fun nextWord() {
        if (wordList.isEmpty()) {
            onGameFinish()
        } else {
            //Select and remove a _word from the list
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
}