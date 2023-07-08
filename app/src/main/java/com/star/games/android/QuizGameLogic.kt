package com.star.games.android

import android.content.Context
import android.view.Window

class QuizGameLogic (val context: Context, val window: Window) {
    private val questions: MutableList<Question> = mutableListOf()
    private val usedQuestionIndices: MutableList<Int> = mutableListOf()
    private var currentQuestionIndex: Int = 0

    fun addQuestion(question: Question) {
        questions.add(question)
    }

    fun startQuiz() {
        usedQuestionIndices.clear()
        goToNextQuestion()
    }

    fun getCurrentQuestion(): Question {
        return questions[currentQuestionIndex]
    }

    fun answerQuestion(selectedAnswer: String): Boolean {
        val currentQuestion: Question = getCurrentQuestion()

        return currentQuestion.correctAnswer == selectedAnswer
    }

     fun goToNextQuestion() {
         usedQuestionIndices.add(currentQuestionIndex)

         if (usedQuestionIndices.size == questions.size) {

             usedQuestionIndices.clear()
         }

         currentQuestionIndex = getRandomQuestionIndex()
    }

    fun isQuizFinished(): Boolean {
        return currentQuestionIndex >= questions.size
    }

    private fun getRandomQuestionIndex(): Int {
        val unusedQuestionIndices = (0 until questions.size).filterNot { usedQuestionIndices.contains(it) }
        return unusedQuestionIndices.random()
    }
}

data class Question(val questionText: String, val options: List<String>, val correctAnswer: String) {
    val optionsText: List<String> = options.mapIndexed { _, option ->
        option
    }
}




