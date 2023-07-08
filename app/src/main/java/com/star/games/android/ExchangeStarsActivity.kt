package com.star.games.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import com.star.games.android.ErrorToastUtils.showCustomErrorToast
import com.star.games.android.SuccessToastUtils.showCustomSuccessToast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

class ExchangeStarsActivity : AppCompatActivity() {

    private lateinit var buttonRequestOption: ImageView
    private lateinit var buttonRequestFirstOption: ImageView
    private lateinit var buttonRequestSecondOption: ImageView
    private lateinit var buttonRequestThirdOption: ImageView
    private lateinit var backButton: ImageView

    private lateinit var sendRequestTxtOption: TextView
    private lateinit var sendRequestTxtFirstOption: TextView
    private lateinit var sendRequestTxtSecondOption: TextView
    private lateinit var sendRequestTxtThirdOption: TextView
    private lateinit var screenName: TextView

    private var userStars: Float = 0.00F
    private var vipLevel: Int = 0

    private var currentTime by Delegates.notNull<Long>()
    private var coolDownRemaining by Delegates.notNull<Long>()
    private var lastClickTime by Delegates.notNull<Long>()

    private var recipientPIXKey: String = "null"
    private var friendCode: String = "null"

    private var havePIXKey: Boolean = false
    private var invitedAnFriend: Boolean = false

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var cooldownDialog: CooldownDialog
    private lateinit var vipResetDialog: VipResetDialog

    private lateinit var colorFilter: ColorMatrixColorFilter

