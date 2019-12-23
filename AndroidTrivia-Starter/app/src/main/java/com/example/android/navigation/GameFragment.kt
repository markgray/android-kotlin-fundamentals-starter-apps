/*
 * Copyright 2018, The Android Open Source Project
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

package com.example.android.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.android.navigation.databinding.FragmentGameBinding

/**
 * [Fragment] containing the "business" logic of our app, which consists of asking three Android
 * related multiple choice questions and evaluating the answers.
 */
class GameFragment : Fragment() {
    /**
     * Container class for the questions we can ask.
     */
    data class Question(
            val text: String,
            val answers: List<String>)

    /**
     * The first answer is the correct one. We randomize the answers before showing the text.
     * All questions must have four answers. We'd want these to contain references to string
     * resources so we could internationalize. (Or better yet, don't define the questions in code...)
     */
    private val questions: MutableList<Question> = mutableListOf(
            Question(text = "What is Android Jetpack?",
                    answers = listOf("All of these", "Tools", "Documentation", "Libraries")),
            Question(text = "What is the base class for layouts?",
                    answers = listOf("ViewGroup", "ViewSet", "ViewCollection", "ViewRoot")),
            Question(text = "What layout do you use for complex screens?",
                    answers = listOf("ConstraintLayout", "GridLayout", "LinearLayout", "FrameLayout")),
            Question(text = "What do you use to push structured data into a layout?",
                    answers = listOf("Data binding", "Data pushing", "Set text", "An OnClick method")),
            Question(text = "What method do you use to inflate layouts in fragments?",
                    answers = listOf("onCreateView()", "onActivityCreated()", "onCreateLayout()", "onInflateLayout()")),
            Question(text = "What's the build system for Android?",
                    answers = listOf("Gradle", "Graddle", "Grodle", "Groyle")),
            Question(text = "Which class do you use to create a vector drawable?",
                    answers = listOf("VectorDrawable", "AndroidVectorDrawable", "DrawableVector", "AndroidVector")),
            Question(text = "Which one of these is an Android navigation component?",
                    answers = listOf("NavController", "NavCentral", "NavMaster", "NavSwitcher")),
            Question(text = "Which XML element lets you register an activity with the launcher activity?",
                    answers = listOf("intent-filter", "app-registry", "launcher-registry", "app-launcher")),
            Question(text = "What do you use to mark a layout for data binding?",
                    answers = listOf("<layout>", "<binding>", "<data-binding>", "<dbinding>"))
    )

    /**
     * The current [Question] being asked, its `text` field is bound to the android:text attribute
     * of the `TextView` with android:id `questionText` in our R.layout.fragment_game layout file.
     */
    lateinit var currentQuestion: Question
    /**
     * The [MutableList] of four answer choices for our the [currentQuestion] currently being asked,
     * they are bound to the android:text attribute of the four `RadioButton` answer choosers in our
     * R.layout.fragment_game layout file.
     */
    lateinit var answers: MutableList<String>
    /**
     * Index of the question in our [MutableList] of [Question]'s field [questions] that is
     * currently being asked
     */
    private var questionIndex = 0
    /**
     * Number of questions we ask for each playing of the game (a constant 3 actually).
     */
    private val numQuestions = ((questions.size + 1) / 2).coerceAtMost(3)

    /**
     * Called to have the fragment instantiate its user interface view. We use the method
     * [DataBindingUtil.inflate] to use our [LayoutInflater] parameter [inflater] to inflate our
     * layout file R.layout.fragment_game using our [ViewGroup] parameter [container] for its
     * `LayoutParams` without attaching to it in order to initialize our variable `val binding`
     * to the [FragmentGameBinding] for our layout file (`binding` will then be used to automagically
     * fill our UI with the current text information for our game state). Next we call our method
     * [randomizeQuestions] to shuffle our questions and initialize our [questionIndex] field to 0.
     * We set the `game` property of `binding to *this* (`game` is a `variable` of the `type`
     * of our [Fragment] "com.example.android.navigation.GameFragment" defined in our layout file
     * which is used by the binding framework to update the text of the UI from our fields). Next
     * we use `binding` to find the `submitButton` in our UI and set its `OnClickListener` to a
     * lambda which checks the answer to our question which the user chose with the `RadioButton`'s
     * against the correct answer and navigates to the `GameOverFragment` if it is wrong, or to ask
     * the next question if correct as long as there are more questions to ask, and if no more
     * questions are left it navigates to the `GameWonFragment`.
     *
     * Finally we return the `root` [View] of `binding` to the caller (this is outermost [View] in
     * the layout file associated with the Binding).
     *
     * @param inflater The [LayoutInflater] object that can be used to inflate any views in the
     * fragment
     * @param container If non-null, this is the parent view that the fragment's UI will be attached
     * to.  The fragment should not add the view itself, but this can be used to generate the
     * `LayoutParams` of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     * saved state as given here.
     * @return Return the [View] for the fragment's UI, or null.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        /**
         * Inflate the layout for this fragment
         */
        val binding = DataBindingUtil.inflate<FragmentGameBinding>(
                inflater, R.layout.fragment_game, container, false)

