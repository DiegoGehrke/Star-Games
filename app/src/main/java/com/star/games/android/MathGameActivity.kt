package com.star.games.android

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
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


class MathGameActivity : AppCompatActivity() {

    private lateinit var firstBtn: ImageView
    private lateinit var secondBtn: ImageView
    private lateinit var thirdBtn: ImageView
    private lateinit var fourthBtn: ImageView
    private lateinit var fifthBtn: ImageView
    private lateinit var sixthBtn: ImageView
    private lateinit var backBtn: ImageView
    private lateinit var firstBtnTxt: TextView
    private lateinit var secondBtnTxt: TextView
    private lateinit var thirdBtnTxt: TextView
    private lateinit var fourthBtnTxt: TextView
    private lateinit var fifthBtnTxt: TextView
    private lateinit var sixthBtnTxt: TextView
    private lateinit var showQuestionTxt: TextView
    private lateinit var timerTxt: TextView
    private lateinit var phaseCounterTxt: TextView
    private lateinit var mistakesCounterTxt: TextView
    private lateinit var gameName: TextView

    private val user = FirebaseAuth.getInstance().currentUser
    private val db = Firebase.firestore
    private val uid = user!!.uid
    private val dailyEvent = db.collection("DAILY_EVENT").document(uid)
    private val userData = db.collection("USERS").document(uid)

    private var progress: Int = 45
    private var coin: Float = 0.00F
    private var collectStarsProgress: Int = 0
    private var vipLevel: Int = 0
    private var getUserVipPoints: Long = 0
    private var confirmBackInt: Int = 0
    private var confirmBackWithBtnInt: Int = 0
    private var bonusAmount: Float = 0F
    private var savedProgress: Int = 0

    private var timer = Timer()
    private val backTimer = Timer()
    private lateinit var timerTask: TimerTask

    private var getIntentExtraValue : Boolean = false
    private var isTimerRunning = false

    private lateinit var progressBarTimer: ProgressBar

    private lateinit var additionMathGame: MathGameLogic

    private lateinit var backToGamesCenterActivity: Intent

    private lateinit var banner: IronSourceBannerLayout

    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    private lateinit var configSettings: FirebaseRemoteConfigSettings

    private var mathHighestReward: Float = 12.00f
    private var mathMediumReward: Float = 10.00f
    private var mathLowestReward: Float = 8.00f

    private lateinit var map: HashMap<String, Any>