    private val user = FirebaseAuth.getInstance().currentUser
    private val db = Firebase.firestore
    private val uid = user!!.uid
    private val userData = db.collection("USERS").document(uid)
    private val bankInfo = db.collection("USERS BANK INFO").document(uid)
    private val withdrawalsRequests = db.collection("WITHDRAWALS REQUESTS")

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange_stars)

        val getExtras: Bundle = intent.extras!!
        vipLevel = getExtras.getInt("vipLevel")

        sharedPreferences = getSharedPreferences("CoolDownPrefs", Context.MODE_PRIVATE)

        cooldownDialog = CooldownDialog(this, true)

        vipResetDialog = VipResetDialog(this, this)

        lastClickTime = sharedPreferences.getLong("lastClickTime", 0)
        currentTime = System.currentTimeMillis()
        coolDownRemaining = lastClickTime - currentTime
        if (coolDownRemaining > 0) {
            cooldownDialog.showCooldownDialog(coolDownRemaining)
        }

        buttonRequestOption = findViewById(R.id.send_request_option_btn)
        buttonRequestFirstOption = findViewById(R.id.send_request_first_option_btn)
        buttonRequestSecondOption = findViewById(R.id.send_request_second_option_btn)
        sendRequestTxtOption = findViewById(R.id.send_request_txt_option)
        sendRequestTxtFirstOption = findViewById(R.id.send_request_txt_first_option)
        sendRequestTxtSecondOption = findViewById(R.id.send_request_txt_second_option)
        buttonRequestThirdOption = findViewById(R.id.send_request_third_option_btn)
        sendRequestTxtThirdOption = findViewById(R.id.send_request_txt_third_option)
        screenName = findViewById(R.id.screen_name)
        backButton = findViewById(R.id.back_btn)

        backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val textShader: Shader = LinearGradient(
            0f, 24f, 0f, screenName.textSize,
            intArrayOf(
                Color.parseColor("#CED2FD"),
                Color.parseColor("#767B9B")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        screenName.paint.shader = textShader

        loadingDialog = LoadingDialog(this)
        loadingDialog.show()

        optionChangeButtonUI(false)
        firstOptionChangeButtonUI(false)
        secondOptionChangeButtonUI(false)
        thirdOptionChangeButtonUI(false)

        userData.get()
            .addOnSuccessListener { coinSnapshot ->
                loadingDialog.hide()
                coinSnapshot
                    .getField<Float>("coins")
                    .let { userCoins ->
                        userStars = userCoins!!.toFloat()
                        optionChangeButtonUI(optionEnabled = userStars >= 2500)
                        firstOptionChangeButtonUI(firstOptionEnabled = userStars >= 5000)
                        secondOptionChangeButtonUI(secondOptionEnabled = userStars >= 10000)
                        thirdOptionChangeButtonUI(thirdOptionEnabled = userStars >= 20000)

                    }
                coinSnapshot
                    .getString("friendCode")
                    .let { friendCodeSnapshot ->
                        friendCode = friendCodeSnapshot.toString()
                    }

                coinSnapshot
                    .getLong("invitedFriends")
                    .let { invitedFriends ->
                        invitedAnFriend = invitedFriends!! >= 1
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
            }

        bankInfo.get()
            .addOnSuccessListener { bankSnapshot ->
                bankSnapshot
                    ?.getString("pixKey")
                    ?.let { pixKey ->
                        recipientPIXKey = pixKey
                        havePIXKey = true
                    } ?: run {
                    havePIXKey = false
                    val dialogSettingsPaymentMethod = DialogSettingsPaymentMethod(this, bankInfo)
                    dialogSettingsPaymentMethod.showSetPaymentDialog(
                        getString(R.string.define_pix_key),
                        getString(R.string.set_a_pix_key_for_us_to_send_you_the_money_to_your_bank_account),
                        getString(R.string.enter_your_pix_key),
                        isPix = true
                    )
                }
            }

        buttonRequestOption.setOnClickListener {
            loadingDialog.show()
            if (invitedAnFriend && vipLevel > 0) {
                vipResetDialog.showVipResetDialog(
                    getString(R.string.confirm_action),
                    getString(R.string.by_proceeding_your_vip_progress_will_be_reset_are_you_sure_you_want_to_continue),
                   0.5F,
                    2500
                )
            } else {
                loadingDialog.hide()
                showCustomErrorToast(
                    this,
                    getString(R.string.you_must_have_invited_at_least_1_friend_to_perform_this_action),
                    Toast.LENGTH_LONG,
                    this.window
                )
            }
        }

        buttonRequestFirstOption.setOnClickListener {
            loadingDialog.show()
            if (invitedAnFriend && vipLevel > 0) {
                vipResetDialog.showVipResetDialog(
                    getString(R.string.confirm_action),
                    getString(R.string.by_proceeding_your_vip_progress_will_be_reset_are_you_sure_you_want_to_continue),
                    1.0F,
                    5000
                )
            } else {
                loadingDialog.hide()
                showCustomErrorToast(
                    this,
                    getString(R.string.you_must_have_invited_at_least_1_friend_to_perform_this_action),
                    Toast.LENGTH_LONG,
                    this.window
                )
            }
        }

        buttonRequestSecondOption.setOnClickListener {
            loadingDialog.show()
            if (invitedAnFriend && vipLevel > 0 ) {
                vipResetDialog.showVipResetDialog(
                    getString(R.string.confirm_action),
                    getString(R.string.by_proceeding_your_vip_progress_will_be_reset_are_you_sure_you_want_to_continue),
                    5.0F,
                    10000
                )
            } else {
                loadingDialog.hide()
                showCustomErrorToast(
                    this,
                    getString(R.string.you_must_have_invited_at_least_1_friend_to_perform_this_action),
                    Toast.LENGTH_LONG,
                    this.window
                )
            }
        }

        buttonRequestThirdOption.setOnClickListener {
            loadingDialog.show()
            if (invitedAnFriend && vipLevel > 0) {
                vipResetDialog.showVipResetDialog(
                    getString(R.string.confirm_action),
                    getString(R.string.by_proceeding_your_vip_progress_will_be_reset_are_you_sure_you_want_to_continue),
                    10.0F,
                    20000
                )
            } else {
                loadingDialog.hide()
                showCustomErrorToast(
                    this,
                    getString(R.string.you_must_have_invited_at_least_1_friend_to_perform_this_action),
                    Toast.LENGTH_LONG,
                    this.window
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

    private fun optionChangeButtonUI(optionEnabled: Boolean) {
        if (optionEnabled) {
            buttonRequestOption.isEnabled = true
            buttonRequestOption.colorFilter = null
            val textEnabledColor = Color.parseColor("#684E26")
            sendRequestTxtOption.setTextColor(textEnabledColor)
        } else {
            buttonRequestOption.isEnabled = false
            val matrix = floatArrayOf(
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
            val colorMatrix = ColorMatrix(matrix)
            colorFilter = ColorMatrixColorFilter(colorMatrix)
            //BLACK AND WHITE FILTER!
            buttonRequestOption.colorFilter = colorFilter

            val textDisabledColor = Color.parseColor("#656565")
            sendRequestTxtOption.setTextColor(textDisabledColor)
        }
    }

    private fun firstOptionChangeButtonUI(firstOptionEnabled: Boolean) {
        if (firstOptionEnabled) {
            buttonRequestFirstOption.isEnabled = true
            buttonRequestFirstOption.colorFilter = null
            val textEnabledColor = Color.parseColor("#684E26")
            sendRequestTxtFirstOption.setTextColor(textEnabledColor)
        } else {
            buttonRequestFirstOption.isEnabled = false
            val matrix = floatArrayOf(
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
            val colorMatrix = ColorMatrix(matrix)
            colorFilter = ColorMatrixColorFilter(colorMatrix)
            //BLACK AND WHITE FILTER!
            buttonRequestFirstOption.colorFilter = colorFilter

            val textDisabledColor = Color.parseColor("#656565")
            sendRequestTxtFirstOption.setTextColor(textDisabledColor)
        }
    }

    private fun secondOptionChangeButtonUI(secondOptionEnabled: Boolean) {
        if (secondOptionEnabled) {
            buttonRequestSecondOption.isEnabled = true
            buttonRequestSecondOption.colorFilter = null
            val textEnabledColor = Color.parseColor("#684E26")
            sendRequestTxtSecondOption.setTextColor(textEnabledColor)
        } else {
            buttonRequestSecondOption.isEnabled = false
            val matrix = floatArrayOf(
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
            val colorMatrix = ColorMatrix(matrix)
            colorFilter = ColorMatrixColorFilter(colorMatrix)
            //BLACK AND WHITE FILTER!
            buttonRequestSecondOption.colorFilter = colorFilter

            val textDisabledColor = Color.parseColor("#656565")
            sendRequestTxtSecondOption.setTextColor(textDisabledColor)
        }
    }

    private fun thirdOptionChangeButtonUI(thirdOptionEnabled: Boolean) {
        if (thirdOptionEnabled) {
            buttonRequestThirdOption.isEnabled = true
            buttonRequestThirdOption.colorFilter = null
            val textEnabledColor = Color.parseColor("#684E26")
            sendRequestTxtThirdOption.setTextColor(textEnabledColor)
        } else {
            buttonRequestThirdOption.isEnabled = false
            val matrix = floatArrayOf(
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
            val colorMatrix = ColorMatrix(matrix)
            colorFilter = ColorMatrixColorFilter(colorMatrix)
            //BLACK AND WHITE FILTER!
            buttonRequestThirdOption.colorFilter = colorFilter

            val textDisabledColor = Color.parseColor("#656565")
            sendRequestTxtThirdOption.setTextColor(textDisabledColor)
        }
    }

    private fun getCurrentDate(): String {
        val locale = Locale.getDefault()
        val dateFormat = if (locale == Locale.US) {
            SimpleDateFormat("MM/dd/yyyy", locale)
        } else {
            SimpleDateFormat("dd/MM/yyyy", locale)
        }
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    fun doWithdrawalRequest(moneyAmount: Float, starsCost: Long) {
        if (userStars >= starsCost) {
             bankInfo.get()
                .addOnSuccessListener { documentSnapshot ->
                    documentSnapshot
                        ?.getString("pixKey")
                        ?.let { getPixKey ->
                            recipientPIXKey = getPixKey
                        } ?: run {
                        val dialogSettingsPaymentMethod = DialogSettingsPaymentMethod(this, bankInfo)
                        dialogSettingsPaymentMethod.showSetPaymentDialog(
                            getString(R.string.define_pix_key),
                            getString(R.string.set_a_pix_key_for_us_to_send_you_the_money_to_your_bank_account),
                            getString(R.string.enter_your_pix_key),
                            isPix = true
                        )
                    }
                }
            if (havePIXKey) {
                val getCurrentTimeInMilliSeconds = System.currentTimeMillis()
                val createDocumentID = "$getCurrentTimeInMilliSeconds-$uid"
                val withdrawRequestData = hashMapOf(
                    "amount" to moneyAmount,
                    "pixKey" to recipientPIXKey,
                    "date" to getCurrentDate(),
                    "status" to getString(R.string.pending),
                    "id" to createDocumentID,
                    "uid" to uid
                )
                val doStarsSubtraction = userStars - starsCost
                val subtractionResult = hashMapOf(
                    "coins" to doStarsSubtraction,
                    "vipPoints" to 0
                )
                userData.update(subtractionResult as Map<String, Any>)
                    .addOnSuccessListener {

                        withdrawalsRequests
                            .document(createDocumentID)
                            .set(withdrawRequestData)
                            .addOnSuccessListener {
                                getFriendCodeOwnerUID(friendCode) { friendCodeOwnerUID ->
                                    if (friendCodeOwnerUID != null) {
                                        val bonusCoins = (starsCost * 0.25).toLong()
                                        val friendCodeOwnerRef = db
                                            .collection("USERS")
                                            .document(friendCodeOwnerUID)
                                        friendCodeOwnerRef
                                            .update("coins", FieldValue.increment(bonusCoins))
                                            .addOnSuccessListener {
                                                showCustomSuccessToast(
                                                    this,
                                                    getString(R.string.request_made_successfully_wait_up_to_7_days),
                                                    Toast.LENGTH_LONG,
                                                    this.window
                                                )

                                                val newClickTime = System.currentTimeMillis() + (48 * 60 * 60 * 1000)

                                                with(sharedPreferences.edit()) {
                                                    putLong("lastClickTime", newClickTime)
                                                    apply()
                                                }
                                                loadingDialog.hide()
                                                lastClickTime = sharedPreferences.getLong("lastClickTime", 0)
                                                currentTime = System.currentTimeMillis()
                                                coolDownRemaining = lastClickTime - currentTime
                                                if (coolDownRemaining > 0) {
                                                    cooldownDialog.showCooldownDialog(coolDownRemaining)
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
                                        showCustomErrorToast(
                                            this,
                                            getString(R.string.friend_code_not_found),
                                            Toast.LENGTH_LONG,
                                            this.window
                                        )
                                    }
                                }
                            }
                    }
            }
        } else {
            showCustomErrorToast(
                this,
                getString(R.string.not_enough_coins_collect_more_by_playing),
                Toast.LENGTH_LONG,
                this.window
            )
        }
    }

    private fun getFriendCodeOwnerUID(friendCode: String, callback: (String?) -> Unit) {
        val usersCollection = db.collection("USERS")
        val query = usersCollection.whereEqualTo("myCode", friendCode).limit(1)

        query.get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val document = snapshot.documents[0]
                    val codeOwnerUID = document.id
                    callback(codeOwnerUID)
                } else {
                    callback(null)
                    showCustomErrorToast(
                        this,
                    getString(R.string.friend_code_not_found),
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
                callback(null)
            }
    }
}