        /**
         * Shuffles the questions and sets the question index to the first question.
         */
        randomizeQuestions()

        /**
         * Bind this fragment class to the layout
         */
        binding.game = this

        /**
         * Set the onClickListener for the submitButton. We initialize our variable `val checkedId`
         * by using our `binding` [FragmentGameBinding] variable to find the `RadioGroup` in our
         * layout file with id `questionRadioGroup` and retrieving its `checkedRadioButtonId`
         * property (identifier of the selected radio button in this group). If this is -1 (no
         * button selected) we do nothing. Otherwise we set our variable `var answerIndex` to 0
         * and use a *when* block to set `answerIndex` to 1, 2 or 3 if the ID in `checkedId` is
         * `secondAnswerRadioButton`, `thirdAnswerRadioButton` or `fourthAnswerRadioButton`
         * respectively. Then if the `answerIndex` entry of the list of answers contained in our
         * [answers] field (the text of the answer chosen by the user) is equal to the zeroth entry
         * in the original un-shuffled answers contained in the `answers` list of the current
         * question [currentQuestion] (always the correct one) we increment the [questionIndex] to
         * point to the next question in our [questions] field and if this is less than [numQuestions]
         * we set [currentQuestion] to the [questionIndex] entry in [questions] call our [setQuestion]
         * method to set the question and randomize the answers then call the `invalidateAll` method
         * of `binding` to invalidate all binding expressions and request a new rebind to refresh
         * the UI. If the user has already answered [numQuestions] questions we call the method
         * [View.findNavController] to find the `NavController` associated with the [View] `view`
         * that was clicked and use its `navigate` method to navigate to the `GameWonFragment`.
         *
         * If the answer chosen by the user is wrong on the otherhand we call the method
         * [View.findNavController] to find the `NavController` associated with the [View] `view`
         * that was clicked and use its `navigate` method to navigate to the `GameOverFragment`
         */
        binding.submitButton.setOnClickListener { view: View ->
            val checkedId = binding.questionRadioGroup.checkedRadioButtonId
            /**
             * Do nothing if nothing is checked (id == -1)
             */
            if (-1 != checkedId) {
                var answerIndex = 0
                when (checkedId) {
                    R.id.secondAnswerRadioButton -> answerIndex = 1
                    R.id.thirdAnswerRadioButton -> answerIndex = 2
                    R.id.fourthAnswerRadioButton -> answerIndex = 3
                }
                /**
                 * The first answer in the original question is always the correct one, so if our
                 * answer matches, we have the correct answer.
                 */
                if (answers[answerIndex] == currentQuestion.answers[0]) {
                    questionIndex++
                    /**
                     * Advance to the next question
                     */
                    if (questionIndex < numQuestions) {
                        currentQuestion = questions[questionIndex]
                        setQuestion()
                        binding.invalidateAll()
                    } else {
                        /**
                         * We've won!  Navigate to the gameWonFragment.
                         */
                        view.findNavController()
                                .navigate(R.id.action_gameFragment_to_gameWonFragment)
                    }
                } else {
                    /**
                     * Game over! A wrong answer sends us to the gameOverFragment.
                     */
                    view.findNavController().
                            navigate(R.id.action_gameFragment_to_gameOverFragment)
                }
            }
        }
        return binding.root
    }

    /**
     * Randomize the questions and set the first question. We use the [MutableList.shuffle] method
     * of our [MutableList] field [questions] to shuffle the [Question]'s, set our next question
     * index field [questionIndex] to 0, then call our [setQuestion] method to set the question and
     * randomize the answers.
     */
    private fun randomizeQuestions() {
        questions.shuffle()
        questionIndex = 0
        setQuestion()
    }

    /**
     * Sets the question and randomizes the answers. This only changes the data, not the UI. Calling
     * `invalidateAll` on the [FragmentGameBinding] updates the data. We set our [Question] field
     * [currentQuestion] to the [Question] in our [questions] field with index [questionIndex]. We
     * then initialize our [MutableList] field [answers] with a copy of the `answers` [List] field
     * and shuffle this copy. We fetch the `FragmentActivity` our fragment is currently associated
     * with, cast it to [AppCompatActivity] in order to fetch a handle to its `ActionBar` which we
     * use to set its title to a formatted [String] displaying "Android Trivia", the number of the
     * question being asked ([questionIndex] plus 1) and the number of questions we intend to ask
     * [numQuestions].
     */
    private fun setQuestion() {
        currentQuestion = questions[questionIndex]
        /**
         * randomize the answers into a copy of the array
         */
        answers = currentQuestion.answers.toMutableList()
        /**
         * and shuffle them
         */
        answers.shuffle()

        (activity as AppCompatActivity).supportActionBar?.title = getString(
                R.string.title_android_trivia_question,
                questionIndex + 1,
                numQuestions
        )
    }
}
