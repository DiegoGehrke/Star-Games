@file:Suppress("DEPRECATION")

package com.star.games.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.model.Placement
import com.ironsource.mediationsdk.sdk.RewardedVideoListener
import com.star.games.android.ErrorToastUtils.showCustomErrorToast
import kotlin.properties.Delegates


private lateinit var intentBack: Intent

private lateinit var imageView6: ImageView
private lateinit var vipAddPoints: ImageView
private lateinit var goToNextVipBenefitFrame: ImageView
private lateinit var goToPreviousVipBenefitFrame: ImageView
private lateinit var helpBtn: ImageView

private lateinit var currentUserVipPoints: TextView
private lateinit var currentUserVipLevel: TextView
private lateinit var firstBenefitDesc: TextView
private lateinit var firstBenefitBonus: TextView
private lateinit var currentVipLevelBenefits: TextView
private lateinit var screenNameTxt: TextView

private var currentPage = 0
private var vipLevel = 0
private var getUserVipPoints: Float = 0.00f
private var getVipPointsDailyLimit: Long = 50
private var lastClickTime: Long = 0
private var currentTime by Delegates.notNull<Long>()
private var coolDownRemaining by Delegates.notNull<Long>()

private var successfullyRewarded = false

private val user  = FirebaseAuth.getInstance().currentUser
private val db = Firebase.firestore
private val uid = user!!.uid
private val userData = db.collection("USERS").document(uid)
private val dailyUserGetMoreVipPointsLimit = db.collection("DAILY USER VIP").document(uid)

private lateinit var getVipSharedPreferences: SharedPreferences

private lateinit var cooldownDialog: CooldownDialog
private lateinit var rulesDialog: RulesDialog

private lateinit var vipLevelBenefitsDatabase: VipLevelBenefitsDatabase

