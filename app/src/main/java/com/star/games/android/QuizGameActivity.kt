package com.star.games.android

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.ironsource.mediationsdk.ISBannerSize
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.IronSourceBannerLayout
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener
import com.star.games.android.ErrorToastUtils.showCustomErrorToast
import com.star.games.android.SuccessToastUtils.showCustomSuccessToast
import java.util.Timer
import java.util.TimerTask
import kotlin.math.roundToInt


class QuizGameActivity : AppCompatActivity(){

    private lateinit var quizGameLogic: QuizGameLogic
    private lateinit var quizGameQuestions: QuizQuestions

    private lateinit var questionTextView: TextView
    private lateinit var optionOneTextView: TextView
    private lateinit var optionTwoTextView: TextView
    private lateinit var optionThreeTextView: TextView
    private lateinit var optionFourTextView: TextView
    private lateinit var gamePhaseCounterTxt: TextView
    private lateinit var secondsRemainingTxt: TextView
    private lateinit var errorsCounterTxt: TextView
    private lateinit var gameName: TextView

    private lateinit var progressBar: ProgressBar

    private lateinit var optionOneButton: ImageView
    private lateinit var optionTwoButton: ImageView
    private lateinit var optionThreeButton: ImageView
    private lateinit var optionFourthButton: ImageView
    private lateinit var backBtn: ImageView

    private var score: Int = 0
    private var errors: Int = 0
    private var progress: Int = 60
    private var vipLevel: Int = 0
    private var collectStarsProgress : Int = 0
    private var coin : Float = 0.00F
    private var getUserVipPoints : Long = 0
    private var confirmBackInt : Int = 0
    private var confirmBackWithBtnInt : Int = 0
    private var bonusAmount : Float = 0F
    private var savedProgress = 0

    private var gamePhase: String = "1/2"

    private lateinit var timerTask: TimerTask
    private var timer : Timer = Timer()
    private val backTimer = Timer()

    private var isFirstPhaseDone: Boolean = false
    private var isSecondPhaseDone: Boolean = false
    /*private var isThirdPhaseDone: Boolean = false
    private var isFourthPhaseDone: Boolean = false
    private var isFifthPhaseDone: Boolean = false
    private var isSixthPhaseDone: Boolean = false*/
    private var getIntentExtraValue : Boolean = false
    private var isTimerRunning = false

    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val uid = user!!.uid
    private val dailyEvent = db.collection("DAILY_EVENT").document(uid)
    private val userData = db.collection("USERS").document(uid)

    private lateinit var banner: IronSourceBannerLayout

    private lateinit var backToGamesCenterActivity: Intent

    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    private lateinit var configSettings: FirebaseRemoteConfigSettings

    private var quizHighestReward: Float = 12.00f
    private var quizMediumReward: Float = 10.00f
    private var quizLowestReward: Float = 8.00f

    private lateinit var map: HashMap<String, Any>

    private lateinit var playerBackgroundSound: ExoPlayer
    private lateinit var backgroundMediaItem: MediaItem
    private lateinit var playerCorrectAnswer: ExoPlayer
    private lateinit var correctAnswerMediaItem: MediaItem
    private lateinit var playerWrongAnswer: ExoPlayer
    private lateinit var wrongAnswerMediaItem: MediaItem

