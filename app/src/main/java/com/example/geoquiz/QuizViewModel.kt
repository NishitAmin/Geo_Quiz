package com.example.geoquiz

import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {

    val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true))

    var currentIndex = 0

    var isCheater = false

    var ansQues = 1

    var value : IntArray = intArrayOf(questionBank.size)

    var correct = 0

    var incorrect = 0

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].testResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }
}