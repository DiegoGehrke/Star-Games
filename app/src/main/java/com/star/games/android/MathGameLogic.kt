package com.star.games.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlin.math.roundToInt

class MathGameLogic(
    private val showQuest: TextView,
    private val firstBtn: ImageView,
    private val secondBtn: ImageView,
    private val thirdBtn: ImageView,
    private val fourthBtn: ImageView,
    private val fifthBtn: ImageView,
    private val sixthBtn: ImageView,
    private var firstBtnTxt: TextView,
    private var secondBtnTxt: TextView,
    private var thirdBtnTxt: TextView,
    private var fourthBtnTxt: TextView,
    private var fifthBtnTxt: TextView,
    private var sixthBtnTxt: TextView,
    private var coin: Float,
    private var collectStarsProgress: Int,
    private val context: Context,
    private var phaseCounterTxt: TextView,
    private var mistakesCounterTxt: TextView,
    private val window: Window,
    private val getIntentExtraValue: Boolean,
    private var vipLevel: Int
) {
    private var points: Int = 0
    private var num1: Int = 0
    private var num2: Int = 0
    private var errors: Int = 0
    private var correctAnswer: Int = 0
    private var bonusAmount: Float = 0F
    private var mathHighestReward: Float = 12.00f
    private var mathMediumReward: Float = 10.00f
    private var mathLowestReward: Float = 8.00f

    private var firstPhase: Boolean = false
    private var secondPhase: Boolean = false
    /*private var thirdPhase: Boolean = false
    private var fourthPhase: Boolean = false
    private var fifthPhase: Boolean = false
    private var sixthPhase: Boolean = false*/

    private var firebaseRemoteConfig: FirebaseRemoteConfig
    private var configSettings: FirebaseRemoteConfigSettings

    private var map: HashMap<String, Any>

    private val user = FirebaseAuth.getInstance().currentUser
    private val db = Firebase.firestore
    private val uid = user!!.uid
    private val dailyEvent = db.collection("DAILY_EVENT").document(uid)
    private val userData = db.collection("USERS").document(uid)

    private var playerCorrectAnswer: ExoPlayer
    private var correctAnswerMediaItem: MediaItem
    private var playerWrongAnswer: ExoPlayer
    private var wrongAnswerMediaItem: MediaItem

    init {
        setOnClickListeners()
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        configSettings = FirebaseRemoteConfigSettings
            .Builder()
            .setMinimumFetchIntervalInSeconds(1)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        map = HashMap()
        map["MATH_HIGHEST_REWARD"] = mathHighestReward
        map["MATH_MEDIUM_REWARD"] = mathMediumReward
        map["MATH_LOWEST_REWARD"] = mathLowestReward
        firebaseRemoteConfig.setDefaultsAsync(map)
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mathHighestReward = firebaseRemoteConfig.getDouble("MATH_HIGHEST_REWARD").toFloat()
                    mathMediumReward = firebaseRemoteConfig.getDouble("MATH_MEDIUM_REWARD").toFloat()
                    mathLowestReward = firebaseRemoteConfig.getDouble("MATH_LOWEST_REWARD").toFloat()
                } else {
                    ErrorToastUtils.showCustomErrorToast(
                        context,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_LONG,
                        this.window
                    )
                }
            }

        //correct
        playerCorrectAnswer = ExoPlayer.Builder(context).build()
        correctAnswerMediaItem = MediaItem.fromUri(
            "android.resource://com.star.games.android/${R.raw.correct_answer_sound}"
        )
        playerCorrectAnswer.setMediaItem(correctAnswerMediaItem)
        playerCorrectAnswer.prepare()

        //wrong
        playerWrongAnswer = ExoPlayer.Builder(context).build()
        wrongAnswerMediaItem = MediaItem.fromUri(
            "android.resource://com.star.games.android/${R.raw.wrong_answer_second_sound}"
        )
        playerWrongAnswer.setMediaItem(wrongAnswerMediaItem)
        playerWrongAnswer.prepare()
    }

    fun readCurrentFirstPhaseStatus(): Boolean = firstPhase
    fun readCurrentSecondPhaseStatus(): Boolean = secondPhase
    /*fun readCurrentThirdPhaseStatus(): Boolean = thirdPhase
    fun readCurrentFourthPhaseStatus(): Boolean = fourthPhase
    fun readCurrentFifthPhaseStatus(): Boolean = fifthPhase
    fun readCurrentSixthPhaseStatus(): Boolean = sixthPhase*/

    private fun setOnClickListeners() {
        firstBtn.setOnClickListener {it as ImageView
            val answer: String = firstBtnTxt.text.toString()
            val isCorrect: Boolean = checkAnswer(answer)
            if (isCorrect) {
                playerCorrectAnswer.seekToDefaultPosition()
                playerCorrectAnswer.play()
                it.setImageResource(R.drawable.math_btn_correct)
                points++
                disableAllButtons()
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    checkCurrentPhase()
                    resetButtons()
                    generateAdditionQuestion()
                    enableAllButtons()
                }, 250)
            } else {
                playerWrongAnswer.seekToDefaultPosition()
                playerWrongAnswer.play()
                checkHowManyErrorsHave()
                it.setImageResource(R.drawable.math_btn_incorrect)
                val correctButton = findCorrectButton()
                correctButton.setImageResource(R.drawable.math_btn_correct)
                disableAllButtons()
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    resetButtons()
                    generateAdditionQuestion()
                    enableAllButtons()
                }, 250)
            }
        }
        secondBtn.setOnClickListener {it as ImageView
            val answer: String = secondBtnTxt.text.toString()
            val isCorrect: Boolean = checkAnswer(answer)
            if (isCorrect) {
                playerCorrectAnswer.seekToDefaultPosition()
                playerCorrectAnswer.play()
                it.setImageResource(R.drawable.math_btn_correct)
                points++
                disableAllButtons()
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    checkCurrentPhase()
                    resetButtons()
                    generateAdditionQuestion()
                    enableAllButtons()
                }, 250)
            } else {
                playerWrongAnswer.seekToDefaultPosition()
                playerWrongAnswer.play()
                checkHowManyErrorsHave()
                it.setImageResource(R.drawable.math_btn_incorrect)
                val correctButton = findCorrectButton()
                correctButton.setImageResource(R.drawable.math_btn_correct)
                disableAllButtons()
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    resetButtons()
                    generateAdditionQuestion()
                    enableAllButtons()
                }, 250)
            }
        }
        thirdBtn.setOnClickListener { it as ImageView
            val answer: String = thirdBtnTxt.text.toString()
            val isCorrect: Boolean = checkAnswer(answer)
            if (isCorrect) {
                playerCorrectAnswer.seekToDefaultPosition()
                playerCorrectAnswer.play()
                it.setImageResource(R.drawable.math_btn_correct)
                points++
                disableAllButtons()
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    checkCurrentPhase()
                    resetButtons()
                    generateAdditionQuestion()
                    enableAllButtons()
                }, 250)
            } else {
                playerWrongAnswer.seekToDefaultPosition()
                playerWrongAnswer.play()
                checkHowManyErrorsHave()
                it.setImageResource(R.drawable.math_btn_incorrect)
                val correctButton = findCorrectButton()
                correctButton.setImageResource(R.drawable.math_btn_correct)
                disableAllButtons()
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    resetButtons()
                    generateAdditionQuestion()
                    enableAllButtons()
                }, 250)
            }
        }
        fourthBtn.setOnClickListener {it as ImageView
            val answer: String = fourthBtnTxt.text.toString()
            val isCorrect: Boolean = checkAnswer(answer)
            if (isCorrect) {
                playerCorrectAnswer.seekToDefaultPosition()
                playerCorrectAnswer.play()
                it.setImageResource(R.drawable.math_btn_correct)
                points++
                disableAllButtons()
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    checkCurrentPhase()
                    resetButtons()
                    generateAdditionQuestion()
                    enableAllButtons()
                }, 250)
            } else {
                playerWrongAnswer.seekToDefaultPosition()
                playerWrongAnswer.play()
                checkHowManyErrorsHave()
                it.setImageResource(R.drawable.math_btn_incorrect)
                val correctButton = findCorrectButton()
                correctButton.setImageResource(R.drawable.math_btn_correct)
                disableAllButtons()
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    resetButtons()
                    generateAdditionQuestion()
                    enableAllButtons()
                }, 250)
            }
        }
        fifthBtn.setOnClickListener { it as ImageView
            val answer: String = fifthBtnTxt.text.toString()
            val isCorrect: Boolean = checkAnswer(answer)
            if (isCorrect) {
                playerCorrectAnswer.seekToDefaultPosition()
                playerCorrectAnswer.play()
                it.setImageResource(R.drawable.math_btn_correct)
                points++
                disableAllButtons()
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    checkCurrentPhase()
                    resetButtons()
                    generateAdditionQuestion()
                    enableAllButtons()
                }, 250)
            } else {
                playerWrongAnswer.seekToDefaultPosition()
                playerWrongAnswer.play()
                checkHowManyErrorsHave()
                it.setImageResource(R.drawable.math_btn_incorrect)
                val correctButton = findCorrectButton()
                correctButton.setImageResource(R.drawable.math_btn_correct)
                disableAllButtons()
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    resetButtons()
                    generateAdditionQuestion()
                    enableAllButtons()
                }, 250)
            }
        }
        sixthBtn.setOnClickListener { it as ImageView
            val answer: String = sixthBtnTxt.text.toString()
            val isCorrect: Boolean = checkAnswer(answer)
            if (isCorrect) {
                playerCorrectAnswer.seekToDefaultPosition()
                playerCorrectAnswer.play()
                it.setImageResource(R.drawable.math_btn_correct)
                points++
                disableAllButtons()
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    checkCurrentPhase()
                    resetButtons()
                    generateAdditionQuestion()
                    enableAllButtons()
                }, 250)
            } else {
                playerWrongAnswer.seekToDefaultPosition()
                playerWrongAnswer.play()
                checkHowManyErrorsHave()
                it.setImageResource(R.drawable.math_btn_incorrect)
                val correctButton = findCorrectButton()
                correctButton.setImageResource(R.drawable.math_btn_correct)
                disableAllButtons()
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    resetButtons()
                    generateAdditionQuestion()
                    enableAllButtons()
                }, 250)
            }
        }
    }

    private fun checkHowManyErrorsHave() {
        errors += 1
        mistakesCounterTxt.text = context.getString(R.string.errors, errors)
        if (errors > 5) {
            ErrorToastUtils.showCustomErrorToast(
                context,
                context.getString(R.string.you_lose_the_game),
                Toast.LENGTH_LONG,
                this.window
            )
            if (context is Activity) {
                val intent = Intent(context, GamesCenterActivity::class.java)
                playerWrongAnswer.release()
                playerCorrectAnswer.release()
                context.startActivity(intent)
                context.finish()
            }
        }
    }

    private fun findCorrectButton(): ImageView {
        val buttonMap = mapOf(
            firstBtnTxt.text.toString().toInt() to firstBtn,
            secondBtnTxt.text.toString().toInt() to secondBtn,
            thirdBtnTxt.text.toString().toInt() to thirdBtn,
            fourthBtnTxt.text.toString().toInt() to fourthBtn,
            fifthBtnTxt.text.toString().toInt() to fifthBtn,
            sixthBtnTxt.text.toString().toInt() to sixthBtn
        )
        return buttonMap[correctAnswer]!!
    }

    private fun resetButtons() {
        val buttons = listOf(firstBtn, secondBtn, thirdBtn, fourthBtn, fifthBtn, sixthBtn)
        buttons.forEach { it.setImageResource(R.drawable.math_btn) }
    }

    private fun enableAllButtons() {
        firstBtn.isEnabled = true
        secondBtn.isEnabled = true
        thirdBtn.isEnabled = true
        fourthBtn.isEnabled = true
        fifthBtn.isEnabled = true
        sixthBtn.isEnabled = true
    }

    private fun disableAllButtons() {
        firstBtn.isEnabled = false
        secondBtn.isEnabled = false
        thirdBtn.isEnabled = false
        fourthBtn.isEnabled = false
        fifthBtn.isEnabled = false
        sixthBtn.isEnabled = false
    }

    private fun setRandomTexts() {
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

    /*private fun generateDivisionQuestion() {
        num1 = (1..10).random()
        num2 = (1..num1).random()
        correctAnswer = num1 / num2
        showQuest.text = context.getString(R.string.show_division_math_operation, num1, num2)
        setRandomTexts()
    }*/

   /* private fun generateMultiplicationQuestion() {
        num1 = (1..10).random()
        num2 = (1..10).random()
        correctAnswer = num1 * num2
        showQuest.text = "$num1 x $num2 = ?"
        setRandomTexts()
    }*/

    /*private fun generateSubtractionQuestion() {
        num1 = (1..10).random()
        num2 = (1..num1).random()
        correctAnswer = num1 - num2
        showQuest.text = context.getString(R.string.show_subtraction_math_operation, num1, num2)
        setRandomTexts()
    }*/

    fun generateAdditionQuestion() {
        num1 = (1..10).random()
        num2 = (1..10).random()
        correctAnswer = num1 + num2
        showQuest.text = context.getString(R.string.show_addiction_math_operation, num1, num2)
        setRandomTexts()
    }

    private fun checkAnswer(userAnswer: String): Boolean {
        return userAnswer.toInt() == correctAnswer
    }

    private fun checkCurrentPhase() {
        if (points in 0..3) {
            val phase = "1/2"
            phaseCounterTxt.text = context.getString(R.string.phase, phase)
        } else if (points in 4..7) {
            val phase = "2/2"
            phaseCounterTxt.text = context.getString(R.string.phase, phase)
            firstPhase = true
        } else if (points > 7) {
            firstPhase = false
            secondPhase = true
            val loadingDialog = LoadingDialog(context)
            loadingDialog.show()
            updateUserCoinsAndEventProgress()
        }
    }

    private fun updateUserCoinsAndEventProgress() {
        if (!getIntentExtraValue) {
            val bonusPercentage = if (vipLevel < 33) {
                (vipLevel * 3) / 100f
            } else {
                (vipLevel * 4) / 100f
            }
            var baseBonusAmount: Float = if (firstPhase) {
                mathMediumReward
            } else if (secondPhase) {
                mathHighestReward
            } else {
                mathLowestReward
            }
            bonusAmount = baseBonusAmount * bonusPercentage
            baseBonusAmount += bonusAmount

            val newCoinAmount: Float = coin + baseBonusAmount
            val newProgressAmount: Int = collectStarsProgress + baseBonusAmount.roundToInt()
            val updateDailyEventData: HashMap<String, Any> = hashMapOf(
                "collectedStars" to newProgressAmount
            )
            userData
                .update("coins", newCoinAmount)
                .addOnSuccessListener {
                    dailyEvent
                        .get()
                        .addOnSuccessListener {
                            dailyEvent.update(updateDailyEventData)
                                .addOnSuccessListener {
                                    val intent = Intent(context, GamesCenterActivity::class.java)
                                    intent.putExtra("earnedCoins", baseBonusAmount)
                                    playerWrongAnswer.release()
                                    playerCorrectAnswer.release()
                                    context.startActivity(intent)
                                    if (context is Activity) {
                                        context.finish()
                                    }
                                }
                        }
                }
                .addOnFailureListener { exception ->
                    ErrorToastUtils.showCustomErrorToast(
                        context,
                        context.getString(R.string.error_, exception.message.toString()),
                        Toast.LENGTH_LONG,
                        this.window
                    )
                }
    } else {
            val intent = Intent(context, GamesCenterActivity::class.java)
            playerWrongAnswer.release()
            playerCorrectAnswer.release()
            context.startActivity(intent)
            if (context is Activity) {
                context.finish()
            }
        }
    }
}
