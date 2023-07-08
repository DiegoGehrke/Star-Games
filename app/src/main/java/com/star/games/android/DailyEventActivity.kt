package com.star.games.android

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
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import com.ironsource.mediationsdk.ISBannerSize
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.IronSourceBannerLayout
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener
import com.star.games.android.ErrorToastUtils.showCustomErrorToast
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class DailyEventActivity : AppCompatActivity() {

    private lateinit var eventNameTxt : TextView
    private lateinit var missionsTxt : TextView
    private lateinit var eventDurationTxt : TextView
    private lateinit var claimTxt : TextView
    private lateinit var claimTxt1 : TextView
    private lateinit var claimTxt2 : TextView
    private lateinit var userEventPointsTxt : TextView
    private lateinit var firstQuestProgressTxt : TextView
    private lateinit var secondQuestProgressTxt : TextView
    private lateinit var firstQuestPointsRewardTxt : TextView
    private lateinit var secondQuestPointsRewardTxt : TextView
    private lateinit var thirdQuestPointsRewardTxt : TextView
    private lateinit var secondQuestDesc : TextView
    private lateinit var firstQuestDesc : TextView

    private lateinit var claimBtnBackground : LinearLayout
    private lateinit var claimBtnBackground1 : LinearLayout
    private lateinit var claimBtnBackground2 : LinearLayout
    private lateinit var claimFirstQuestBtn : LinearLayout
    private lateinit var secondQuestPointsReward : ImageView
    private lateinit var thirdQuestPointsReward : ImageView
    private lateinit var firstQuestPointsReward : ImageView
    private lateinit var claimSecondQuestBtn : LinearLayout
    private lateinit var claimThirdQuestBtn : LinearLayout

    private lateinit var dialogShowRewardDetail: CustomDialogRewardDetail
    private lateinit var dialogShowReward: RewardDialog
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var rulesDialog: RulesDialog
    private lateinit var firebaseRemoteConfigDailyEvent: FirebaseRemoteConfigDailyEvent

    private var isAbleToClaimFirstReward : Boolean = false
    private var isAbleToClaimSecondReward : Boolean = false
    private var isAbleToClaimThirdReward : Boolean = false
    private var isAbleToClaimFourthReward : Boolean = false
    private var isAbleToClaimFirstQuest : Boolean = false
    private var isAbleToClaimSecondQuest : Boolean = false
    private var isAbleToClaimThirdQuest : Boolean = false

    private lateinit var eventPointsProgress : ProgressBar
    private lateinit var firstQuestProgress : ProgressBar
    private lateinit var secondQuestProgress : ProgressBar

    private lateinit var firstRewardIcon : ImageView
    private lateinit var secondRewardIcon : ImageView
    private lateinit var thirdRewardIcon : ImageView
    private lateinit var fourthRewardIcon : ImageView
    private lateinit var firstQuestFrame : ImageView
    private lateinit var secondQuestFrame : ImageView
    private lateinit var thirdQuestFrame : ImageView
    private lateinit var helpBtn : ImageView
    private lateinit var backBtn : ImageView
    private lateinit var secondQuestIcon : ImageView
    private lateinit var firstQuestIcon : ImageView

    private var dailyEventPointsInt : Int = 0
    private var coin : Float = 0.00F
    private var vipLevel: Int = 0
    private var baseValue: Float = 00.00F

    private val user  = FirebaseAuth.getInstance().currentUser
    private val db = Firebase.firestore
    private val uid = user!!.uid
    private val dailyEvent = db.collection("DAILY_EVENT").document(uid)
    private val userData = db.collection("USERS").document(uid)

    private lateinit var banner: IronSourceBannerLayout

    /*private var missionUseTicketsGoal: Long? = null
    private var missionCollectStarsGoal: Long? = null*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_event)

        initializeViews()
        setEventDuration()
        fetchUserData()
        setOnClickListeners()
        textsUI()

        banner = IronSource.createBanner(this@DailyEventActivity, ISBannerSize.BANNER)!!

        banner.levelPlayBannerListener = object : LevelPlayBannerListener {
            override fun onAdLoaded(adInfo: AdInfo) {}

            override fun onAdLoadFailed(error: IronSourceError) {
                showCustomErrorToast(
                    this@DailyEventActivity,
                    error.errorMessage.toString(),
                    Toast.LENGTH_LONG,
                    this@DailyEventActivity.window
                )
            }

            override fun onAdClicked(adInfo: AdInfo) {}

            override fun onAdScreenPresented(adInfo: AdInfo) {}

            override fun onAdScreenDismissed(adInfo: AdInfo) {}

            override fun onAdLeftApplication(adInfo: AdInfo) {}
        }
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        banner.layoutParams = layoutParams

        val relativeLayout = findViewById<RelativeLayout>(R.id.receive_banner_ad)
        relativeLayout.addView(banner)

        IronSource.loadBanner(banner)
    }

    private fun setEventDuration() {
        val userTimeZone = TimeZone.getDefault()

        val sdfUtc = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        sdfUtc.timeZone = TimeZone.getTimeZone("UTC")

        val sdfUser = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        sdfUser.timeZone = userTimeZone

        val eventEndTimeUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        eventEndTimeUtc.set(Calendar.HOUR_OF_DAY, 0)
        eventEndTimeUtc.set(Calendar.MINUTE, 0)
        eventEndTimeUtc.set(Calendar.SECOND, 0)

        val previousDayLocal = eventEndTimeUtc.clone() as Calendar
        previousDayLocal.add(Calendar.DAY_OF_MONTH, -1)
        val previousDayLocalString = sdfUser.format(previousDayLocal.time)

        val eventEndTimeLocal = sdfUser.format(eventEndTimeUtc.time)

        eventDurationTxt.text = "$previousDayLocalString - $eventEndTimeLocal"
    }

    private fun setOnClickListeners() {
        helpBtn.setOnClickListener {
            val rulesList: List<String> = listOf(
                getString(R.string.the_daily_collection_event_is_a_daily_event_),
                getString(R.string.the_objective_of_the_event_is_),
                getString(R.string.there_are_several_ways_to_earn_points_during_the_event) +
                        getString(R.string.playing_the_games_available_in_our_app) +
                        getString(R.string.logging_in_daily) +
                        getString(R.string.spending_your_gamer_tickets),
                getString(R.string.participate_in_the_daily_collect_event_)
            )
            rulesDialog.showEventRulesDialog(rulesList)
        }

        backBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        firstRewardIcon.setOnClickListener {
            if (isAbleToClaimFirstReward) {
                val newCoinValue = coin + baseValue
                val setUserData = hashMapOf(
                    "coins" to newCoinValue
                )
                userData.set(setUserData, SetOptions.merge())
                    .addOnSuccessListener {
                        userData.addSnapshotListener { userSnapshot, _ ->
                            if (userSnapshot != null && userSnapshot.exists()) {
                                coin = userSnapshot.getField("coins")!!
                            }
                        }
                    }
                    .addOnFailureListener { errorToAddUserData ->
                        showCustomErrorToast(
                            this,
                            errorToAddUserData
                                .message.toString(),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                    }
                val updateDailyEventData = hashMapOf(
                    "firstGiftClaimed" to "yes"
                )
                dailyEvent.set(updateDailyEventData, SetOptions.merge())
                    .addOnSuccessListener {
                        firstRewardIcon.setImageResource(R.drawable.opened_gift)
                        isAbleToClaimFirstReward = false
                        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
                        numberFormat.minimumFractionDigits = 2
                        numberFormat.maximumFractionDigits = 2
                        val rewardNumberFormatted = numberFormat.format(baseValue)
                        val rewardNumberFloat = rewardNumberFormatted.replace(
                            ",",
                            "."
                        ).toFloat()
                        dialogShowReward.showRewardDialog(
                            R.drawable.star_reward_icon,
                            rewardNumberFloat
                        )
                    }
                    .addOnFailureListener { errorToUpdateEventData ->
                        showCustomErrorToast(
                            this,
                            errorToUpdateEventData
                                .message.toString(),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                        firstRewardIcon.setImageResource(R.drawable.gifticon)
                        isAbleToClaimFirstReward = true
                    }
            } else {
                dialogShowRewardDetail.showDialogRewardDetail(R.drawable.star_reward_icon, firstRewardIcon)
            }
        }

        secondRewardIcon.setOnClickListener {
            if (isAbleToClaimSecondReward) {
                val newCoinValue = coin + baseValue
                val setUserData = hashMapOf(
                    "coins" to newCoinValue
                )
                userData.set(setUserData, SetOptions.merge())
                    .addOnSuccessListener {
                        userData.addSnapshotListener { userSnapshot, _ ->
                            if (userSnapshot != null && userSnapshot.exists()) {
                                coin = userSnapshot.getField("coins")!!
                            }
                        }
                    }
                    .addOnFailureListener { errorToAddUserData ->
                        showCustomErrorToast(
                            this,
                            errorToAddUserData
                                .message.toString(),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                    }
                val updateDailyEventData = hashMapOf(
                    "secondGiftClaimed" to "yes"
                )
                dailyEvent.set(updateDailyEventData, SetOptions.merge())
                    .addOnSuccessListener {
                        secondRewardIcon.setImageResource(R.drawable.opened_gift)
                        isAbleToClaimSecondReward = false
                        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
                        numberFormat.minimumFractionDigits = 2
                        numberFormat.maximumFractionDigits = 2
                        val rewardNumberFormatted = numberFormat.format(baseValue)
                        val rewardNumberFloat = rewardNumberFormatted.replace(
                            ",",
                            "."
                        ).toFloat()
                        dialogShowReward.showRewardDialog(
                            R.drawable.star_reward_icon,
                            rewardNumberFloat
                        )
                    }
                    .addOnFailureListener { errorToUpdateEventData ->
                        showCustomErrorToast(
                            this,
                            errorToUpdateEventData
                                .message.toString(),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                        secondRewardIcon.setImageResource(R.drawable.gifticon)
                        isAbleToClaimSecondReward = true
                    }
            } else {
                dialogShowRewardDetail.showDialogRewardDetail(R.drawable.star_reward_icon, secondRewardIcon)
            }
        }

        thirdRewardIcon.setOnClickListener {
            if (isAbleToClaimThirdReward) {
                val newCoinValue = coin + baseValue
                val setUserData = hashMapOf(
                    "coins" to newCoinValue
                )
                userData.set(setUserData, SetOptions.merge())
                    .addOnSuccessListener {
                        userData.addSnapshotListener { userSnapshot, _ ->
                            if (userSnapshot != null && userSnapshot.exists()) {
                                coin = userSnapshot.getField("coins")!!
                            }
                        }
                    }
                    .addOnFailureListener { errorToAddUserData ->
                        showCustomErrorToast(
                            this,
                            errorToAddUserData
                                .message.toString(),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                    }
                val updateDailyEventData = hashMapOf(
                    "thirdGiftClaimed" to "yes"
                )
                dailyEvent.set(updateDailyEventData, SetOptions.merge())
                    .addOnSuccessListener {
                        thirdRewardIcon.setImageResource(R.drawable.opened_gift)
                        isAbleToClaimThirdReward = false
                        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
                        numberFormat.minimumFractionDigits = 2
                        numberFormat.maximumFractionDigits = 2
                        val rewardNumberFormatted = numberFormat.format(baseValue)
                        val rewardNumberFloat = rewardNumberFormatted.replace(
                            ",",
                            "."
                        ).toFloat()
                        dialogShowReward.showRewardDialog(
                            R.drawable.star_reward_icon,
                            rewardNumberFloat
                        )
                    }
                    .addOnFailureListener { errorToUpdateEventData ->
                        showCustomErrorToast(
                            this,
                            errorToUpdateEventData
                                .message.toString(),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                        thirdRewardIcon.setImageResource(R.drawable.gifticon)
                        isAbleToClaimThirdReward = true
                    }
            } else {
                dialogShowRewardDetail.showDialogRewardDetail(R.drawable.star_reward_icon, thirdRewardIcon)
            }
        }

        fourthRewardIcon.setOnClickListener {
            if (isAbleToClaimFourthReward) {
                val newCoinValue = coin + baseValue
                val setUserData = hashMapOf(
                    "coins" to newCoinValue
                )
                userData.set(setUserData, SetOptions.merge())
                    .addOnSuccessListener {
                        userData.addSnapshotListener { userSnapshot, _ ->
                            if (userSnapshot != null && userSnapshot.exists()) {
                                coin = userSnapshot.getField("coins")!!
                            }
                        }
                    }
                    .addOnFailureListener { errorToAddUserData ->
                        showCustomErrorToast(
                            this,
                            errorToAddUserData
                                .message.toString(),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                    }
                val updateDailyEventData = hashMapOf(
                    "fourthGiftClaimed" to "yes"
                )
                dailyEvent.set(updateDailyEventData, SetOptions.merge())
                    .addOnSuccessListener {
                        fourthRewardIcon.setImageResource(R.drawable.opened_gift)
                        isAbleToClaimFourthReward = false
                        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
                        numberFormat.minimumFractionDigits = 2
                        numberFormat.maximumFractionDigits = 2
                        val rewardNumberFormatted = numberFormat.format(baseValue)
                        val rewardNumberFloat = rewardNumberFormatted.replace(
                            ",",
                            "."
                        ).toFloat()
                        dialogShowReward.showRewardDialog(
                            R.drawable.star_reward_icon,
                            rewardNumberFloat
                        )
                    }
                    .addOnFailureListener { errorToUpdateEventData ->
                        showCustomErrorToast(
                            this,
                            errorToUpdateEventData
                                .message.toString(),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                        fourthRewardIcon.setImageResource(R.drawable.gifticon)
                        isAbleToClaimFourthReward = true
                    }
            } else {
                dialogShowRewardDetail.showDialogRewardDetail(R.drawable.star_reward_icon, fourthRewardIcon)
            }
        }

        claimFirstQuestBtn.setOnClickListener {
            if (isAbleToClaimFirstQuest) {
                loadingDialog.show()
                dailyEventPointsInt += 25
                val addPointsToEvent = hashMapOf(
                    "dailyEventPoints" to dailyEventPointsInt,
                    "collectStarsQuestDone" to "yes"
                )
                dailyEvent.set(addPointsToEvent, SetOptions.merge())
                    .addOnSuccessListener {
                        getDailyEventProgressData()
                    }
                    .addOnFailureListener { errorToUpdateDailyEvent ->
                        loadingDialog.hide()
                        showCustomErrorToast(
                            this,
                            errorToUpdateDailyEvent
                                .message.toString(),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                    }
            }
        }

        claimSecondQuestBtn.setOnClickListener {
            if (isAbleToClaimSecondQuest) {
                loadingDialog.show()
                dailyEventPointsInt += 50
                val addPointsToEvent: HashMap<String, Any> = hashMapOf(
                    "dailyEventPoints" to dailyEventPointsInt,
                    "use50TicketsQuestDone" to "yes"
                )
                dailyEvent.set(addPointsToEvent, SetOptions.merge())
                    .addOnSuccessListener {
                        getDailyEventProgressData()
                    }
                    .addOnFailureListener { errorToUpdateDailyEvent ->
                        loadingDialog.hide()
                        showCustomErrorToast(
                            this,
                            errorToUpdateDailyEvent
                                .message.toString(),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                    }
            }
        }

        claimThirdQuestBtn.setOnClickListener {
            if (isAbleToClaimThirdQuest) {
                loadingDialog.show()
                dailyEventPointsInt += 25
                val addPointsToEvent = hashMapOf(
                    "dailyEventPoints" to dailyEventPointsInt,
                    "dailyLogin" to "yes"
                )
                dailyEvent.set(addPointsToEvent, SetOptions.merge())
                    .addOnSuccessListener {
                        getDailyEventProgressData()
                    }
                    .addOnFailureListener { errorToUpdateDailyEvent ->
                        loadingDialog.hide()
                        showCustomErrorToast(
                            this,
                            errorToUpdateDailyEvent
                                .message.toString(),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                    }
            }
        }
    }

    private fun initializeViews() {
        eventNameTxt = findViewById(R.id.event_name)
        missionsTxt = findViewById(R.id.missions_txt)
        eventDurationTxt = findViewById(R.id.event_duration_txt)
        userEventPointsTxt = findViewById(R.id.user_event_points_txt)
        secondQuestProgressTxt = findViewById(R.id.second_quest_progress_txt)
        claimTxt = findViewById(R.id.claim_txt)
        claimTxt1 = findViewById(R.id.claim2_txt)
        claimTxt2 = findViewById(R.id.claim3_txt)

        loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        rulesDialog = RulesDialog(this)
        dialogShowRewardDetail = CustomDialogRewardDetail(this)
        dialogShowReward = RewardDialog(this)

        firstRewardIcon = findViewById(R.id.gift1)
        secondRewardIcon = findViewById(R.id.gift2)
        thirdRewardIcon = findViewById(R.id.gift3)
        fourthRewardIcon = findViewById(R.id.gift4)
        claimFirstQuestBtn = findViewById(R.id.claim_btn)
        firstQuestPointsReward = findViewById(R.id.first_quest_points_reward)
        firstQuestPointsRewardTxt = findViewById(R.id.first_quest_points_reward_txt)
        secondQuestPointsReward = findViewById(R.id.second_quest_points_reward)
        secondQuestPointsRewardTxt = findViewById(R.id.second_quest_points_reward_txt)
        thirdQuestPointsReward = findViewById(R.id.third_quest_points_reward)
        thirdQuestPointsRewardTxt = findViewById(R.id.third_quest_points_reward_txt)
        firstQuestFrame = findViewById(R.id.first_quest_frame)
        secondQuestFrame = findViewById(R.id.second_quest_frame)
        thirdQuestFrame = findViewById(R.id.third_quest_frame)
        claimSecondQuestBtn = findViewById(R.id.claim2_btn)
        claimThirdQuestBtn = findViewById(R.id.claim3_btn)
        firstQuestProgress = findViewById(R.id.first_quest_progress)
        firstQuestProgressTxt = findViewById(R.id.first_quest_progress_txt)
        secondQuestProgress = findViewById(R.id.second_quest_progress)
        helpBtn = findViewById(R.id.help_icon)
        claimBtnBackground = findViewById(R.id.claim_btn_background)
        claimBtnBackground1 = findViewById(R.id.claim_btn_background1)
        claimBtnBackground2 = findViewById(R.id.claim_btn_background2)
        eventPointsProgress = findViewById(R.id.eventPointsProgress)
        backBtn = findViewById(R.id.back_btn)
        firstQuestDesc = findViewById(R.id.first_quest_desc)
        secondQuestDesc = findViewById(R.id.second_quest_desc)
        firstQuestIcon = findViewById(R.id.first_quest_icon)
        secondQuestIcon = findViewById(R.id.second_quest_icon)
        firstQuestDesc.text = getString(R.string.collect_250_stars, 250)
        secondQuestDesc.text = getString(R.string.use_50_tickets, 50)
        claim1UI(false)
        claim2UI(false)
        claim3UI(false)
    }

    private fun calculateVipLevel(vipPoints: Long): Int {
        return (vipPoints / 500).toInt()
    }

    private fun fetchUserData() {
        userData.get()
            .addOnSuccessListener { userSnapshot ->
                userSnapshot
                    .getField<Float>("coins")
                    .let { coins ->
                        coin = coins as Float
                    }
                val getUserVipPoints = userSnapshot.getLong("vipPoints") ?: 0
                vipLevel = calculateVipLevel(getUserVipPoints)
                firebaseRemoteConfigDailyEvent = FirebaseRemoteConfigDailyEvent(vipLevel)

                firebaseRemoteConfigDailyEvent.calculateGiftRewards { updatedValue ->
                    baseValue = updatedValue
                }
                /*firebaseRemoteConfigDailyEvent.fetchGoalToCompleteMissions {
                    missionUseTicketsGoal = firebaseRemoteConfigDailyEvent.useTicketsQuestGoal
                    missionCollectStarsGoal = firebaseRemoteConfigDailyEvent.collectStarsQuestGoal
                    firstQuestProgress.max = missionCollectStarsGoal!!.toInt()
                    secondQuestProgress.max = missionUseTicketsGoal?.toInt() ?: 0

                }*/
                getDailyEventProgressData()
            }
            .addOnFailureListener { exception ->
                showCustomErrorToast(
                    this,
                    exception.message.toString(),
                    Toast.LENGTH_LONG,
                    this.window
                )
                backBtn.performClick()
            }
    }

    private fun getDailyEventProgressData() {
        dailyEvent.get()
            .addOnSuccessListener { documentSnapshot ->
                loadingDialog.hide()
                documentSnapshot
                    ?.getLong("dailyEventPoints")
                    ?.let { dailyEventPointsSnapshot ->
                        dailyEventPointsInt = dailyEventPointsSnapshot.toInt()
                        eventPointsProgress.progress = dailyEventPointsSnapshot.toInt()
                        userEventPointsTxt.text = getString(R.string.points_big, dailyEventPointsSnapshot)
                    } ?: run {
                    eventPointsProgress.progress = 0
                    userEventPointsTxt.text = getString(R.string.points_big, 0)
                }

                val gifts = listOf(
                    Pair(firstRewardIcon, "firstGiftClaimed"),
                    Pair(secondRewardIcon, "secondGiftClaimed"),
                    Pair(thirdRewardIcon, "thirdGiftClaimed"),
                    Pair(fourthRewardIcon, "fourthGiftClaimed")
                )

                gifts.forEach { (giftIcon, giftClaimedField) ->
                    if (documentSnapshot.contains(giftClaimedField)) {
                        giftIcon.setImageResource(R.drawable.opened_gift)
                    } else {
                        when (giftIcon) {
                            firstRewardIcon -> isAbleToClaimFirstReward = dailyEventPointsInt >= 25
                            secondRewardIcon -> isAbleToClaimSecondReward = dailyEventPointsInt >= 50
                            thirdRewardIcon -> isAbleToClaimThirdReward = dailyEventPointsInt >= 75
                            fourthRewardIcon -> isAbleToClaimFourthReward = dailyEventPointsInt >= 100
                        }
                        giftIcon.setImageResource(R.drawable.gifticon)
                    }
                }

                documentSnapshot?.run {
                    getString("collectStarsQuestDone")
                        ?.let { firstQuestDoneUI() }
                        ?: getLong("collectedStars")
                            ?.let { collectedStars ->
                                firstQuestProgressTxt.text = getString(R.string.barra, collectedStars, 250)
                                if (collectedStars >= 250) {
                                    isAbleToClaimFirstQuest = true
                                    claim1UI(true)
                                    firstQuestProgressTxt.setTextColor(
                                        ContextCompat.getColor(
                                            this@DailyEventActivity,
                                            R.color.greenProgressTxt
                                        ))
                                }
                                firstQuestProgress.progress = collectedStars.toInt()
                            } ?: run {
                            firstQuestProgressTxt.text = getString(R.string.barra, 0, 250)
                            firstQuestProgress.progress = 0
                            firstQuestProgressTxt.setTextColor(
                                ContextCompat.getColor(
                                    this@DailyEventActivity,
                                    R.color.redProgressTxt
                                ))
                        }

                    getString("use50TicketsQuestDone")
                        ?.let {
                            secondQuestDoneUI()
                            secondQuestProgressTxt.text = getString(R.string.barra, 50, 50)
                            secondQuestProgress.progress = 50
                        }
                        ?: getLong("usedTickets")
                            ?.let { usedTickets ->
                                secondQuestProgressTxt.text = getString(R.string.barra, usedTickets, 50)
                                secondQuestProgress.progress = usedTickets.toInt()
                                if (usedTickets >= 50) {
                                    isAbleToClaimSecondQuest = true
                                    claim2UI(true)
                                    secondQuestProgressTxt.setTextColor(
                                        ContextCompat.getColor(
                                            this@DailyEventActivity,
                                            R.color.greenProgressTxt
                                        ))
                                }
                            }
                        ?: run {
                            secondQuestProgressTxt.text = getString(R.string.barra, 0, 50)
                            secondQuestProgress.progress = 0
                            secondQuestProgressTxt.setTextColor(
                                ContextCompat.getColor(
                                    this@DailyEventActivity,
                                    R.color.redProgressTxt
                                ))
                        }
                    getString("dailyLogin")
                        ?.let {
                            isAbleToClaimThirdQuest = false
                            thirdQuestDoneUI()
                        } ?: run {
                        isAbleToClaimThirdQuest = true
                        claim3UI(true)
                    }
                }

            }
            .addOnFailureListener { exception ->
                loadingDialog.hide()
                showCustomErrorToast(
                    this,
                    exception.message.toString(),
                    Toast.LENGTH_LONG,
                    this.window
                )
                backBtn.performClick()
            }

    }

    private fun textsUI(){
        val textShader: Shader = LinearGradient(
            0f, 12f, 0f, eventNameTxt.textSize,
            intArrayOf(
                Color.parseColor("#CED2FD"),
                Color.parseColor("#767B9B")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        eventNameTxt.paint.shader = textShader

        val textShader2: Shader = LinearGradient(
            0f, 24f, 0f, missionsTxt.textSize,
            intArrayOf(
                Color.parseColor("#FFE17C"),
                Color.parseColor("#F8D661")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        missionsTxt.paint.shader = textShader2

        val textShader3: Shader = LinearGradient(
            0f, 24f, 0f, eventDurationTxt.textSize,
            intArrayOf(
                Color.parseColor("#FFE17C"),
                Color.parseColor("#F8D661")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        eventDurationTxt.paint.shader = textShader3

        val textShader9: Shader = LinearGradient(
            0f, 24f, 0f, userEventPointsTxt.textSize,
            intArrayOf(
                Color.parseColor("#FFE17C"),
                Color.parseColor("#F8D661")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        userEventPointsTxt.paint.shader = textShader9

    }

    private fun claim1UI(enabled : Boolean){
        if(enabled){
            claimTxt.text = getString(R.string.receive)
            if (claimTxt.text.length > 8){
                claimTxt.textSize = 10F
            }
            val textShader4: Shader = LinearGradient(
                0f, 0f, 0f, claimTxt.textSize,
                intArrayOf(
                    Color.parseColor("#66422A"),
                    Color.parseColor("#291000")
                ),
                null,
                Shader.TileMode.MIRROR
            )
            claimTxt.paint.shader = textShader4
            claimBtnBackground.setBackgroundResource(R.drawable.active_button)

        }
        else {
            claimTxt.text = getString(R.string.incomplete)
            if (claimTxt.text.length > 8){
                claimTxt.textSize = 10F
            }
            val textShader9: Shader = LinearGradient(
                0f, 0f, 0f, claimTxt.textSize,
                intArrayOf(
                    Color.parseColor("#696969"),
                    Color.parseColor("#282828")
                ),
                null,
                Shader.TileMode.MIRROR
            )
            claimTxt.paint.shader = textShader9
            claimBtnBackground.setBackgroundResource(R.drawable.event_unable_button)
        }
    }

    private fun claim2UI(enabled : Boolean){
        if (enabled){
            claimTxt1.text = getString(R.string.receive)
            if (claimTxt1.text.length > 8){
                claimTxt1.textSize = 10F
            }
            val textShader5: Shader = LinearGradient(
                0f, 0f, 0f, claimTxt1.textSize,
                intArrayOf(
                    Color.parseColor("#66422A"),
                    Color.parseColor("#291000")
                ),
                null,
                Shader.TileMode.MIRROR
            )
            claimTxt1.paint.shader = textShader5
            claimBtnBackground1.setBackgroundResource(R.drawable.active_button)
        } else {
            claimTxt1.text = getString(R.string.incomplete)
            if (claimTxt1.text.length > 8){
                claimTxt1.textSize = 10F
            }
            val textShader9: Shader = LinearGradient(
                0f, 0f, 0f, claimTxt1.textSize,
                intArrayOf(
                    Color.parseColor("#696969"),
                    Color.parseColor("#282828")
                ),
                null,
                Shader.TileMode.MIRROR
            )
            claimTxt1.paint.shader = textShader9
            claimBtnBackground1.setBackgroundResource(R.drawable.event_unable_button)
        }

    }

    private fun claim3UI(enabled: Boolean){
        if (enabled){
            claimTxt2.text = getString(R.string.receive)
            if (claimTxt2.text.length > 8){
                claimTxt2.textSize = 10F
            }
            val textShader5: Shader = LinearGradient(
                0f, 0f, 0f, claimTxt2.textSize,
                intArrayOf(
                    Color.parseColor("#66422A"),
                    Color.parseColor("#291000")
                ),
                null,
                Shader.TileMode.MIRROR
            )
            claimTxt2.paint.shader = textShader5
            claimBtnBackground2.setBackgroundResource(R.drawable.active_button)
        } else {
            claimTxt2.text = getString(R.string.incomplete)
            if (claimTxt2.text.length > 8){
                claimTxt2.textSize = 10F
            }
            val textShader9: Shader = LinearGradient(
                0f, 0f, 0f, claimTxt2.textSize,
                intArrayOf(
                    Color.parseColor("#696969"),
                    Color.parseColor("#282828")
                ),
                null,
                Shader.TileMode.MIRROR
            )
            claimTxt2.paint.shader = textShader9
            claimBtnBackground2.setBackgroundResource(R.drawable.event_unable_button)
        }
    }

    private fun firstQuestDoneUI() {
        val context : Context = this@DailyEventActivity
        val textView3 = TextView(context)
        textView3.id = View.generateViewId()
        textView3.text = getString(R.string.collected)
        textView3.setTextColor(Color.WHITE)
        textView3.textSize = 12f
        textView3.typeface = ResourcesCompat.getFont(this, R.font.inter_black)
        val textShader1: Shader = LinearGradient(
            0f, 0f, 0f, textView3.textSize,
            intArrayOf(
                Color.parseColor("#FFD66E"),
                Color.parseColor("#A48B34")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        textView3.paint.shader = textShader1

        claimFirstQuestBtn.visibility = View.GONE
        firstQuestPointsReward.visibility = View.GONE
        firstQuestPointsRewardTxt.visibility = View.GONE
        firstQuestProgress.visibility = View.GONE
        firstQuestProgressTxt.visibility = View.GONE
        firstQuestIcon.marginTop to 0
        firstQuestIcon.marginBottom to 0

        val parentLayout = findViewById<ConstraintLayout>(R.id.background)

        parentLayout.addView(textView3)

        val constraintSet = ConstraintSet()
        constraintSet.constrainWidth(textView3.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(textView3.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(textView3.id, ConstraintSet.TOP, firstQuestFrame.id, ConstraintSet.TOP)
        constraintSet.connect(textView3.id, ConstraintSet.BOTTOM, firstQuestFrame.id, ConstraintSet.BOTTOM)
        constraintSet.connect(textView3.id, ConstraintSet.END, firstQuestFrame.id, ConstraintSet.END, 48)
        constraintSet.applyTo(parentLayout)
    }

    private fun secondQuestDoneUI() {
        val context455 : Context = this@DailyEventActivity
        val textView2 = TextView(context455)
        textView2.id = View.generateViewId()
        textView2.text = getString(R.string.collected)
        textView2.setTextColor(Color.WHITE)
        textView2.textSize = 12f
        textView2.typeface = ResourcesCompat.getFont(this, R.font.inter_black)
        val textShader3e: Shader = LinearGradient(
            0f, 0f, 0f, textView2.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#FFD66E"), // First color
                Color.parseColor("#A48B34") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        textView2.paint.shader = textShader3e

        claimSecondQuestBtn.visibility = View.GONE
        secondQuestPointsReward.visibility = View.GONE
        secondQuestPointsRewardTxt.visibility = View.GONE
        secondQuestProgress.visibility = View.GONE
        secondQuestProgressTxt.visibility = View.GONE

        val parentLayout = findViewById<ConstraintLayout>(R.id.background)

        parentLayout.addView(textView2)

        val constraintSet = ConstraintSet()
        constraintSet.constrainWidth(textView2.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(textView2.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.connect(textView2.id, ConstraintSet.TOP, secondQuestFrame.id, ConstraintSet.TOP)
        constraintSet.connect(textView2.id, ConstraintSet.BOTTOM, secondQuestFrame.id, ConstraintSet.BOTTOM)
        constraintSet.connect(textView2.id, ConstraintSet.END, secondQuestFrame.id, ConstraintSet.END, 48)
        constraintSet.applyTo(parentLayout)

    }

    private fun thirdQuestDoneUI() {
        val context7776 : Context = this@DailyEventActivity
        val textView3 = TextView(context7776)
        textView3.id = View.generateViewId()
        textView3.text = getString(R.string.collected)
        textView3.setTextColor(Color.WHITE)
        textView3.textSize = 12f
        textView3.typeface = ResourcesCompat.getFont(this, R.font.inter_black)
        val textShader2: Shader = LinearGradient(
            0f, 0f, 0f, textView3.textSize,
            intArrayOf(
                Color.parseColor("#FFD66E"),
                Color.parseColor("#A48B34")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        textView3.paint.shader = textShader2

        claimThirdQuestBtn.visibility = View.GONE
        thirdQuestPointsReward.visibility = View.GONE
        thirdQuestPointsRewardTxt.visibility = View.GONE

        val parentLayout3743 = findViewById<ConstraintLayout>(R.id.background)

        parentLayout3743.addView(textView3)

        val constraintSet45 = ConstraintSet()
        constraintSet45.constrainWidth(textView3.id, ConstraintSet.WRAP_CONTENT)
        constraintSet45.constrainHeight(textView3.id, ConstraintSet.WRAP_CONTENT)
        constraintSet45.connect(textView3.id, ConstraintSet.TOP, thirdQuestFrame.id, ConstraintSet.TOP)
        constraintSet45.connect(textView3.id, ConstraintSet.BOTTOM, thirdQuestFrame.id, ConstraintSet.BOTTOM)
        constraintSet45.connect(textView3.id, ConstraintSet.END, thirdQuestFrame.id, ConstraintSet.END, 48)
        constraintSet45.applyTo(parentLayout3743)
    }
}