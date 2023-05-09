package com.star.games.android

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DailyEventActivity : AppCompatActivity() {

    private lateinit var eventNameTxt : TextView
    private lateinit var missionsTxt : TextView
    private lateinit var eventDurationTxt : TextView
    private lateinit var claimTxt : TextView
    private lateinit var claimTxt1 : TextView
    private lateinit var claimTxt2 : TextView
    private lateinit var userEventPointsTxt : TextView
    private lateinit var claimBtnBackground : LinearLayout
    private lateinit var claimBtnBackground1 : LinearLayout
    private lateinit var claimBtnBackground2 : LinearLayout
    private lateinit var firstQuestFrame : ImageView
    private lateinit var firstQuestPointsReward : LinearLayout
    private lateinit var firstQuestDesc : TextView
    private lateinit var secondQuestProgressTxt : TextView
    private lateinit var secondQuestPointsReward : LinearLayout
    private lateinit var thirdQuestPointsReward : LinearLayout
    private lateinit var secondQuestFrame : ImageView
    private lateinit var thirdQuestFrame : ImageView
    private lateinit var claimFirstQuestBtn : LinearLayout
    private lateinit var claimSecondQuestBtn : LinearLayout
    private lateinit var claimThirdQuestBtn : LinearLayout

    private lateinit var dialogShowRewardDetail: CustomDialogRewardDetail
    private lateinit var dialogShowReward: RewardDialog
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var eventPointsProgress : ProgressBar

    private var progress : Long = 0
    private var cantCollectFirstReward : Boolean = true
    private var cantCollectSecondReward : Boolean = true
    private var cantCollectThirdReward : Boolean = true
    private var cantClaimFirstQuest = true
    private var cantClaimSecondQuest = true
    private var cantClaimThirdQuest = true
    private lateinit var firstQuestProgressTxt : TextView
    private var cantCollectFourthReward : Boolean = true
    private var coin : Long = 0
    private var coinInt : Int = 0
    private var dailyEventPoints : Int = 0

    private val user  = FirebaseAuth.getInstance().currentUser
    private val db = Firebase.firestore
    private val uid = user!!.uid
    private val dailyEvent = db.collection("DAILY_EVENT").document(uid)
    private val userData = db.collection("USERS").document(uid)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_event)

        val backToHomeActivity = Intent(this@DailyEventActivity, HomeActivity::class.java)
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(backToHomeActivity)
                finish()
            }
        })

        eventNameTxt = findViewById(R.id.event_name)
        missionsTxt = findViewById(R.id.missions_txt)
        eventDurationTxt = findViewById(R.id.event_duration_txt)
        userEventPointsTxt = findViewById(R.id.user_event_points_txt)
        firstQuestDesc = findViewById(R.id.first_quest_desc)
        secondQuestProgressTxt = findViewById(R.id.second_quest_progress_txt)
        secondQuestPointsReward = findViewById(R.id.second_quest_points_reward)
        thirdQuestPointsReward = findViewById(R.id.third_quest_points_reward)
        secondQuestFrame = findViewById(R.id.second_quest_frame)
        thirdQuestFrame = findViewById(R.id.third_quest_frame)
        textsUI()
        claimTxt = findViewById(R.id.claim_txt)
        claimTxt1 = findViewById(R.id.claim2_txt)
        claimTxt2 = findViewById(R.id.claim3_txt)
        firstQuestFrame = findViewById(R.id.first_quest_frame)
        firstQuestPointsReward = findViewById(R.id.first_quest_points_reward)
        loadingDialog = LoadingDialog(this)
        loadingDialog.show()

        val firstRewardIcon : ImageView = findViewById(R.id.gift1)
        val secondRewardIcon : ImageView = findViewById(R.id.gift2)
        val thirdRewardIcon : ImageView = findViewById(R.id.gift3)
        val fourthRewardIcon : ImageView = findViewById(R.id.gift4)
        claimFirstQuestBtn = findViewById(R.id.claim_btn)
        claimSecondQuestBtn = findViewById(R.id.claim2_btn)
        claimThirdQuestBtn = findViewById(R.id.claim3_btn)
        val firstQuestProgress : ProgressBar = findViewById(R.id.first_quest_progress)
        firstQuestProgressTxt = findViewById(R.id.first_quest_progress_txt)
        val secondQuestProgress : ProgressBar = findViewById(R.id.second_quest_progress)
        val backBtn : ImageView = findViewById(R.id.back_btn)
        backBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        claimBtnBackground = findViewById(R.id.claim_btn_background)
        claimBtnBackground1 = findViewById(R.id.claim_btn_background1)
        claimBtnBackground2 = findViewById(R.id.claim_btn_background2)
        eventPointsProgress = findViewById(R.id.eventPointsProgress)
        claim1UI(false)
        claim2UI(false)
        claim3UI(false)
        dailyEvent.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loadingDialog.hide()
                    val document = task.result
                    if (document.exists()) {
                        if (document.contains("dailyEventPoints")) {
                            progress = document.getLong("dailyEventPoints")!!
                            userEventPointsTxt.text = getString(R.string.points_big, progress)
                            dailyEventPoints = progress.toInt()
                            eventPointsProgress.progress = progress.toInt()
                        } else {
                            userEventPointsTxt.text = getString(R.string.points_big, 0)
                        }
                        if (progress >= 25) {
                            if (document.contains("firstClaimed")) {
                                cantCollectFirstReward = true
                                firstRewardIcon.setImageResource(R.drawable.opened_gift)
                            } else {
                                cantCollectFirstReward = false
                                firstRewardIcon.setImageResource(R.drawable.gifticon)
                            }
                            if (progress >= 50) {
                                if (document.contains("secondClaimed")) {
                                    cantCollectSecondReward = true
                                    secondRewardIcon.setImageResource(R.drawable.opened_gift)
                                } else {
                                    cantCollectSecondReward = false
                                    secondRewardIcon.setImageResource(R.drawable.gifticon)
                                }
                            }
                            if (progress >= 75) {
                                if (document.contains("thirdClaimed")) {
                                    cantCollectThirdReward = true
                                    thirdRewardIcon.setImageResource(R.drawable.opened_gift)
                                } else {
                                    cantCollectThirdReward = false
                                    thirdRewardIcon.setImageResource(R.drawable.gifticon)
                                }
                            }
                            if (progress >= 100) {
                                if (document.contains("fourthClaimed")) {
                                    cantCollectFourthReward = true
                                    fourthRewardIcon.setImageResource(R.drawable.opened_gift)
                                } else {
                                    cantCollectFourthReward = false
                                    fourthRewardIcon.setImageResource(R.drawable.gifticon)
                                }
                            }
                        }
                        if (document.contains("collectStarsQuest")){
                            val collectStarsProgressBar = document.getLong("collectStarsQuest")!!
                            firstQuestProgress.progress = collectStarsProgressBar.toInt()
                            firstQuestProgressTxt.text = "$collectStarsProgressBar/500"
                            if (collectStarsProgressBar >= 500){
                                if (document.contains("firstQuestDone")){
                                    firstQuestDoneUI()
                                }
                                else {
                                    cantClaimFirstQuest = false
                                    claim1UI(true)
                                }
                            }
                            else{
                                claim1UI(false)
                                cantClaimFirstQuest = true
                            }
                        }
                        if (document.contains("useTicketsQuest")){
                            val useTicketsQuestProgress = document.getLong("useTicketsQuest")!!

                            secondQuestProgress.progress = useTicketsQuestProgress.toInt()
                            secondQuestProgressTxt.text = "$useTicketsQuestProgress/5"
                            if (useTicketsQuestProgress >= 5){
                                if (document.contains("secondQuestDone")){
                                    secondQuestProgressTxt.setTextColor(ContextCompat.getColor(this, R.color.greenProgressTxt))
                                    secondQuestDoneUI()
                                } else {
                                    cantClaimSecondQuest = false
                                    claim2UI(true)
                                }
                            }
                            else{
                                claim2UI(false)
                                cantClaimSecondQuest = true
                            }
                        }
                        if (document.contains("dailyLogin")){
                                if (document.contains("thirdQuestDone")){
                                    thirdQuestDoneUI()
                                } else {
                                    cantClaimThirdQuest = false
                                    claim3UI(true)
                                }
                        }
                        else{
                            claim3UI(false)
                            cantClaimThirdQuest = true
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.message.toString(), Toast.LENGTH_LONG).show()
                eventPointsProgress.progress = 0
                firstQuestProgress.progress = 0
                secondQuestProgress.progress = 0
                loadingDialog.hide()
            }

        userData.get()
            .addOnSuccessListener { document ->
                loadingDialog.hide()
                if (document != null) {
                    coin = document.getLong("coin")!!
                    coinInt = coin.toInt()

                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.message.toString(), Toast.LENGTH_LONG).show()
                loadingDialog.hide()

            }

        dialogShowRewardDetail = CustomDialogRewardDetail(this)
        dialogShowReward = RewardDialog(this)
        firstRewardIcon.setOnClickListener {
            if (!cantCollectFirstReward){
                val conta = coinInt + 500
                val dados = hashMapOf(
                    "coin" to conta
                )
                userData.update(dados as Map<String, Any>)
                    .addOnSuccessListener {
                        userData.addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                // ocorreu um erro ao receber a atualização
                                return@addSnapshotListener
                            }
                            if (snapshot != null && snapshot.exists()) {
                                val map = snapshot.data
                                coin = map?.get("coin") as Long
                                coinInt = coin.toInt()
                            }
                        }
                    }

                val eventUpdate = hashMapOf(
                    "firstClaimed" to "y"
                )

                dailyEvent.update(eventUpdate as Map<String, Any>)
                    .addOnSuccessListener {
                        dialogShowReward.showRewardDialog(R.drawable.quinhentos_stars)
                        cantCollectFirstReward = true
                        firstRewardIcon.setImageResource(R.drawable.opened_gift)
                    }
            }
            else {
                dialogShowRewardDetail.showDialogRewardDetail(R.drawable.star_reward_icon, firstRewardIcon)
            }
        }

        secondRewardIcon.setOnClickListener {
                if (!cantCollectSecondReward){
                    val continha433 = coinInt + 500
                    val data3908 = hashMapOf(
                        "coin" to continha433
                    )
                    userData.update(data3908 as Map<String, Any>)
                        .addOnSuccessListener {
                            userData.addSnapshotListener { snap0213, ex6102 ->
                                if (ex6102 != null) {
                                    // ocorreu um erro ao receber a atualização
                                    return@addSnapshotListener
                                }
                                if (snap0213 != null && snap0213.exists()) {
                                    val getData0192 = snap0213.data
                                    coin = getData0192?.get("coin") as Long
                                    coinInt = coin.toInt()
                                }
                            }
                        }

                    val eventUpdate1 = hashMapOf(
                        "secondClaimed" to "y"
                    )

                    dailyEvent.update(eventUpdate1 as Map<String, Any>)
                        .addOnSuccessListener {
                            dialogShowReward.showRewardDialog(R.drawable.quinhentos_stars)
                            cantCollectSecondReward = true
                            secondRewardIcon.setImageResource(R.drawable.opened_gift)
                        }
                }
                else {
                    dialogShowRewardDetail.showDialogRewardDetail(R.drawable.star_reward_icon, firstRewardIcon)
                }
            }

        thirdRewardIcon.setOnClickListener {
            if (!cantCollectThirdReward){
                val conta67 = coinInt + 500
                val dados76 = hashMapOf(
                    "coin" to conta67
                )
                userData.update(dados76 as Map<String, Any>)
                    .addOnSuccessListener {
                        userData.addSnapshotListener { snap9898, ex934 ->
                            if (ex934 != null) {
                                // ocorreu um erro ao receber a atualização
                                return@addSnapshotListener
                            }
                            if (snap9898 != null && snap9898.exists()) {
                                val map0586 = snap9898.data
                                coin = map0586?.get("coin") as Long
                                coinInt = coin.toInt()
                            }
                        }
                    }

                val eventUpdate103 = hashMapOf(
                    "thirdClaimed" to "y"
                )

                dailyEvent.update(eventUpdate103 as Map<String, Any>)
                    .addOnSuccessListener {
                        dialogShowReward.showRewardDialog(R.drawable.quinhentos_stars)
                        cantCollectThirdReward = true
                        thirdRewardIcon.setImageResource(R.drawable.opened_gift)
                    }
            }
            else {
                dialogShowRewardDetail.showDialogRewardDetail(R.drawable.star_reward_icon, firstRewardIcon)
            }
        }

        fourthRewardIcon.setOnClickListener {
            if (!cantCollectFourthReward){
                val conta73673 = coinInt + 500
                val setData73673 = hashMapOf(
                    "coin" to conta73673
                )
                userData.update(setData73673 as Map<String, Any>)
                    .addOnSuccessListener {
                        userData.addSnapshotListener { snap4875, ex878 ->
                            if (ex878 != null) {
                                // ocorreu um erro ao receber a atualização
                                return@addSnapshotListener
                            }
                            if (snap4875 != null && snap4875.exists()) {
                                val map49075 = snap4875.data
                                coin = map49075?.get("coin") as Long
                                coinInt = coin.toInt()
                            }
                        }
                    }
                val eventUpdate266 = hashMapOf(
                    "fourthClaimed" to "y"
                )

                dailyEvent.update(eventUpdate266 as Map<String, Any>)
                    .addOnSuccessListener {
                        dialogShowReward.showRewardDialog(R.drawable.quinhentos_stars)
                        cantCollectFourthReward = true
                        fourthRewardIcon.setImageResource(R.drawable.opened_gift)
                    }
            }
            else {
                dialogShowRewardDetail.showDialogRewardDetail(R.drawable.star_reward_icon, firstRewardIcon)
            }
        }

        claimFirstQuestBtn.setOnClickListener {
            if (!cantClaimFirstQuest){
                dailyEventPoints += 25
                val setFirstQuestData = hashMapOf(
                    "dailyEventPoints" to dailyEventPoints,
                    "firstQuestDone" to "y"
                )
                dailyEvent.set(setFirstQuestData, SetOptions.merge())
                    .addOnSuccessListener {
                        dailyEvent.addSnapshotListener { snapFirstQuest, exFirstQuest->
                            if (exFirstQuest != null) {
                                // ocorreu um erro ao receber a atualização
                                return@addSnapshotListener
                            }

                            if (snapFirstQuest != null && snapFirstQuest.exists()) {
                                val mapFirstQuest = snapFirstQuest.data
                                if (mapFirstQuest?.containsKey("dailyEventPoints")!!){
                                    val prugressFirstQuest = (mapFirstQuest["dailyEventPoints"]) as Long
                                    eventPointsProgress.progress = prugressFirstQuest.toInt()

                                    userEventPointsTxt.text = getString(R.string.points_big, prugressFirstQuest)
                                    claim1UI(false)
                                    cantClaimFirstQuest = true
                                    firstQuestDoneUI()
                                }


                            }
                        }
                    }
            }
        }

        claimSecondQuestBtn.setOnClickListener {
                if (!cantClaimSecondQuest){
                    dailyEventPoints += 25
                    val claimSecondQuestMap = hashMapOf(
                        "dailyEventPoints" to dailyEventPoints,
                        "secondQuestDone" to "y"
                    )
                    dailyEvent.set(claimSecondQuestMap, SetOptions.merge())
                        .addOnSuccessListener {
                            dailyEvent.addSnapshotListener { snapSecondQuest, e2 ->
                                if (e2 != null) {
                                    // ocorreu um erro ao receber a atualização
                                    return@addSnapshotListener
                                }

                                if (snapSecondQuest != null && snapSecondQuest.exists()) {
                                    val getSecondQuestData = snapSecondQuest.data
                                    if (getSecondQuestData?.containsKey("dailyEventPoints")!!){
                                        val prugress = (getSecondQuestData["dailyEventPoints"]) as Long
                                        eventPointsProgress.progress = prugress.toInt()
                                        userEventPointsTxt.text = getString(R.string.points_big, prugress)
                                        claim2UI(false)
                                        cantClaimSecondQuest = true
                                        secondQuestDoneUI()
                                    }


                                }
                            }
                        }
                }
        }

        claimThirdQuestBtn.setOnClickListener {
            if (!cantClaimThirdQuest) {
                dailyEventPoints += 25
                val thirdQuestData = hashMapOf(
                    "dailyEventPoints" to dailyEventPoints,
                    "thirdQuestDone" to "y"
                )
                dailyEvent.set(thirdQuestData, SetOptions.merge())
                    .addOnSuccessListener {
                        dailyEvent.addSnapshotListener { snapThirdQuest, eThirdQuest ->

                            if (eThirdQuest != null) {
                                return@addSnapshotListener
                            }

                            if (snapThirdQuest != null && snapThirdQuest.exists()) {
                                val getThirdMap = snapThirdQuest.data
                                val prugress3 = (getThirdMap!!["dailyEventPoints"]) as Long
                                if (prugress3 >= 25) {
                                    cantCollectFirstReward = false
                                }
                                eventPointsProgress.progress = prugress3.toInt()
                                userEventPointsTxt.text = getString(R.string.points_big, prugress3)
                                claim3UI(false)
                                cantClaimThirdQuest = true
                                thirdQuestDoneUI()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    private fun thirdQuestDoneUI() {
        val context : Context = this@DailyEventActivity
        val imageView3 = ImageView(context)
        val textView3 = TextView(context)
        imageView3.id = View.generateViewId()
        textView3.id = View.generateViewId()
        textView3.text = getString(R.string.collected)
        textView3.setTextColor(Color.WHITE)
        textView3.textSize = 12f
        textView3.typeface = ResourcesCompat.getFont(this, R.font.inter_black)
        imageView3.setImageResource(R.drawable.quest_done)
        val textShadere: Shader = LinearGradient(
            0f, 0f, 0f, textView3.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#FFD66E"), // First color
                Color.parseColor("#A48B34") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        textView3.paint.shader = textShadere

        claimThirdQuestBtn.visibility = View.GONE
        thirdQuestPointsReward.visibility = View.GONE

        val parentLayout = findViewById<ConstraintLayout>(R.id.background)

        parentLayout.addView(textView3)
        parentLayout.addView(imageView3)

        val constraintSet = ConstraintSet()
        constraintSet.constrainWidth(imageView3.id, 48)
        constraintSet.constrainHeight(imageView3.id, 48)
        constraintSet.connect(imageView3.id, ConstraintSet.TOP, thirdQuestFrame.id, ConstraintSet.TOP)
        constraintSet.connect(imageView3.id, ConstraintSet.BOTTOM, textView3.id, ConstraintSet.BOTTOM)
        constraintSet.connect(imageView3.id, ConstraintSet.START, textView3.id, ConstraintSet.START)
        constraintSet.connect(imageView3.id, ConstraintSet.END, textView3.id, ConstraintSet.END)
        constraintSet.constrainWidth(textView3.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(textView3.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(textView3.id, ConstraintSet.TOP, imageView3.id, ConstraintSet.TOP)
        constraintSet.connect(textView3.id, ConstraintSet.BOTTOM, thirdQuestFrame.id, ConstraintSet.BOTTOM)
        constraintSet.connect(textView3.id, ConstraintSet.END, thirdQuestFrame.id, ConstraintSet.END, 48)
        constraintSet.applyTo(parentLayout)
    }

    private fun secondQuestDoneUI() {
        val context : Context = this@DailyEventActivity
        val imageView2 = ImageView(context)
        val textView2 = TextView(context)
        imageView2.id = View.generateViewId()
        textView2.id = View.generateViewId()
        textView2.text = getString(R.string.collected)
        textView2.setTextColor(Color.WHITE)
        textView2.textSize = 12f
        textView2.typeface = ResourcesCompat.getFont(this, R.font.inter_black)
        imageView2.setImageResource(R.drawable.quest_done)
        val textShadere: Shader = LinearGradient(
            0f, 0f, 0f, textView2.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#FFD66E"), // First color
                Color.parseColor("#A48B34") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        textView2.paint.shader = textShadere

        claimSecondQuestBtn.visibility = View.GONE
        secondQuestPointsReward.visibility = View.GONE

        val parentLayout = findViewById<ConstraintLayout>(R.id.background)

        parentLayout.addView(textView2)
        parentLayout.addView(imageView2)

        val constraintSet = ConstraintSet()
        constraintSet.constrainWidth(imageView2.id, 48)
        constraintSet.constrainHeight(imageView2.id, 48)
        constraintSet.connect(imageView2.id, ConstraintSet.TOP, secondQuestFrame.id, ConstraintSet.TOP)
        constraintSet.connect(imageView2.id, ConstraintSet.BOTTOM, textView2.id, ConstraintSet.BOTTOM)
        constraintSet.connect(imageView2.id, ConstraintSet.START, textView2.id, ConstraintSet.START)
        constraintSet.connect(imageView2.id, ConstraintSet.END, textView2.id, ConstraintSet.END)
        constraintSet.constrainWidth(textView2.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(textView2.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(textView2.id, ConstraintSet.TOP, imageView2.id, ConstraintSet.TOP)
        constraintSet.connect(textView2.id, ConstraintSet.BOTTOM, secondQuestFrame.id, ConstraintSet.BOTTOM)
        constraintSet.connect(textView2.id, ConstraintSet.END, secondQuestFrame.id, ConstraintSet.END, 48)
        constraintSet.applyTo(parentLayout)
    }

    private fun firstQuestDoneUI() {
        firstQuestProgressTxt.setTextColor(ContextCompat.getColor(this, R.color.greenProgressTxt))
        val context : Context = this@DailyEventActivity
        val imageView11 = ImageView(context)
        val textView11 = TextView(context)
        imageView11.id = View.generateViewId()
        textView11.id = View.generateViewId()
        textView11.text = getString(R.string.collected)
        textView11.setTextColor(Color.WHITE)
        textView11.textSize = 12f
        textView11.typeface = ResourcesCompat.getFont(this, R.font.inter_black)
        imageView11.setImageResource(R.drawable.quest_done)

        val textShadere: Shader = LinearGradient(
            0f, 0f, 0f, textView11.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#FFD66E"), // First color
                Color.parseColor("#A48B34") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        textView11.paint.shader = textShadere

        claimFirstQuestBtn.visibility = View.GONE
        firstQuestPointsReward.visibility = View.GONE

        val parentLayout = findViewById<ConstraintLayout>(R.id.background)

        parentLayout.addView(textView11)
        parentLayout.addView(imageView11)

        val constraintSet = ConstraintSet()
        constraintSet.constrainWidth(imageView11.id, 48)
        constraintSet.constrainHeight(imageView11.id, 48)
        constraintSet.connect(imageView11.id, ConstraintSet.TOP, firstQuestFrame.id, ConstraintSet.TOP)
        constraintSet.connect(imageView11.id, ConstraintSet.BOTTOM, textView11.id, ConstraintSet.BOTTOM)
        constraintSet.connect(imageView11.id, ConstraintSet.START, textView11.id, ConstraintSet.START)
        constraintSet.connect(imageView11.id, ConstraintSet.END, textView11.id, ConstraintSet.END)
        constraintSet.constrainWidth(textView11.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(textView11.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(textView11.id, ConstraintSet.TOP, imageView11.id, ConstraintSet.TOP)
        constraintSet.connect(textView11.id, ConstraintSet.BOTTOM, firstQuestFrame.id, ConstraintSet.BOTTOM)
        constraintSet.connect(textView11.id, ConstraintSet.START, firstQuestPointsReward.id, ConstraintSet.START)
        constraintSet.connect(textView11.id, ConstraintSet.END, firstQuestFrame.id, ConstraintSet.END, 48)
        constraintSet.applyTo(parentLayout)
    }

    private fun textsUI(){
        val ts11111: Shader = LinearGradient(
            0f, 12f, 0f, eventNameTxt.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#CED2FD"), // First color
                Color.parseColor("#767B9B") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        eventNameTxt.paint.shader = ts11111

        val ts22222: Shader = LinearGradient(
            0f, 24f, 0f, missionsTxt.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#FFE17C"), // First color
                Color.parseColor("#F8D661") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        missionsTxt.paint.shader = ts22222

        val ts3333: Shader = LinearGradient(
            0f, 24f, 0f, eventDurationTxt.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#FFE17C"), // First color
                Color.parseColor("#F8D661") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        eventDurationTxt.paint.shader = ts3333

        val ts99995: Shader = LinearGradient(
            0f, 24f, 0f, userEventPointsTxt.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#FFE17C"), // First color
                Color.parseColor("#F8D661") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        userEventPointsTxt.paint.shader = ts99995

    }

    private fun claim1UI(enabled : Boolean){
        if(enabled){
            claimTxt.text = getString(R.string.receive)
            if (claimTxt.text.length > 8){
                claimTxt.textSize = 10F
            }
            val sh44445: Shader = LinearGradient(
                0f, 0f, 0f, claimTxt.textSize, // Change the ending y-coordinate to be textSize
                intArrayOf(
                    Color.parseColor("#66422A"), // First color
                    Color.parseColor("#291000") // Second color
                ),
                null,
                Shader.TileMode.MIRROR
            )
            claimTxt.paint.shader = sh44445
            claimBtnBackground.setBackgroundResource(R.drawable.active_button)

        }
        else {
            claimTxt.text = getString(R.string.incomplete)
            if (claimTxt.text.length > 8){
                claimTxt.textSize = 10F
            }
            val sh9998: Shader = LinearGradient(
                 0f, 0f, 0f, claimTxt.textSize, // Change the ending y-coordinate to be textSize
                 intArrayOf(
                     Color.parseColor("#696969"), // First color
                     Color.parseColor("#282828") // Second color
                 ),
                 null,
                 Shader.TileMode.MIRROR
             )
             claimTxt.paint.shader = sh9998
            claimBtnBackground.setBackgroundResource(R.drawable.event_unable_button)
        }
    }

    private fun claim2UI(enabled : Boolean){
        if (enabled){
            claimTxt1.text = getString(R.string.receive)
            if (claimTxt1.text.length > 8){
                claimTxt1.textSize = 10F
            }
            val a556767: Shader = LinearGradient(
                0f, 0f, 0f, claimTxt1.textSize, // Change the ending y-coordinate to be textSize
                intArrayOf(
                    Color.parseColor("#66422A"), // First color
                    Color.parseColor("#291000") // Second color
                ),
                null,
                Shader.TileMode.MIRROR
            )
            claimTxt1.paint.shader = a556767
            claimBtnBackground1.setBackgroundResource(R.drawable.active_button)
        } else {
            claimTxt1.text = getString(R.string.incomplete)
            if (claimTxt1.text.length > 8){
                claimTxt1.textSize = 10F
            }
            val s9999954: Shader = LinearGradient(
                0f, 0f, 0f, claimTxt1.textSize, // Change the ending y-coordinate to be textSize
                intArrayOf(
                    Color.parseColor("#696969"), // First color
                    Color.parseColor("#282828") // Second color
                ),
                null,
                Shader.TileMode.MIRROR
            )
            claimTxt1.paint.shader = s9999954
            claimBtnBackground1.setBackgroundResource(R.drawable.event_unable_button)
        }

    }

    private fun claim3UI(enabled: Boolean){
        if (enabled){
            claimTxt2.text = getString(R.string.receive)
            if (claimTxt2.text.length > 8){
                claimTxt2.textSize = 10F
            }
            val suhsudhsuf: Shader = LinearGradient(
                0f, 0f, 0f, claimTxt2.textSize, // Change the ending y-coordinate to be textSize
                intArrayOf(
                    Color.parseColor("#66422A"), // First color
                    Color.parseColor("#291000") // Second color
                ),
                null,
                Shader.TileMode.MIRROR
            )
            claimTxt2.paint.shader = suhsudhsuf
            claimBtnBackground2.setBackgroundResource(R.drawable.active_button)
        } else {
            claimTxt2.text = getString(R.string.incomplete)
            if (claimTxt2.text.length > 8){
                claimTxt2.textSize = 10F
            }
            val ererere1: Shader = LinearGradient(
                0f, 0f, 0f, claimTxt2.textSize, // Change the ending y-coordinate to be textSize
                intArrayOf(
                    Color.parseColor("#696969"), // First color
                    Color.parseColor("#282828") // Second color
                ),
                null,
                Shader.TileMode.MIRROR
            )
            claimTxt2.paint.shader = ererere1
            claimBtnBackground2.setBackgroundResource(R.drawable.event_unable_button)
        }
    }
}