    private lateinit var countdownDialog: CountdownDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_gamew)

        banner = IronSource.createBanner(this@QuizGameActivity, ISBannerSize.BANNER)!!

        banner.levelPlayBannerListener = object : LevelPlayBannerListener {
            override fun onAdLoaded(adInfo: AdInfo) {}

            override fun onAdLoadFailed(error: IronSourceError) {
                showCustomErrorToast(
                    this@QuizGameActivity,
                    error.errorMessage.toString(),
                    Toast.LENGTH_LONG,
                    this@QuizGameActivity.window
                )
            }

            override fun onAdClicked(adInfo: AdInfo) {
                onPause()
            }

            override fun onAdScreenPresented(adInfo: AdInfo) {}

            override fun onAdScreenDismissed(adInfo: AdInfo) {}

            override fun onAdLeftApplication(adInfo: AdInfo) {}
        }
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        banner.layoutParams = layoutParams

        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraint_bckg)
        constraintLayout.addView(banner)
        IronSource.loadBanner(banner)

        initializeViews()
        setBackToPreviousActivityListener(this.window)
        fetchUserData()
        initializeQuizGame()
        setViewListener()
        textsUI()
    }

    override fun onPause() {
        super.onPause()
        isTimerRunning = false
        savedProgress = progress
        timer.cancel()
        timer.purge()
    }

    override fun onResume() {
        super.onResume()
        isTimerRunning = true
        timer = Timer()
    }

    override fun onDestroy() {
        super.onDestroy()
        IronSource.destroyBanner(this@QuizGameActivity.banner)
        timer.cancel()
        timerTask.cancel()
    }

    private fun initializeViews() {
        questionTextView = findViewById(R.id.quiz_quest_txt)
        optionOneTextView = findViewById(R.id.first_option_txt)
        optionTwoTextView = findViewById(R.id.second_option_txt)
        optionThreeTextView = findViewById(R.id.third_option_txt)
        optionFourTextView = findViewById(R.id.fourth_option_txt)
        gamePhaseCounterTxt = findViewById(R.id.phaseCounter)
        secondsRemainingTxt = findViewById(R.id.remainingSecondsTxt)
        gameName = findViewById(R.id.game_name)

        errorsCounterTxt = findViewById(R.id.mistakesCounterTxt)
        errorsCounterTxt.text = getString(R.string.errors, errors)

        optionOneButton = findViewById(R.id.first_option_btn)
        optionTwoButton = findViewById(R.id.second_option_btn)
        optionThreeButton = findViewById(R.id.third_option_btn)
        optionFourthButton = findViewById(R.id.fourth_option_btn)
        backBtn = findViewById(R.id.back_btn)

        progressBar = findViewById(R.id.timerProgressBar)

        backToGamesCenterActivity = Intent(
            this@QuizGameActivity,
            GamesCenterActivity::class.java
        )

        quizGameLogic = QuizGameLogic(this, this.window)
        quizGameQuestions = QuizQuestions(this)
        val questions: List<Question> = quizGameQuestions.getQuestions()
        for (question: Question in questions) {
            quizGameLogic.addQuestion(question)
        }

        if (intent.extras != null) {
            val getExtras: Bundle = intent.extras!!
            getIntentExtraValue = getExtras.containsKey("noHaveTicketsOrChances")
        }

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        configSettings = FirebaseRemoteConfigSettings
            .Builder()
            .setMinimumFetchIntervalInSeconds(1)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        map = HashMap()
        map["QUIZ_HIGHEST_REWARD"] = quizHighestReward
        map["QUIZ_MEDIUM_REWARD"] = quizMediumReward
        map["QUIZ_LOWEST_REWARD"] = quizLowestReward
        firebaseRemoteConfig.setDefaultsAsync(map)
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    quizHighestReward = firebaseRemoteConfig.getDouble("QUIZ_HIGHEST_REWARD").toFloat()
                    quizMediumReward = firebaseRemoteConfig.getDouble("QUIZ_MEDIUM_REWARD").toFloat()
                    quizLowestReward = firebaseRemoteConfig.getDouble("QUIZ_LOWEST_REWARD").toFloat()
                } else {
                    showCustomErrorToast(
                        this,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_LONG,
                        this.window
                    )
                }
            }
        //background
        playerBackgroundSound = ExoPlayer.Builder(this).build()
        backgroundMediaItem = MediaItem.fromUri(
            "https://drive.google.com/uc?id=1DX32f3QDFMxPUcYIzDA5QP9WUzN1pwtv"
        )
        playerBackgroundSound.setMediaItem(backgroundMediaItem)
        playerBackgroundSound.repeatMode = Player.REPEAT_MODE_ONE
        playerBackgroundSound.prepare()
        playerBackgroundSound.play()

        //correct
        playerCorrectAnswer = ExoPlayer.Builder(this).build()
        correctAnswerMediaItem = MediaItem.fromUri(
            "android.resource://$packageName/${R.raw.correct_answer_sound}"
        )
        playerCorrectAnswer.setMediaItem(correctAnswerMediaItem)
        playerCorrectAnswer.prepare()

        //wrong
        playerWrongAnswer = ExoPlayer.Builder(this).build()
        wrongAnswerMediaItem = MediaItem.fromUri(
            "android.resource://$packageName/${R.raw.wrong_answer_second_sound}"
        )
        playerWrongAnswer.setMediaItem(wrongAnswerMediaItem)
        playerWrongAnswer.prepare()

        countdownDialog = CountdownDialog(this)
        countdownDialog.countdownShow()
    }

    private fun setBackToPreviousActivityListener(window: Window) {
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                confirmBackInt ++
                if (confirmBackInt == 1){
                    showCustomSuccessToast(
                        applicationContext,
                        getString(R.string.click_again_to_give_up),
                        Toast.LENGTH_LONG,
                        window
                    )
                }
                else if (confirmBackInt > 1){
                    playerBackgroundSound.release()
                    startActivity(backToGamesCenterActivity)
                    finish()
                }
                backTimer.schedule(object : TimerTask() {
                    override fun run() {
                        confirmBackInt = 0
                    }
                }, 3000)
            }
        })

        backBtn.setOnClickListener {
            confirmBackWithBtnInt ++
            if (confirmBackWithBtnInt == 1){
                showCustomSuccessToast(
                    this,
                    getString(R.string.click_again_to_give_up),
                    Toast.LENGTH_LONG,
                    this.window
                )
            }
            else if (confirmBackWithBtnInt > 1){
                playerBackgroundSound.release()
                startActivity(backToGamesCenterActivity)
                finish()
            }
            backTimer.schedule(object : TimerTask() {
                override fun run() {
                    confirmBackWithBtnInt = 0
                }
            }, 3000)
        }
    }

    fun initializeQuizGame() {
        quizGameLogic.startQuiz()
        updateQuestionView()
        updateGamePhaseCounter()
    }

    private fun formatTime(minutes: Int, seconds: Int): String {
        return if (minutes > 0) {
            getString(R.string.remaining_time, minutes, seconds)
        } else {
            getString(R.string.remaining_time_seconds, seconds)
        }
    }

    fun startTimer() {
            timerTask = object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        progress--
                        val minutes: Int = progress / 60
                        val seconds: Int = progress % 60
                        secondsRemainingTxt.text = formatTime(minutes, seconds)
                        progressBar.progress = progress
                        if (progress == 0 || progress < 1) {
                            timer.cancel()
                            timerTask.cancel()
                            val loadingDialog = LoadingDialog(this@QuizGameActivity)
                            loadingDialog.show()
                            updateUserCoinsAndEventProgress()
                        }
                    }
                }
            }
            if (isTimerRunning) {
                timer.scheduleAtFixedRate(timerTask, 100, 900)
            } else {
                progress = savedProgress
                timer.scheduleAtFixedRate(timerTask, 100, 900)
            }
    }

    private fun fetchUserData() {
        userData
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    coin = document.getField("coins")!!
                    getUserVipPoints = document.getLong("vipPoints") ?: 0
                    vipLevel = calculateVipLevel(getUserVipPoints)
                }
            }
            .addOnFailureListener { exception ->
                showCustomErrorToast(
                    this,
                    getString(R.string.error_, exception.message.toString()),
                    Toast.LENGTH_LONG,
                    this.window
                )
            }

        dailyEvent
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    if (document.contains("collectedStars")) {
                        val getCollectStarsProgress = document.getLong("collectedStars") ?: 0
                        collectStarsProgress = getCollectStarsProgress.toInt()
                    } else {
                        dailyEvent.update("collectedStars", 0)
                            .addOnSuccessListener {
                                dailyEvent.addSnapshotListener { snapshot, e ->
                                    if (e != null) {
                                        return@addSnapshotListener
                                    }
                                    if (snapshot != null && snapshot.exists()) {
                                        val map: Map<String, Any> = snapshot.data!!
                                        val collectedStars: Long = map["collectedStars"] as Long
                                        collectStarsProgress = collectedStars.toInt()
                                    }
                                }
                            }
                    }
                } else {
                    val addData: HashMap<String, Any> = hashMapOf(
                        "collectedStars" to 0
                    )
                    dailyEvent.set(addData)
                        .addOnSuccessListener {
                            dailyEvent.addSnapshotListener { snapshot, e ->
                                if (e != null) {
                                    return@addSnapshotListener
                                }
                                if (snapshot != null && snapshot.exists()) {
                                    val map: Map<String, Any> = snapshot.data!!
                                    val collectedStars: Long = map["collectedStars"] as Long
                                    collectStarsProgress = collectedStars.toInt()
                                }
                            }
                        }

                }
            }
            .addOnFailureListener { exception ->
                showCustomErrorToast(
                    this,
                    getString(R.string.error_, exception.message.toString()),
                    Toast.LENGTH_LONG,
                    this.window
                )
            }
    }

    private fun calculateVipLevel(vipPoints: Long): Int {
        return (vipPoints / 500).toInt()
    }

    private fun updateUserCoinsAndEventProgress() {
        if (!getIntentExtraValue) {
            val bonusPercentage = if (vipLevel < 33) {
                (vipLevel * 3) / 100f
            } else {
                (vipLevel * 4) / 100f
            }
            var baseBonusAmount: Float = if (isFirstPhaseDone) {
                quizMediumReward
            } else if (isSecondPhaseDone) {
                quizHighestReward
            } else {
                quizLowestReward
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
                                        intent = Intent(this@QuizGameActivity, GamesCenterActivity::class.java)
                                        intent.putExtra("earnedCoins", baseBonusAmount)
                                        playerBackgroundSound.release()
                                        startActivity(intent)
                                        this@QuizGameActivity.finish()
                                    }
                            }
                    }
                    .addOnFailureListener { exception ->
                        showCustomErrorToast(
                            this,
                            getString(R.string.error_, exception.message.toString()),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                    }
        } else {
            val intent = Intent(this, GamesCenterActivity::class.java)
            playerBackgroundSound.release()
            startActivity(intent)
            this@QuizGameActivity.finish()
        }
    }

    private fun setViewListener() {
        optionOneButton.setOnClickListener {
            val answer: String = optionOneTextView.text.toString()
            val isCorrect: Boolean = quizGameLogic.answerQuestion(answer)
            if (!isCorrect) {
                playerWrongAnswer.seekToDefaultPosition()
                playerWrongAnswer.play()
                val currentQuestion: Question = quizGameLogic.getCurrentQuestion()
                val correctAnswer: String = currentQuestion.correctAnswer
                wrongAnswer(optionOneButton, when (correctAnswer) {
                    optionTwoTextView.text.toString() -> optionTwoButton
                    optionThreeTextView.text.toString() -> optionThreeButton
                    optionFourTextView.text.toString() -> optionFourthButton
                    else ->  optionOneButton
                })
                disableAllOptions()
                Handler(Looper.getMainLooper()).postDelayed({
                    enableAllOptions()
                    quizGameLogic.goToNextQuestion()
                    updateQuestionView()
                }, 350)
            } else {
                playerCorrectAnswer.seekToDefaultPosition()
                playerCorrectAnswer.play()
                correctAnswer(optionOneButton)
                disableAllOptions()
                Handler(Looper.getMainLooper()).postDelayed({
                    enableAllOptions()
                    quizGameLogic.goToNextQuestion()
                    updateQuestionView()
                }, 350)
                score += 1
                updateGamePhaseCounter()
            }
        }
        optionTwoButton.setOnClickListener {
            val answer: String = optionTwoTextView.text.toString()
            val isCorrect: Boolean = quizGameLogic.answerQuestion(answer)
            if (!isCorrect) {
                playerWrongAnswer.seekToDefaultPosition()
                playerWrongAnswer.play()
                val currentQuestion: Question = quizGameLogic.getCurrentQuestion()
                val correctAnswer: String = currentQuestion.correctAnswer
                wrongAnswer(optionTwoButton, when (correctAnswer) {
                    optionOneTextView.text.toString() -> optionOneButton
                    optionThreeTextView.text.toString() -> optionThreeButton
                    optionFourTextView.text.toString() -> optionFourthButton
                    else ->  optionTwoButton
                })
                disableAllOptions()
                Handler(Looper.getMainLooper()).postDelayed({
                    enableAllOptions()
                    quizGameLogic.goToNextQuestion()
                    updateQuestionView()
                }, 350)
            } else {
                playerCorrectAnswer.seekToDefaultPosition()
                playerCorrectAnswer.play()
                correctAnswer(optionTwoButton)
                disableAllOptions()
                Handler(Looper.getMainLooper()).postDelayed({
                    enableAllOptions()
                    quizGameLogic.goToNextQuestion()
                    updateQuestionView()
                }, 350)
                score += 1
                updateGamePhaseCounter()
            }
        }
        optionThreeButton.setOnClickListener {
            val answer: String = optionThreeTextView.text.toString()
            val isCorrect: Boolean = quizGameLogic.answerQuestion(answer)
            if (!isCorrect) {
                playerWrongAnswer.seekToDefaultPosition()
                playerWrongAnswer.play()
                val currentQuestion: Question = quizGameLogic.getCurrentQuestion()
                val correctAnswer: String = currentQuestion.correctAnswer
                wrongAnswer(optionThreeButton, when (correctAnswer) {
                    optionOneTextView.text.toString() -> optionOneButton
                    optionTwoTextView.text.toString() -> optionTwoButton
                    optionFourTextView.text.toString() -> optionFourthButton
                    else ->  optionThreeButton
                })
                disableAllOptions()
                Handler(Looper.getMainLooper()).postDelayed({
                    enableAllOptions()
                    quizGameLogic.goToNextQuestion()
                    updateQuestionView()
                }, 350)
            } else {
                playerCorrectAnswer.seekToDefaultPosition()
                playerCorrectAnswer.play()
                correctAnswer(optionThreeButton)
                disableAllOptions()
                Handler(Looper.getMainLooper()).postDelayed({
                    enableAllOptions()
                    quizGameLogic.goToNextQuestion()
                    updateQuestionView()
                }, 250)
                score += 1
                updateGamePhaseCounter()
            }
        }
        optionFourthButton.setOnClickListener {
            val answer: String = optionFourTextView.text.toString()
            val isCorrect: Boolean = quizGameLogic.answerQuestion(answer)
            if (!isCorrect) {
                playerWrongAnswer.seekToDefaultPosition()
                playerWrongAnswer.play()
                val currentQuestion: Question = quizGameLogic.getCurrentQuestion()
                val correctAnswer: String = currentQuestion.correctAnswer
                wrongAnswer(optionFourthButton, when (correctAnswer) {
                    optionOneTextView.text.toString() -> optionOneButton
                    optionTwoTextView.text.toString() -> optionTwoButton
                    optionThreeTextView.text.toString() -> optionThreeButton
                    else -> optionFourthButton
                })
                disableAllOptions()
                Handler(Looper.getMainLooper()).postDelayed({
                    enableAllOptions()
                    quizGameLogic.goToNextQuestion()
                    updateQuestionView()
                }, 350)
            } else {
                playerCorrectAnswer.seekToDefaultPosition()
                playerCorrectAnswer.play()
                correctAnswer(optionFourthButton)
                disableAllOptions()
                Handler(Looper.getMainLooper()).postDelayed({
                    enableAllOptions()
                    quizGameLogic.goToNextQuestion()
                    updateQuestionView()
                }, 350)
                score += 1
                updateGamePhaseCounter()
            }
        }
    }

    private fun wrongAnswer(wrongAnswerView: ImageView, wasTheCorrectAnswer: ImageView) {
       wrongAnswerView.setImageResource(R.drawable.quiz_wrong_answer_button_background)
       wasTheCorrectAnswer.setImageResource(R.drawable.quiz_correct_answer_button_background)
        Handler(Looper.getMainLooper()).postDelayed({
            wrongAnswerView.setImageResource(R.drawable.quiz_button)
            wasTheCorrectAnswer.setImageResource(R.drawable.quiz_button)
        }, 250)
        errors += 1
        errorsCounterTxt.text = getString(R.string.errors, errors)
        if (errors > 5) {
            showCustomErrorToast(
                this,
                getString(R.string.you_lose_the_game),
                Toast.LENGTH_LONG,
                this.window
            )
            playerBackgroundSound.release()
            startActivity(backToGamesCenterActivity)
            finish()
        }
    }

    private fun correctAnswer(correctAnswerView: ImageView) {
        correctAnswerView.setImageResource(R.drawable.quiz_correct_answer_button_background)
        Handler(Looper.getMainLooper()).postDelayed({
            correctAnswerView.setImageResource(R.drawable.quiz_button)
        }, 250)
    }

    private fun disableAllOptions() {
        optionOneButton.isEnabled = false
        optionTwoButton.isEnabled = false
        optionThreeButton.isEnabled = false
        optionFourthButton.isEnabled = false
    }

    private fun enableAllOptions() {
        optionOneButton.isEnabled = true
        optionTwoButton.isEnabled = true
        optionThreeButton.isEnabled = true
        optionFourthButton.isEnabled = true
    }

    private fun updateQuestionView() {
        val question = quizGameLogic.getCurrentQuestion()

        questionTextView.text = question.questionText
        optionOneTextView.text = question.options[0]
        optionTwoTextView.text = question.options[1]
        optionThreeTextView.text = question.options[2]
        optionFourTextView.text = question.options[3]
    }

    private fun updateGamePhaseCounter() {
        when (score) {
            in 0..3 -> {
                gamePhase = "1/2"
                gamePhaseCounterTxt.text = getString(
                    R.string.phase,
                    gamePhase
                )
            }
            in 4..8 -> {
                isFirstPhaseDone = true
                gamePhase = "2/2"
                gamePhaseCounterTxt.text = getString(
                    R.string.phase,
                    gamePhase
                )
            }
           /* in 12..17 -> {
                gamePhase = "3/6"
                gamePhaseCounterTxt.text = getString(
                    R.string.phase,
                    gamePhase
                )
                isSecondPhaseDone = true
            }
            in 18..23 -> {
                gamePhase = "4/6"
                gamePhaseCounterTxt.text = getString(
                    R.string.phase,
                    gamePhase
                )
                isThirdPhaseDone = true
            }
            in 24..29 -> {
                gamePhase = "5/6"
                gamePhaseCounterTxt.text = getString(
                    R.string.phase,
                    gamePhase
                )
                isFourthPhaseDone = true
            }
            in 30..35 -> {
                gamePhase = "6/6"
                gamePhaseCounterTxt.text = getString(
                    R.string.phase,
                    gamePhase
                )
                isFifthPhaseDone = true
            }*/
            else -> {
                val loadingDialog = LoadingDialog(this@QuizGameActivity)
                loadingDialog.show()
                isFirstPhaseDone = false
                isSecondPhaseDone = true
                updateUserCoinsAndEventProgress()
            }
        }
    }

    private fun textsUI() {
        gameName = findViewById(R.id.game_name)
        val textShader: Shader = LinearGradient(
            0f, 24f, 0f, gameName.textSize,
            intArrayOf(
                Color.parseColor("#CED2FD"),
                Color.parseColor("#767B9B")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        gameName.paint.shader = textShader
    }

}

