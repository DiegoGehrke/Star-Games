package com.star.games.android

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.properties.Delegates

class MemoryGameActivity : AppCompatActivity() {

    private var card1: ImageView? = null
    private var card2: ImageView? = null
    private var card3: ImageView? = null
    private var card4: ImageView? = null
    private var card5: ImageView? = null
    private var card6: ImageView? = null
    private var card7: ImageView? = null
    private var card8: ImageView? = null
    private val _timer = Timer()
    private var timer1: TimerTask? = null
    private var timertxt: TextView? = null
    private var progress = 120
    private lateinit var gameName : TextView
    lateinit var timerProgress: ProgressBar
    private var ttaskk: TimerTask? = null
    private val timer = Timer()
    private var genRanNum = 0.0
    private var click = 0.0
    private var genPos = 0.0
    private var genCardType = 0.0
    private var gamePhase : String = "1/6"
    lateinit var cardType : String
    private var gameStart = false
    private var map = HashMap<String, Any>()
    private var cardVarMap = HashMap<String?, Any>()
    private var clickedCard = ArrayList<String>()
    private val matchedCardList = ArrayList<String>()
    private val genCardList = ArrayList<String?>()
    private var total = 0.0
    private var boolPhase1 : Boolean = true
    private var boolPhase2 : Boolean = false
    private var boolPhase3 : Boolean = false
    private var boolPhase4 : Boolean = false
    private var boolPhase5 : Boolean = false
    private var boolPhase6 : Boolean = false
    private var confirmBackInt : Int = 0
    private var confirmBackWithBtnInt : Int = 0
    private lateinit var backBtn : ImageView
    private val backTimer = Timer()
    lateinit var phaseTxt : TextView
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val uid = user?.uid
    private val dailyEvent = db.collection("DAILY_EVENT").document(uid!!)
    private var collectStarsProgress by Delegates.notNull<Int>()
    var coin by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory_game)
       val backToGamesCenterActivity = Intent(this@MemoryGameActivity, GamesCenterActivity::class.java)
        gameName = findViewById(R.id.game_name)
        val textShader: Shader = LinearGradient(
            0f, 24f, 0f, gameName.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#CED2FD"), // First color
                Color.parseColor("#767B9B") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        gameName.paint.shader = textShader

        dailyEvent.get().addOnSuccessListener { document ->
            if (document.exists()) {
                if (document.contains("collectStarsQuest")) {
                    // O campo já existe, atualiza o valor
                    val getCollectStarsProgress = document.getLong("collectStarsQuest") ?: 0
                    collectStarsProgress = getCollectStarsProgress.toInt()
                    //dailyEvent.update("collectStarsQuest", currentProgress + 1)
                } else {
                    // O campo não existe, cria com o valor inicial 1
                    dailyEvent.update("collectStarsQuest", 0)
                        .addOnSuccessListener {
                            dailyEvent.addSnapshotListener { snapshot, e ->
                                if (e != null) {
                                    // ocorreu um erro ao receber a atualização
                                    return@addSnapshotListener
                                }
                                if (snapshot != null && snapshot.exists()) {
                                    val map = snapshot.data
                                    val collectedStars = map?.get("collectStarsQuest") as Long
                                    collectStarsProgress = collectedStars.toInt()
                                }
                            }
                        }
                }
            } else {
                // O documento não existe, cria com o campo e valor inicial 1
                val addData = hashMapOf(
                    "collectStarsQuest" to 0
                )
                dailyEvent.set(addData)
                    .addOnSuccessListener {
                        dailyEvent.addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                // ocorreu um erro ao receber a atualização
                                return@addSnapshotListener
                            }
                            if (snapshot != null && snapshot.exists()) {
                                val map = snapshot.data
                                val collectedStars = map?.get("collectStarsQuest") as Long
                                collectStarsProgress = collectedStars.toInt()
                            }
                        }
                    }

            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Erro ao checar/atualizar/criar o campo collectStarsQuest", exception)
        }

        card1 = findViewById(R.id.card1)
        card2 = findViewById(R.id.card2)
        card3 = findViewById(R.id.card3)
        card4 = findViewById(R.id.card4)
        card5 = findViewById(R.id.card5)
        card6 = findViewById(R.id.card6)
        card7 = findViewById(R.id.card7)
        card8 = findViewById(R.id.card8)
        timertxt = findViewById(R.id.textView7)
        timerProgress = findViewById(R.id.timerProgress2)
        backBtn = findViewById(R.id.back_btn)

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                confirmBackInt ++
                if (confirmBackInt == 1){
                    Toast.makeText(applicationContext, "Clique novamente para desistir", Toast.LENGTH_LONG).show()
                }
                else if (confirmBackInt > 1){
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
                Toast.makeText(this, "Clique novamente para desistir", Toast.LENGTH_LONG).show()
            }
            else if (confirmBackWithBtnInt > 1){
                startActivity(backToGamesCenterActivity)
                finish()
            }
            backTimer.schedule(object : TimerTask() {
                override fun run() {
                    confirmBackWithBtnInt = 0
                }
            }, 3000)
        }
        phaseTxt = findViewById(R.id.phaseCounter)
        phaseTxt.text = getString(R.string.phase, gamePhase)
        timerProgress.max = 120

        card1?.setOnClickListener {
            card1?.isEnabled = false
            cardType = "card1"
            gameplayLogic(card1)
        }
        card2?.setOnClickListener {
            card2?.isEnabled = false
            cardType = "card2"
            gameplayLogic(card2)
        }
        card3?.setOnClickListener {
            card3?.isEnabled = false
            cardType = "card3"
            gameplayLogic(card3)
        }
        card4?.setOnClickListener {
            card4?.isEnabled = false
            cardType = "card4"
            gameplayLogic(card4)
        }
        card5?.setOnClickListener {
            card5?.isEnabled = false
            cardType = "card5"
            gameplayLogic(card5)
        }
        card6?.setOnClickListener {
            card6?.isEnabled = false
            cardType = "card6"
            gameplayLogic(card6)
        }
        card7?.setOnClickListener {
            card7?.isEnabled = false
            cardType = "card7"
            gameplayLogic(card7)
        }
        card8?.setOnClickListener {
            card8?.isEnabled = false
            cardType = "card8"
            gameplayLogic(card8)
        }
        db.collection("USERS")
            .document(uid!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    coin = document.getLong("coin")!!
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }

        newGame()
        ttaskk = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    progress--
                    timertxt?.text = getString(R.string.seconds_remaining, progress)
                    timerProgress.progress = progress
                    if (progress == 0 || progress < 1) {
                        if (boolPhase1){
                            val cont = coin + 250
                            val addEventProgress = collectStarsProgress + 250
                            db.collection("USERS")
                                .document(uid)
                                .update("coin", cont)
                                .addOnSuccessListener {
                                    dailyEvent.update("collectStarsQuest", addEventProgress)
                                        .addOnSuccessListener {
                                            val intent = Intent(this@MemoryGameActivity, GamesCenterActivity::class.java)
                                            startActivity(intent)
                                            this@MemoryGameActivity.finish()
                                            timer.cancel()
                                        }
                                }
                        } else if (boolPhase2){
                            val cont2 = coin + 200
                            val addEventProgress2 = collectStarsProgress + 250
                            db.collection("USERS")
                                .document(uid)
                                .update("coin", cont2)
                                .addOnSuccessListener {
                                    dailyEvent.update("collectStarsQuest", addEventProgress2)
                                        .addOnSuccessListener {
                                            val intent = Intent(this@MemoryGameActivity, GamesCenterActivity::class.java)
                                            startActivity(intent)
                                            this@MemoryGameActivity.finish()
                                            timer.cancel()
                                        }
                                }
                        } else if (boolPhase3){
                            val cont3 = coin + 250
                            val addEventProgress3 = collectStarsProgress + 250
                            db.collection("USERS")
                                .document(uid)
                                .update("coin", cont3)
                                .addOnSuccessListener {
                                    dailyEvent.update("collectStarsQuest", addEventProgress3)
                                        .addOnSuccessListener {
                                            val intent = Intent(this@MemoryGameActivity, GamesCenterActivity::class.java)
                                            startActivity(intent)
                                            this@MemoryGameActivity.finish()
                                            timer.cancel()
                                        }
                                }
                        } else if (boolPhase4){
                            val cont4 = coin + 300
                            val addEventProgress4 = collectStarsProgress + 300
                            db.collection("USERS")
                                .document(uid)
                                .update("coin", cont4)
                                .addOnSuccessListener {
                                    dailyEvent.update("collectStarsQuest", addEventProgress4)
                                        .addOnSuccessListener {
                                            val intent = Intent(this@MemoryGameActivity, GamesCenterActivity::class.java)
                                            startActivity(intent)
                                            this@MemoryGameActivity.finish()
                                            timer.cancel()
                                        }
                                }
                        }
                        else {
                            val intent = Intent(this@MemoryGameActivity, GamesCenterActivity::class.java)
                            startActivity(intent)
                            this@MemoryGameActivity.finish()
                            timer.cancel()
                        }

                    }
                }
            }
        }
        timer.scheduleAtFixedRate(ttaskk, 100, 900)
        levelGenerator()

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
        val anim1 = ObjectAnimator.ofFloat(_card, "ScaleX", 1f, 0f)
        anim1.duration = 100
        anim1.start()
        timer1 = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if(boolPhase1){
                        showCard(
                            _card,
                            (cardVarMap[cardType])!!.toString().toDouble()
                        )
                    }
                    if (boolPhase2){
                        showRoosterCard(
                            _card,
                            (cardVarMap[cardType])!!.toString().toDouble()
                        )
                    }
                     if (boolPhase3){
                        showPigCard(_card,
                            (cardVarMap[cardType])!!.toString().toDouble())
                    }
                    if (boolPhase4){
                        showChihuahuaCard(_card,
                            (cardVarMap[cardType])!!.toString().toDouble())
                    }
                    if (boolPhase5){
                        showTeddyCard(_card,
                            (cardVarMap[cardType])!!.toString().toDouble())
                    }
                    if (boolPhase6){
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
                card1?.isEnabled = true
            }
            if (!matchedCardList.contains("card2")) {
                card2?.isEnabled = true
            }
            if (!matchedCardList.contains("card3")) {
                card3?.isEnabled = true
            }
            if (!matchedCardList.contains("card4")) {
                card4?.isEnabled = true
            }
            if (!matchedCardList.contains("card5")) {
                card5?.isEnabled = true
            }
            if (!matchedCardList.contains("card6")) {
                card6?.isEnabled = true
            }
            if (!matchedCardList.contains("card7")) {
                card7?.isEnabled = true
            }
            if (!matchedCardList.contains("card8")) {
                card8?.isEnabled = true
            }
        } else {
            card1?.isEnabled = false
            card2?.isEnabled = false
            card3?.isEnabled = false
            card4?.isEnabled = false
           card5?.isEnabled = false
            card6?.isEnabled = false
            card7?.isEnabled = false
            card8?.isEnabled = false
        }
    }

    private fun newGame() {
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
            total += 6.25
            clickedCard.clear()
            if (matchedCardList.size == 8) {
                if (boolPhase1){
                    boolPhase1 = false
                    boolPhase2 = true
                    gamePhase = "2/6"
                    phaseTxt.text = getString(R.string.phase, gamePhase)
                }
                 else if (boolPhase2) {
                    boolPhase2 = false
                    boolPhase3 = true
                    gamePhase = "3/6"
                    phaseTxt.text = getString(R.string.phase, gamePhase)
                }
                 else if (boolPhase3){
                    boolPhase3 = false
                    boolPhase4 = true
                    gamePhase = "4/6"
                    phaseTxt.text = getString(R.string.phase, gamePhase)
                }
                 else if (boolPhase4){
                    boolPhase4 = false
                    boolPhase5 = true
                    gamePhase = "5/6"
                    phaseTxt.text = getString(R.string.phase, gamePhase)
                }
                 else if (boolPhase5){
                    boolPhase5 = false
                    boolPhase6 = true
                    gamePhase = "6/6"
                    phaseTxt.text = getString(R.string.phase, gamePhase)
                }
                else if (boolPhase6){
                    boolPhase5 = false
                    boolPhase6 = true
                    gamePhase = "6/6"
                    phaseTxt.text = getString(R.string.phase, gamePhase)
                }
                resetGame()
                map.clear()
            }
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
            cardVarMap = HashMap<String?, Any>()
            cardSetEnable(false)
            newGame()
            levelGenerator()
        }, 500)
    }

    @Deprecated("")
    fun showMessage(_s: String?) {
        Toast.makeText(applicationContext, _s, Toast.LENGTH_SHORT).show()
    }

    @Deprecated("")
    fun getLocationX(_v: View): Int {
        val _location = IntArray(2)
        _v.getLocationInWindow(_location)
        return _location[0]
    }

    @Deprecated("")
    fun getLocationY(_v: View): Int {
        val _location = IntArray(2)
        _v.getLocationInWindow(_location)
        return _location[1]
    }

    @Deprecated("")
    fun getRandom(_min: Int, _max: Int): Int {
        val random = Random()
        return random.nextInt(_max - _min + 1) + _min
    }

    @Deprecated("")
    fun getCheckedItemPositionsToArray(_list: ListView): ArrayList<Double> {
        val _result = ArrayList<Double>()
        val _arr = _list.checkedItemPositions
        for (_iIdx in 0 until _arr.size()) {
            if (_arr.valueAt(_iIdx)) _result.add(_arr.keyAt(_iIdx).toDouble())
        }
        return _result
    }

    @Deprecated("")
    fun getDip(_input: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            _input.toFloat(),
            resources.displayMetrics
        )
    }

    @get:Deprecated("")
    val displayWidthPixels: Int
        get() = resources.displayMetrics.widthPixels

    @get:Deprecated("")
    val displayHeightPixels: Int
        get() = resources.displayMetrics.heightPixels
}