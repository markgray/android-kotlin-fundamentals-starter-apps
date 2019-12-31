package com.example.android.guesstheword.screens.score

import android.util.Log
import androidx.lifecycle.ViewModel

/**
 * [ViewModel] containing the "score" that is displayed by the [ScoreFragment]
 */
class ScoreViewModel(finalScore: Int) : ViewModel() {
    // The final score
    var score = finalScore
    init {
        Log.i("ScoreViewModel", "Final score is $finalScore")
    }
}
