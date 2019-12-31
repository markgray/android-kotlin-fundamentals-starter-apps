package com.example.android.guesstheword.screens.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * [ViewModelProvider.Factory] that creates a [ScoreViewModel] instance
 */
class ScoreViewModelFactory(private val finalScore: Int) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given [Class] (A [ScoreViewModel] in our case). We check to
     * make sure that `<T>` is a class or interface either the same as, or is a superclass or
     * superinterface of the class of [ScoreViewModel], and if so we return a new instance of
     * [ScoreViewModel] constructed using our field [finalScore] as its "score" (cast to a `<T>`).
     * Otherwise we throw an [IllegalArgumentException].
     *
     * @param modelClass a [Class] whose instance is requested
     * @param T          The type parameter for the ViewModel
     * @return a newly created ViewModel
     */
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(ScoreViewModel::class.java)) {
            return ScoreViewModel(finalScore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}