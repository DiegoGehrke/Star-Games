package com.star.games.android

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
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
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random

class MemoryGameActivity : AppCompatActivity() {

    private lateinit var card1 :  ImageView
    private lateinit var card2 : ImageView
    private lateinit var card3 : ImageView
    private lateinit var card4 : ImageView
    private lateinit var card5 : ImageView
    private lateinit var card6 : ImageView
    private lateinit var card7 : ImageView
    private lateinit var card8 : ImageView
    private lateinit var backBtn : ImageView

    private val _timer : Timer = Timer()
    private var timer : Timer = Timer()
    private val backTimer = Timer()
    private lateinit var timer1 : TimerTask
    private lateinit var ttaskk : TimerTask

    private lateinit var timerTxt : TextView
    private lateinit var gameName : TextView
    private lateinit var phaseTxt : TextView

    private var progress : Int = 45
    private var confirmBackInt : Int = 0
    private var confirmBackWithBtnInt : Int = 0
    private var genRanNum: Double = 0.0
    private var click : Double = 0.0
    private var genPos : Double = 0.0
    private var genCardType : Double = 0.0
    private var vipLevel : Int = 0
    private var getUserVipPoints : Long = 0
    private var collectStarsProgress : Int = 0
    private var coin : Float = 0.00F
    private var bonusAmount: Float = 0F
    private var savedProgress = 0

    private lateinit var timerProgress: ProgressBar

    private var gamePhase : String = "1/1"
    private lateinit var cardType : String

    private var map = HashMap<String, Any>()
    private var cardVarMap = HashMap<String?, Any>()
    private var clickedCard = ArrayList<String>()
    private val matchedCardList = ArrayList<String>()
    private val genCardList = ArrayList<String?>()

    private var areInFirstPhase : Boolean = false
    private var areInSecondPhase : Boolean = false
    private var areInThirdPhase : Boolean = false
    private var areInFourthPhase : Boolean = false
    private var areInFifthPhase : Boolean = false
    private var areInSixthPhase : Boolean = false
    private var isFirstPhaseDone : Boolean = false
    private var isSecondPhaseDone : Boolean = false
    private var isThirdPhaseDone : Boolean = false
    private var isFourthPhaseDone : Boolean = false
    private var isFifthPhaseDone : Boolean = false
    private var isSixthPhaseDone : Boolean = false
    private var getIntentExtraValue : Boolean = false
    private var gameStart = false
    private var isTimerRunning = false

    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val uid = user!!.uid
    private val dailyEvent = db.collection("DAILY_EVENT").document(uid)
    private val userData = db.collection("USERS").document(uid)

    private lateinit var banner: IronSourceBannerLayout

    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var loadingDialog: LoadingDialog

    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    private lateinit var configSettings: FirebaseRemoteConfigSettings

    private var memoryHighestReward: Float = 12.00f
    private var memoryLowestReward: Float = 8.00f

    private lateinit var mapDefaultValues: HashMap<String, Any>

    private lateinit var playerBackgroundSound: ExoPlayer
    private lateinit var backgroundMediaItem: MediaItem
    private lateinit var playerFlipCardAnswer: ExoPlayer
    private lateinit var playerFlipMediaItem: MediaItem
    private lateinit var playerMatchCards: ExoPlayer
    private lateinit var matchCardsMediaItem: MediaItem