@Suppress("OVERRIDE_DEPRECATION")
class VipActivity : AppCompatActivity(),  RewardedVideoListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vip)

        getVipSharedPreferences = getSharedPreferences("getTicketCooldown", Context.MODE_PRIVATE)
        lastClickTime = getVipSharedPreferences.getLong("lastClickTime", 0)

        initializeViews()
        setListeners()
        setupTextsUI()
        fetchUserData()
    }

    private fun initializeViews() {
        intentBack = Intent(this, HomeActivity::class.java)
        currentUserVipPoints = findViewById(R.id.user_current_vip_points)
        currentUserVipLevel = findViewById(R.id.vip_level_txt)
        imageView6 = findViewById(R.id.imageView6)
        vipAddPoints = findViewById(R.id.vip_add_points)
        goToNextVipBenefitFrame = findViewById(R.id.next)
        goToPreviousVipBenefitFrame = findViewById(R.id.back)
        firstBenefitDesc = findViewById(R.id.first_benefit_desc)
        firstBenefitBonus = findViewById(R.id.first_benefit_bonus)
        currentVipLevelBenefits = findViewById(R.id.current_vip_level_benefits)
        screenNameTxt = findViewById(R.id.screen_name)
        helpBtn = findViewById(R.id.help_icon)
        rulesDialog = RulesDialog(this)
        cooldownDialog = CooldownDialog(this, false)
    }

    private fun setListeners() {
        goToNextVipBenefitFrame.setOnClickListener {
            currentPage++
            if (currentPage >= 33) {
                currentPage = 0
                vipLevelBenefitsDatabase.levelOneUI(currentPage)
            } else {
                vipLevelBenefitsDatabase.levelOneUI(currentPage)
            }
        }

        goToPreviousVipBenefitFrame.setOnClickListener {
            currentPage--
            if (currentPage < 0) {
                currentPage = 32
                vipLevelBenefitsDatabase.levelOneUI(currentPage)
            } else {
                vipLevelBenefitsDatabase.levelOneUI(currentPage)
            }
        }

        backToTheHomeScreenListeners()

        IronSource.setRewardedVideoListener(this@VipActivity)

        vipAddPoints.setOnClickListener {
            currentTime = System.currentTimeMillis()
            lastClickTime = getVipSharedPreferences.getLong("lastClickTime", 0)
            coolDownRemaining = lastClickTime - currentTime
            if (coolDownRemaining > 0) {
                cooldownDialog.showCooldownDialog(coolDownRemaining)
            } else {
                if (getVipPointsDailyLimit >= 15) {
                    showCustomErrorToast(
                        this,
                        getString(R.string.daily_limit_reached),
                        Toast.LENGTH_LONG,
                        this.window
                    )
                } else {
                    if (IronSource.isRewardedVideoAvailable()) {
                        val newClickTime = System.currentTimeMillis() + (1 * 60 * 1000)

                        with(getVipSharedPreferences.edit()) {
                            putLong("lastClickTime", newClickTime)
                            apply()
                        }
                        IronSource.showRewardedVideo()
                    } else {
                        showCustomErrorToast(
                            this,
                            getString(R.string.please_try_again_in_a_few_seconds),
                            Toast.LENGTH_LONG,
                            this.window
                        )
                    }
                }
            }
        }

        helpBtn.setOnClickListener {
            val howTheVipWorks: List<String> = listOf(
                getString(R.string.vip_is_a_system_where_),
                getString(R.string.to_evolve_from_vip_level_),
                getString(R.string.you_can_watch_ads_by_),
                getString(R.string.the_daily_limit_for_earning_points_through_ads_resets_every_day_at_)
            )
            rulesDialog.showEventRulesDialog(howTheVipWorks)
        }
    }

    private fun setupTextsUI() {
        val ts11111: Shader = LinearGradient(
            0f, 12f, 0f, screenNameTxt.textSize,
            intArrayOf(
                Color.parseColor("#CED2FD"),
                Color.parseColor("#767B9B")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        screenNameTxt.paint.shader = ts11111
    }

    private fun fetchUserData() {
        userData.get()
            .addOnSuccessListener { document ->
                getUserVipPoints = document.getField("vipPoints") ?: 0.00f
                vipLevel = calculateVipLevel(getUserVipPoints)
                vipLevelBenefitsDatabase = VipLevelBenefitsDatabase(
                    context = this@VipActivity,
                    starGainPercentage = firstBenefitBonus,
                    showingBenefitsFromLevel = currentVipLevelBenefits,
                    vipLevel = vipLevel,
                    userCurrentVipPoints = getUserVipPoints.toInt(),
                    showGoalToNextVipLevel = currentUserVipPoints,
                    iconUnlockedOrLockedVip = imageView6
                )
                vipLevelBenefitsDatabase.levelOneUI()
                updateUserInterface()
            }

        dailyUserGetMoreVipPointsLimit
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.contains("dailyLimit")) {
                    getVipPointsDailyLimit = documentSnapshot.getLong("dailyLimit")!!
                } else {
                    val loadingDialog = LoadingDialog(this)
                    loadingDialog.show()
                    val createDocumentField: HashMap<String, Any> = hashMapOf(
                        "dailyLimit" to 0
                    )
                    dailyUserGetMoreVipPointsLimit
                        .set(createDocumentField)
                        .addOnSuccessListener {
                            loadingDialog.hide()
                            getVipPointsDailyLimit = 0
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

    private fun calculateVipLevel(vipPoints: Float): Int {
        return (vipPoints / 500).toInt()
    }

    private fun updateUserInterface() {
        currentUserVipLevel.text = vipLevel.toString()
        if (vipLevel > 0) {
            vipLevelBenefitsDatabase.levelOneUI(currentPosition = (vipLevel - 1))
        }
    }

    override fun onRewardedVideoAdOpened() {}

    override fun onRewardedVideoAdClosed() {
        if (successfullyRewarded) {
            successfullyRewarded = false
            val rewardDialog = RewardDialog(this@VipActivity)
            rewardDialog.showRewardDialog(R.drawable.vip_item_icon, 33.34f)
        }
    }

    override fun onRewardedVideoAvailabilityChanged(p0: Boolean) {}

    override fun onRewardedVideoAdStarted() {}

    override fun onRewardedVideoAdEnded() {}

    override fun onRewardedVideoAdRewarded(p0: Placement?) {
        userData.get()
            .addOnSuccessListener { documentSnapshot ->
                getUserVipPoints = documentSnapshot.getField("vipPoints")!!
                val addVipPoints = getUserVipPoints + 33.34
                val data: HashMap<String, Any> = hashMapOf("vipPoints" to addVipPoints)
                userData.update(data)
                    .addOnSuccessListener {
                        userData.addSnapshotListener { value, _ ->
                            val getDocumentFieldValue: Float = value!!.getField("vipPoints")!!
                            currentUserVipPoints.text = getDocumentFieldValue.toString()
                            vipLevel = calculateVipLevel(value.getField("vipPoints") ?: 0.00f)
                            currentUserVipLevel.text = vipLevel.toString()
                            fetchUserData()
                            updateUserInterface()
                        }
                    }
            }
        successfullyRewarded = true

        val addData: HashMap<String, Any> = hashMapOf(
            "dailyLimit" to getVipPointsDailyLimit + 1
        )
        dailyUserGetMoreVipPointsLimit.update(addData)
            .addOnSuccessListener {
                dailyUserGetMoreVipPointsLimit.addSnapshotListener { value, _ ->
                    getVipPointsDailyLimit = value!!.getLong("dailyLimit")!!
                }
            }
    }

    override fun onRewardedVideoAdShowFailed(p0: IronSourceError?) {}

    override fun onRewardedVideoAdClicked(p0: Placement?) {}

    private fun backToTheHomeScreenListeners() {
        val backToHomeActivity: ImageView = findViewById(R.id.back_btn)
        backToHomeActivity.setOnClickListener {
            startActivity(intentBack)
            finish()
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    startActivity(intentBack)
                    finish()
                }
            }
        )
    }
}
