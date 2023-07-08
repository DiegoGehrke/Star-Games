package com.star.games.android

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import com.google.firebase.ktx.Firebase
import com.star.games.android.ErrorToastUtils.showCustomErrorToast
import com.star.games.android.SuccessToastUtils.showCustomSuccessToast

class ReferralActivity : AppCompatActivity() {

    private lateinit var userCodeTxt: TextView
    private lateinit var invitedFriendsCounterTxt: TextView
    private lateinit var screenName: TextView

    private lateinit var backBtn: ImageView
    private lateinit var copyCodeBtn: ImageView
    private lateinit var shareCodeBtn: ImageView
    private lateinit var helpBtn: ImageView

    private var userCodeString: String = "null"

    private var userInvitedFriendsCount: Int = 0

    private val user  = FirebaseAuth.getInstance().currentUser
    private val db = Firebase.firestore
    private val uid = user!!.uid
    private val userData = db.collection("USERS").document(uid)

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var rulesDialog: RulesDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_referral)

        initializeViews()
        fetchUserData()
        setViewsListeners()
        onBackPressedListener()
        textsUI()
    }

    private fun initializeViews() {
        loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        rulesDialog = RulesDialog(this)
        userCodeTxt = findViewById(R.id.user_code)
        backBtn = findViewById(R.id.back_btn)
        copyCodeBtn = findViewById(R.id.copy_code)
        shareCodeBtn = findViewById(R.id.share_code)
        screenName = findViewById(R.id.event_name)
        helpBtn = findViewById(R.id.help_icon)
        invitedFriendsCounterTxt = findViewById(R.id.invited_friends_counter_txt)
    }

    private fun fetchUserData() {
        userData
            .get()
            .addOnSuccessListener { documentSnapshot ->
                documentSnapshot
                    .getString("myCode")
                    .let { myCode ->
                        userCodeString = myCode.toString()
                        updateUI()
                    }
                documentSnapshot
                    .getLong("invitedFriends")
                    .let { friendsInvited ->
                        userInvitedFriendsCount = friendsInvited!!.toInt()
                        updateUI()
                    }
                loadingDialog.hide()
            }
            .addOnFailureListener { exception ->
                showCustomErrorToast(
                    this,
                    getString(R.string.error_, exception.message.toString()),
                    Toast.LENGTH_LONG,
                    this.window
                )
                loadingDialog.hide()
            }
    }

    private fun updateUI() {
        userCodeTxt.text = userCodeString
        invitedFriendsCounterTxt.text = getString(R.string.invited_friends_, userInvitedFriendsCount)
    }

    private fun setViewsListeners() {
        backBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        copyCodeBtn.setOnClickListener {
            copyTextToClipboard(userCodeTxt, this)

            showCustomSuccessToast(
                this,
                getString(R.string.code_successfully_copied_to_your_clipboard),
                Toast.LENGTH_LONG,
                this.window
            )
        }

        shareCodeBtn.setOnClickListener {
            val textToShare = getString(R.string.share_code_string, userCodeString, userCodeString)
            createShareText(textToShare, this)
        }

        helpBtn.setOnClickListener {
            val list: List<String> = listOf(
               getString(R.string.share_your_unique_invite_code_with_your_friends),
                getString(R.string.when_a_friend_signs_up_),
                getString(R.string.each_time_that_friend_spends_stars_in_the_app_you_ll_receive_25_of_the_stars_they_spend_as_a_reward),
                getString(R.string.the_stars_you_earn_can_be_used_to_unlock_amazing_in_app_rewards)
            )
            rulesDialog.showEventRulesDialog(list)
        }
    }

    private fun createShareText(text: String, context: Context) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun copyTextToClipboard(textView: TextView, context: Context) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = android.content.ClipData.newPlainText("Text", textView.text)
        clipboardManager.setPrimaryClip(clipData)
    }

    private fun onBackPressedListener() {
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backToHomeIntent = Intent(
                    this@ReferralActivity,
                    HomeActivity::class.java
                )
                startActivity(backToHomeIntent)
                this@ReferralActivity.finish()
            }
        })
    }

    private fun textsUI() {
        val screenNameShader: Shader = LinearGradient(
            0f, 8f, 0f, screenName.textSize,
            intArrayOf(
                Color.parseColor("#CED2FD"),
                Color.parseColor("#767B9B")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        screenName.paint.shader = screenNameShader

        val userCodeShader: Shader = LinearGradient(
            0f, 12f, 0f, userCodeTxt.textSize,
            intArrayOf(
                Color.parseColor("#92723B"),
                Color.parseColor("#453714")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        userCodeTxt.paint.shader = userCodeShader
    }
}