    private lateinit var countdownDialog: CountdownDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory_game)

        fetchUserData()
        initializeViews()
        setOnClickListeners()
        initializeBannerAd()
        gradientTextEffect()
        setCharacter()
        levelGenerator()

        getIntentExtraValue = if (intent != null && intent.extras != null) {
            val getExtras: Bundle = intent.extras!!
            getExtras.containsKey("noHaveTicketsOrChances")
        } else {
            false
        }
    }

    private fun setCharacter() {
        val randomNumber = Random.nextInt(1, 7)
        areInFirstPhase = randomNumber == 1
        areInSecondPhase = randomNumber == 2
        areInThirdPhase = randomNumber == 3
        areInFourthPhase = randomNumber == 4
        areInFifthPhase = randomNumber == 5
        areInSixthPhase = randomNumber == 6
    }

    private fun initializeViews() {
        constraintLayout = findViewById(R.id.constraint_bckg)
        gameName = findViewById(R.id.game_name)
        card1 = findViewById(R.id.card1)
        card2 = findViewById(R.id.card2)
        card3 = findViewById(R.id.card3)
        card4 = findViewById(R.id.card4)
        card5 = findViewById(R.id.card5)
        card6 = findViewById(R.id.card6)
        card7 = findViewById(R.id.card7)
        card8 = findViewById(R.id.card8)
        timerTxt = findViewById(R.id.textView7)
        timerProgress = findViewById(R.id.timerProgress2)
        backBtn = findViewById(R.id.back_btn)
        phaseTxt = findViewById(R.id.phaseCounter)
        phaseTxt.text = getString(R.string.phase, gamePhase)
        loadingDialog = LoadingDialog(this@MemoryGameActivity)

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        configSettings = FirebaseRemoteConfigSettings
            .Builder()
            .setMinimumFetchIntervalInSeconds(1)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mapDefaultValues = HashMap()
        mapDefaultValues["MEMORY_HIGHEST_REWARD"] = memoryHighestReward
        mapDefaultValues["MEMORY_LOWEST_REWARD"] = memoryLowestReward
        firebaseRemoteConfig.setDefaultsAsync(mapDefaultValues)
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    memoryHighestReward = firebaseRemoteConfig.getDouble("MEMORY_HIGHEST_REWARD").toFloat()
                    memoryLowestReward = firebaseRemoteConfig.getDouble("MEMORY_LOWEST_REWARD").toFloat()
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
                        // The player is able to immediately play from its current position. The player will be playing if getPlayWhenReady() is true, and paused otherwise.
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
                "https://drive.google.com/uc?id=1V6KN5gz0kOIFz8LSKcAxqCQ5omFhaQT2"
        )
        playerBackgroundSound.setMediaItem(backgroundMediaItem)
        playerBackgroundSound.repeatMode = Player.REPEAT_MODE_ONE
        playerBackgroundSound.prepare()
        playerBackgroundSound.play()

        //flip sound
        playerFlipCardAnswer = ExoPlayer.Builder(this).build()
        playerFlipMediaItem = MediaItem.fromUri(
            "android.resource://$packageName/${R.raw.card_flip_another_sound}"
        )
        playerFlipCardAnswer.setMediaItem(playerFlipMediaItem)
        playerFlipCardAnswer.prepare()

        //match sound
        playerMatchCards = ExoPlayer.Builder(this).build()
        matchCardsMediaItem = MediaItem.fromUri(
            "android.resource://$packageName/${R.raw.correct_answer_sound}"
        )
        playerMatchCards.setMediaItem(matchCardsMediaItem)
        playerMatchCards.prepare()

        countdownDialog = CountdownDialog(this)
        countdownDialog.countdownShow()
    }

    private fun fetchUserData() {
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
                                        val map = snapshot.data
                                        val collectedStars = map?.get("collectedStars") as Long
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
                    exception.message.toString(),
                    Toast.LENGTH_LONG,
                    this.window
                )
            }

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
                    exception.message.toString(),
                    Toast.LENGTH_LONG,
                    this.window
                )
            }
    }

    private fun initializeBannerAd() {
        banner = IronSource.createBanner(this@MemoryGameActivity, ISBannerSize.BANNER)!!
        banner = IronSource.createBanner(this@MemoryGameActivity, ISBannerSize.BANNER)!!

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

    private fun setOnClickListeners() {
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                confirmBackInt ++
                if (confirmBackInt == 1){
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.click_again_to_give_up),
                        Toast.LENGTH_LONG)
                        .show()
                }
                else if (confirmBackInt > 1){
                    val backIntent = Intent(this@MemoryGameActivity, GamesCenterActivity::class.java)
                    startActivity(backIntent)
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
                val backIntent = Intent(this@MemoryGameActivity, GamesCenterActivity::class.java)
                startActivity(backIntent)
                finish()
            }
            backTimer.schedule(object : TimerTask() {
                override fun run() {
                    confirmBackWithBtnInt = 0
                }
            }, 3000)
        }

        card1.setOnClickListener {
            card1.isEnabled = false
            cardType = "card1"
            gameplayLogic(card1)
        }

        card2.setOnClickListener {
            card2.isEnabled = false
            cardType = "card2"
            gameplayLogic(card2)
        }

        card3.setOnClickListener {
            card3.isEnabled = false
            cardType = "card3"
            gameplayLogic(card3)
        }

        card4.setOnClickListener {
            card4.isEnabled = false
            cardType = "card4"
            gameplayLogic(card4)
        }

        card5.setOnClickListener {
            card5.isEnabled = false
            cardType = "card5"
            gameplayLogic(card5)
        }

        card6.setOnClickListener {
            card6.isEnabled = false
            cardType = "card6"
            gameplayLogic(card6)
        }

        card7.setOnClickListener {
            card7.isEnabled = false
            cardType = "card7"
            gameplayLogic(card7)
        }

        card8.setOnClickListener {
            card8.isEnabled = false
            cardType = "card8"
            gameplayLogic(card8)
        }
    }

    override fun onPause() {
        super.onPause()
        isTimerRunning = false
        savedProgress = progress
        playerFlipCardAnswer.pause()
        playerBackgroundSound.pause()
        playerMatchCards.pause()
        timer.cancel()
        timer.purge()
    }

    override fun onResume() {
        super.onResume()
        isTimerRunning = true
        val currentPlaybackPosition = playerBackgroundSound.currentPosition
        playerBackgroundSound.seekTo(currentPlaybackPosition)
        playerBackgroundSound.play()
        timer = Timer()
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.hide()
        timer.cancel()
        ttaskk.cancel()
        playerFlipCardAnswer.release()
        playerBackgroundSound.release()
        playerMatchCards.release()
    }

    fun startTimer() {
        ttaskk = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    progress--
                    val minutes: Int = progress / 60
                    val seconds: Int = progress % 60
                    timerTxt.text = formatTime(minutes, seconds)
                    timerProgress.progress = progress
                    if (progress == 0 || progress < 1) {
                        timer.cancel()
                        ttaskk.cancel()
                        val loadingDialog = LoadingDialog(this@MemoryGameActivity)
                        loadingDialog.show()
                        updateUserCoinsAndEventProgress()
                    }
                }
            }
        }
        if (isTimerRunning) {
            timer.scheduleAtFixedRate(ttaskk, 100, 900)
        } else {
            progress = savedProgress
            timer.scheduleAtFixedRate(ttaskk, 100, 900)
        }
    }

    private fun calculateVipLevel(vipPoints: Long): Int {
        return (vipPoints / 500).toInt()
    }

    private fun levelGenerator() {
        for (_repeat10 in 0..7) {
            genRanNum++
            genCardList.add("card" + genRanNum.toLong().toString())
        }
        genCardList.shuffle()
        cardVarMap = HashMap()
        for (_repeat18 in 0..7) {
            genCardType++
            cardVarMap[genCardList[genPos.toInt()]] = genCardType.toLong().toString()
            genPos++
            if (genCardType == 4.0) {
                genCardType = 0.0
            }
        }
    }

    private fun showCard(_card: ImageView?, _type: Double) {
        if (_type == 1.0) {
            _card!!.setImageResource(R.drawable.sloth_coffe)
        }
        if (_type == 2.0) {
            _card!!.setImageResource(R.drawable.sloth_happy)
        }
        if (_type == 3.0) {
            _card!!.setImageResource(R.drawable.sloth_sleep)
        }
        if (_type == 4.0) {
            _card!!.setImageResource(R.drawable.sloth_spa)
        }
    }

    private fun showRoosterCard(_card: ImageView?, _type: Double) {
        if (_type == 1.0) {
            _card!!.setImageResource(R.drawable.rooster_happy)
        }
        if (_type == 2.0) {
            _card!!.setImageResource(R.drawable.rooster_music)
        }
        if (_type == 3.0) {
            _card!!.setImageResource(R.drawable.rooster_train)
        }
        if (_type == 4.0) {
            _card!!.setImageResource(R.drawable.rooster_hello)
        }
    }

    private fun showPigCard(_card: ImageView?, _type: Double) {
        if (_type == 1.0) {
            _card!!.setImageResource(R.drawable.pig_beer)
        }
        if (_type == 2.0) {
            _card!!.setImageResource(R.drawable.pig_furious)
        }
        if (_type == 3.0) {
            _card!!.setImageResource(R.drawable.pig_plant)
        }
        if (_type == 4.0) {
            _card!!.setImageResource(R.drawable.pig_thief)
        }
    }

    private fun showChihuahuaCard(_card: ImageView?, _type: Double) {
        if (_type == 1.0) {
            _card!!.setImageResource(R.drawable.chihuahua_bathe)
        }
        if (_type == 2.0) {
            _card!!.setImageResource(R.drawable.chihuahua_dinner)
        }
        if (_type == 3.0) {
            _card!!.setImageResource(R.drawable.chihuahua_nervous)
        }
        if (_type == 4.0) {
            _card!!.setImageResource(R.drawable.chihuahua_mind_blown)
        }
    }

    private fun showTeddyCard(_card: ImageView?, _type: Double) {
        if (_type == 1.0) {
            _card!!.setImageResource(R.drawable.bear_idea)
        }
        if (_type == 2.0) {
            _card!!.setImageResource(R.drawable.bear_king)
        }
        if (_type == 3.0) {
            _card!!.setImageResource(R.drawable.bear_question)
        }
        if (_type == 4.0) {
            _card!!.setImageResource(R.drawable.bear_warrior)
        }
    }

    private fun showLionCard(_card: ImageView?, _type: Double) {
        if (_type == 1.0) {
            _card!!.setImageResource(R.drawable.lion_fighter)
        }
        if (_type == 2.0) {
            _card!!.setImageResource(R.drawable.lion_king)
        }
        if (_type == 3.0) {
            _card!!.setImageResource(R.drawable.lion_food)
        }
        if (_type == 4.0) {
            _card!!.setImageResource(R.drawable.lion_winner)
        }
    }

    private fun openAnimation(_card: ImageView?) {
        playerFlipCardAnswer.seekToDefaultPosition()
        playerFlipCardAnswer.play()
        val anim1 = ObjectAnimator.ofFloat(_card, "ScaleX", 1f, 0f)
        anim1.duration = 100
        anim1.start()
        timer1 = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if(areInFirstPhase){
                        showCard(
                            _card,
                            (cardVarMap[cardType])!!.toString().toDouble()
                        )
                    }
                    if (areInSecondPhase){
                        showRoosterCard(
                            _card,
                            (cardVarMap[cardType])!!.toString().toDouble()
                        )
                    }
                     if (areInThirdPhase){
                        showPigCard(_card,
                            (cardVarMap[cardType])!!.toString().toDouble())
                    }
                    if (areInFourthPhase){
                        showChihuahuaCard(_card,
                            (cardVarMap[cardType])!!.toString().toDouble())
                    }
                    if (areInFifthPhase){
                        showTeddyCard(_card,
                            (cardVarMap[cardType])!!.toString().toDouble())
                    }
                    if (areInSixthPhase){
                        showLionCard(_card,
                            (cardVarMap[cardType])!!.toString().toDouble())
                    }

                    val anim2 = ObjectAnimator.ofFloat(_card, "ScaleX", 0f, 1f)
                    anim2.duration = 100
                    anim2.start()
                }
            }
        }
        _timer.schedule(timer1, 100)
    }

    private fun closeAnimation(_card: ImageView?) {
        val anim3 = ObjectAnimator.ofFloat(_card, "ScaleX", 1f, 0f)
        anim3.duration = 100
        anim3.start()
        timer1 = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    _card!!.setImageResource(R.drawable.back_card)
                    val anim4 = ObjectAnimator.ofFloat(_card, "ScaleX", 0f, 1f)
                    anim4.duration = 100
                    anim4.start()
                }
            }
        }
        _timer.schedule(timer1, 100)
    }

    private fun cardSetEnable(_setEnable: Boolean) {
        if (_setEnable) {
            if (!matchedCardList.contains("card1")) {
                card1.isEnabled = true
            }
            if (!matchedCardList.contains("card2")) {
                card2.isEnabled = true
            }
            if (!matchedCardList.contains("card3")) {
                card3.isEnabled = true
            }
            if (!matchedCardList.contains("card4")) {
                card4.isEnabled = true
            }
            if (!matchedCardList.contains("card5")) {
                card5.isEnabled = true
            }
            if (!matchedCardList.contains("card6")) {
                card6.isEnabled = true
            }
            if (!matchedCardList.contains("card7")) {
                card7.isEnabled = true
            }
            if (!matchedCardList.contains("card8")) {
                card8.isEnabled = true
            }
        } else {
            card1.isEnabled = false
            card2.isEnabled = false
            card3.isEnabled = false
            card4.isEnabled = false
            card5.isEnabled = false
            card6.isEnabled = false
            card7.isEnabled = false
            card8.isEnabled = false
        }
    }

    fun startGame() {
        cardSetEnable(true)
        gameStart = true
    }

   @SuppressLint("SetTextI18n")
    fun matchChecker() {
        if (cardVarMap[clickedCard[0]].toString()
                .toDouble() ==
                cardVarMap[clickedCard[1]]
            .toString().toDouble()
        ) {
            matchedCardList.add(clickedCard[0])
            matchedCardList.add(clickedCard[1])
            clickedCard.clear()
            if (matchedCardList.size == 8) {
                if (areInFirstPhase){
                    isFirstPhaseDone = true
                    loadingDialog.show()
                    updateUserCoinsAndEventProgress()
                }
                 else if (areInSecondPhase) {
                    isSecondPhaseDone = true
                    loadingDialog.show()
                    updateUserCoinsAndEventProgress()
                }
                 else if (areInThirdPhase){
                    isThirdPhaseDone = true
                    loadingDialog.show()
                    updateUserCoinsAndEventProgress()
                }
                 else if (areInFourthPhase){
                    isFourthPhaseDone = true
                    loadingDialog.show()
                    updateUserCoinsAndEventProgress()
                }
                 else if (areInFifthPhase){
                    isFifthPhaseDone = true
                    loadingDialog.show()
                    updateUserCoinsAndEventProgress()
                }
                else if (areInSixthPhase){
                    isSixthPhaseDone = true
                    loadingDialog.show()
                    updateUserCoinsAndEventProgress()
                } else {
                    updateUserCoinsAndEventProgress()
                }

            }
        } else {
            playerFlipCardAnswer.seekToDefaultPosition()
            playerFlipCardAnswer.play()
        }
    }

    private fun updateUserCoinsAndEventProgress() {
        if (!getIntentExtraValue) {
            val bonusPercentage = if (vipLevel < 33) {
                (vipLevel * 3) / 100f
            } else {
                (vipLevel * 4) / 100f
            }
            var baseBonusAmount: Float = if (
                isFirstPhaseDone ||
                    isSecondPhaseDone ||
                    isThirdPhaseDone ||
                    isFourthPhaseDone ||
                    isFifthPhaseDone ||
                    isSixthPhaseDone
            ) {
                memoryHighestReward
            } else {
                memoryLowestReward
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
                                        intent = Intent(this@MemoryGameActivity, GamesCenterActivity::class.java)
                                        intent.putExtra("earnedCoins", baseBonusAmount)
                                        playerFlipCardAnswer.release()
                                        playerBackgroundSound.release()
                                        playerMatchCards.release()
                                        startActivity(intent)
                                        this@MemoryGameActivity.finish()
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
            playerFlipCardAnswer.release()
            playerBackgroundSound.release()
            playerMatchCards.release()
            startActivity(intent)
            this@MemoryGameActivity.finish()
        }
    }

    private fun gameplayLogic(_card: ImageView?) {
        if (gameStart) {
            if (click != 2.0) {
                click++
                openAnimation(_card)
                cardType.let { clickedCard.add(it) }
                if (click == 2.0) {
                    matchChecker()
                    cardSetEnable(false)
                    timer1 = object : TimerTask() {
                        override fun run() {
                            runOnUiThread {
                                if (clickedCard.contains("card1")) {
                                    closeAnimation(card1)
                                }
                                if (clickedCard.contains("card2")) {
                                    closeAnimation(card2)
                                }
                                if (clickedCard.contains("card3")) {
                                    closeAnimation(card3)
                                }
                                if (clickedCard.contains("card4")) {
                                    closeAnimation(card4)
                                }
                                if (clickedCard.contains("card5")) {
                                    closeAnimation(card5)
                                }
                                if (clickedCard.contains("card6")) {
                                    closeAnimation(card6)
                                }
                                if (clickedCard.contains("card7")) {
                                    closeAnimation(card7)
                                }
                                if (clickedCard.contains("card8")) {
                                    closeAnimation(card8)
                                }
                            }
                        }
                    }
                    _timer.schedule(timer1, 400)
                    val timer2: TimerTask = object : TimerTask() {
                        override fun run() {
                            runOnUiThread {
                                click = 0.0
                                cardSetEnable(true)
                                clickedCard.clear()
                            }
                        }
                    }
                    _timer.schedule(timer2, 600.toLong())
                }
            }
        }
    }

    private fun resetGame(){
        //ttaskk!!.cancel()
        Handler(Looper.getMainLooper()).postDelayed({
            matchedCardList.clear()
            closeAnimation(card1)
            closeAnimation(card2)
            closeAnimation(card3)
            closeAnimation(card4)
            closeAnimation(card5)
            closeAnimation(card6)
            closeAnimation(card7)
            closeAnimation(card8)
            genRanNum = 0.0
            click = 0.0
            genPos = 0.0
            genCardType = 0.0
            genCardList.clear()
            clickedCard.clear()
            clickedCard = ArrayList<String>()
            cardVarMap.clear()
            cardVarMap = HashMap()
            cardSetEnable(false)
            startGame()
            levelGenerator()
        }, 500)
    }

    private fun formatTime(minutes: Int, seconds: Int): String {
        return if (minutes > 0) {
            getString(R.string.remaining_time, minutes, seconds)
        } else {
            getString(R.string.remaining_time_seconds, seconds)
        }
    }

    private fun gradientTextEffect() {
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