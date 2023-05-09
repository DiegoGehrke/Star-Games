package com.star.games.android

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AdditionMathGame(
    private val showQuest: TextView,
    private val firstBtn: ImageView,
    private val secondBtn: ImageView,
    private val thirdBtn: ImageView,
    private val fourthBtn: ImageView,
    private val fifthBtn: ImageView,
    private val sixthBtn: ImageView,
    private val firstBtnTxt: TextView,
    private val secondBtnTxt: TextView,
    private val thirdBtnTxt: TextView,
    private val fourthBtnTxt: TextView,
    private val fifthBtnTxt: TextView,
    private val sixthBtnTxt: TextView,
) {
    var points: Int = 0
    var num1: Int = 0
    var num2: Int = 0
    var correctAnswer: Int = 0


    private fun setOnClickListeners(){
        firstBtn.setOnClickListener { checkAnswer(firstBtnTxt.text.toString()) }
        secondBtn.setOnClickListener { checkAnswer(secondBtnTxt.text.toString()) }
        thirdBtn.setOnClickListener { checkAnswer(thirdBtnTxt.text.toString()) }
        fourthBtn.setOnClickListener { checkAnswer(fourthBtnTxt.text.toString()) }
        fifthBtn.setOnClickListener { checkAnswer(fifthBtnTxt.text.toString()) }
        sixthBtn.setOnClickListener { checkAnswer(sixthBtnTxt.text.toString()) }
    }

    private fun setRandomTexts(){
        val buttonValues = mutableListOf<Int>()
        for (i in 1..5) {
            var randomValue = (1..20).random()
            while (randomValue == correctAnswer) {
                randomValue = (1..20).random()
            }
            buttonValues.add(randomValue)
        }
        buttonValues.add(correctAnswer)
        buttonValues.shuffle()
        firstBtnTxt.text = buttonValues[0].toString()
        secondBtnTxt.text = buttonValues[1].toString()
        thirdBtnTxt.text = buttonValues[2].toString()
        fourthBtnTxt.text = buttonValues[3].toString()
        fifthBtnTxt.text = buttonValues[4].toString()
        sixthBtnTxt.text = buttonValues[5].toString()
    }

    private fun generateDivisionQuestion() {
        num1 = (1..10).random()
        num2 = (1..num1).random()
        correctAnswer = num1 / num2
        showQuest.text = "$num1 ÷ $num2 = ?"
        setRandomTexts()
        setOnClickListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun generateMultiplicationQuestion() {
        num1 = (1..10).random()
        num2 = (1..10).random()
        correctAnswer = num1 * num2
        showQuest.text = "$num1 x $num2 = ?"
        setRandomTexts()
        setOnClickListeners()
    }

    private fun generateSubtractionQuestion() {
        num1 = (1..10).random()
        num2 = (1..num1).random()
        correctAnswer = num1 - num2
        showQuest.text = "$num1 - $num2 = ?"
        setRandomTexts()
        setOnClickListeners()
    }

    fun generateAdditionQuestion() {
        num1 = (1..10).random()
        num2 = (1..10).random()
        correctAnswer = num1 + num2
        showQuest.text = "$num1 + $num2 = ?"
        setRandomTexts()
        setOnClickListeners()
    }

    private fun checkAnswer(userAnswer: String): Boolean {
        return if (userAnswer.toInt() == correctAnswer) {
            points++
            if (points in 0..20){
                generateAdditionQuestion()
            }
            else if (points in 21..40) {
                generateSubtractionQuestion()
            }
            else if (points in 41..50){
                generateMultiplicationQuestion()
            }
            else if (points in 51..60){
                generateDivisionQuestion()
            }
            else if (points > 60){
                val user = FirebaseAuth.getInstance().currentUser
                val uid = user?.uid
                val db = FirebaseFirestore.getInstance()
                val userRef = db.collection("USERS").document(uid!!)
                val coinsToAdd = 500
                userRef.update("coins", FieldValue.increment(coinsToAdd.toLong()))
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener { e ->

                    }
            }
            true
        } else {

            false
        }
    }
}