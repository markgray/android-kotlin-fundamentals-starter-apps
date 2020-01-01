package com.example.android.guesstheword.screens.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * [ViewModel] containing the "score" that is displayed by the [ScoreFragment]
 */
class ScoreViewModel(finalScore: Int) : ViewModel() {

    /**
     * The private final score Mutable backing field
     */
    private val _score = MutableLiveData<Int>()
    /**
     * The public final score immutable field
     */
    val score: LiveData<Int>
        get() = _score

    /**
     * The private play again backing field for [eventPlayAgain]
     */
    private val _eventPlayAgain = MutableLiveData<Boolean>()
    /**
     * The public play again immutable field, will trigger an observer which will navigate back to
     * the `GameFragment` when it changes to *true*.
     */
    val eventPlayAgain: LiveData<Boolean>
        get() = _eventPlayAgain

    /**
     * We just set the value of our private final score Mutable backing field `_score` to our
     * constructor parameter `finalScore`.
     */
    init {
        Log.i("ScoreViewModel", "Final score is $finalScore")
        _score.value = finalScore
    }

    /**
     * This is called when the "Play Again" button in our [ScoreFragment] UI is clicked. Setting the
     * backing field of our [eventPlayAgain] field to *true* will trigger the observer to  navigate
     * back to the `GameFragment`. We just set the value of our `MutableLiveData<Boolean>` field
     * [_eventPlayAgain] to *true*.
     */
    fun onPlayAgain() {
        _eventPlayAgain.value = true
    }

    /**
     * This is called to reset the backing field of our [eventPlayAgain] field to *false* after the
     * [ScoreFragment] starts the navigation back to the `GameFragment`.
     */
    fun onPlayAgainComplete() {
        _eventPlayAgain.value = false
    }
}