    private lateinit var playerBackgroundSound: ExoPlayer
    private lateinit var backgroundMediaItem: MediaItem

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var countdownDialog: CountdownDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_math_game)

        initializeViews()
        loadBannerAd()
        setBackToPreviousActivityListener(this.window)
        fetchUserData()
        textsUI()
    }

    private fun loadBannerAd() {
        banner = IronSource.createBanner(this@MathGameActivity, ISBannerSize.BANNER)!!

        banner.levelPlayBannerListener = object : LevelPlayBannerListener {
            override fun onAdLoaded(adInfo: AdInfo) {}

            override fun onAdLoadFailed(error: IronSourceError) {}

            override fun onAdClicked(adInfo: AdInfo) {}

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
    }

    override fun onPause() {
        super.onPause()
        isTimerRunning = false
        playerBackgroundSound.pause()
        savedProgress = progress
        timer.cancel()
        timer.purge()
    }

    override fun onResume() {
        super.onResume()
        val currentPlaybackPosition = playerBackgroundSound.currentPosition
        playerBackgroundSound.seekTo(currentPlaybackPosition)
        playerBackgroundSound.play()
        isTimerRunning = true
        timer = Timer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerBackgroundSound.release()
        timer.cancel()
        timerTask.cancel()
    }

    private fun initializeViews() {
        firstBtn = findViewById(R.id.first_btn)
        secondBtn = findViewById(R.id.second_btn)
        thirdBtn = findViewById(R.id.third_btn)
        fourthBtn = findViewById(R.id.fourth_btn)
        fifthBtn = findViewById(R.id.fifth_btn)
        sixthBtn = findViewById(R.id.sixth_btn)
        firstBtnTxt = findViewById(R.id.first_btn_txt)
        secondBtnTxt = findViewById(R.id.second_btn_txt)
        thirdBtnTxt = findViewById(R.id.third_btn_txt)
        fourthBtnTxt = findViewById(R.id.fourth_btn_txt)
        fifthBtnTxt = findViewById(R.id.fifth_btn_txt)
        sixthBtnTxt = findViewById(R.id.sixth_btn_txt)
        showQuestionTxt = findViewById(R.id.questTxt)
        timerTxt = findViewById(R.id.textView7)
        progressBarTimer = findViewById(R.id.timerProgress2)
        phaseCounterTxt = findViewById(R.id.phaseCounter)
        mistakesCounterTxt = findViewById(R.id.mistakesCounterTxt)
        gameName = findViewById(R.id.game_name)
        backBtn = findViewById(R.id.back_btn)
        phaseCounterTxt.text = getString(R.string.phase, "1/2")
        mistakesCounterTxt.text = getString(R.string.errors, 0)
        loadingDialog = LoadingDialog(this)

        backToGamesCenterActivity = Intent(this, GamesCenterActivity::class.java)

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
                    showCustomErrorToast(
                        this,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_LONG,
                        this.window
                    )
                }
            }

        //background sound
        playerBackgroundSound = ExoPlayer.Builder(this).build()
        /*playerBackgroundSound.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(@Player.State state: Int) {
                when (state) {
                    Player.STATE_READY -> {
                        loadingDialog.hide()
                        countdownDialog.countdownShow()
                    }
                    Player.STATE_BUFFERING -> {
                        loadingDialog.show()
                    }
                    Player.STATE_IDLE -> {
                        // The player is idle, meaning it holds only limited resources.The player must be prepared before it will play the media.
                    }
                    Player.STATE_ENDED -> {
                        // The player has finished playing the media.
                    }
                    else -> {
                        // Other things
                    }
                }
            }
        })*/
        backgroundMediaItem = MediaItem.fromUri(
            "https://drive.google.com/uc?id=1gjsqDTrA7ovLtC2K-digS42VWwd4JJyO"
        )
        playerBackgroundSound.setMediaItem(backgroundMediaItem)
        playerBackgroundSound.repeatMode = Player.REPEAT_MODE_ONE
        playerBackgroundSound.prepare()
        playerBackgroundSound.play()

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

    fun initializeMathGame() {
        additionMathGame = MathGameLogic(
            showQuestionTxt,
            firstBtn,
            secondBtn,
            thirdBtn,
            fourthBtn,
            fifthBtn,
            sixthBtn,
            firstBtnTxt,
            secondBtnTxt,
            thirdBtnTxt,
            fourthBtnTxt,
            fifthBtnTxt,
            sixthBtnTxt,
            coin,
            collectStarsProgress,
            this@MathGameActivity,
            phaseCounterTxt,
            mistakesCounterTxt,
            this.window,
            getIntentExtraValue,
            vipLevel
        )
        additionMathGame.generateAdditionQuestion()
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
                    timerTxt.text = formatTime(minutes, seconds)
                    progressBarTimer.progress = progress
                    if (progress == 0 || progress < 1) {
                        timer.cancel()
                        timerTask.cancel()
                        val loadingDialog = LoadingDialog(this@MathGameActivity)
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

    private fun updateUserCoinsAndEventProgress() {
        if (!getIntentExtraValue) {
            val bonusPercentage = if (vipLevel < 33) {
                (vipLevel * 3) / 100f
            } else {
                (vipLevel * 4) / 100f
            }
            var baseBonusAmount: Float = if (additionMathGame.readCurrentFirstPhaseStatus()) {
                mathMediumReward
            } else if (additionMathGame.readCurrentSecondPhaseStatus()) {
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
                                        intent = Intent(this@MathGameActivity, GamesCenterActivity::class.java)
                                        intent.putExtra("earnedCoins", baseBonusAmount)
                                        playerBackgroundSound.release()
                                        startActivity(intent)
                                        this@MathGameActivity.finish()
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
                        intent = Intent(this@MathGameActivity, GamesCenterActivity::class.java)
                        intent.putExtra("earnedCoins", baseBonusAmount)
                        playerBackgroundSound.release()
                        startActivity(intent)
                        this@MathGameActivity.finish()
                    }
        } else {
            val intent = Intent(this, GamesCenterActivity::class.java)
            playerBackgroundSound.release()
            startActivity(intent)
            this@MathGameActivity.finish()
        }
    }

    private fun textsUI() {
        val shader21082937: Shader = LinearGradient(
            0f, 12f, 0f, showQuestionTxt.textSize,
            intArrayOf(
                Color.parseColor("#FFE17C"),
                Color.parseColor("#F8D661")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        showQuestionTxt.paint.shader = shader21082937

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