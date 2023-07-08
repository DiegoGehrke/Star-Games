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
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ManageAccountActivity : AppCompatActivity() {

    private lateinit var screenName: TextView
    private lateinit var showUserEmailTxt: TextView

    private lateinit var logOutBtn: ImageView
    private lateinit var changeEmailBtn: ImageView
    private lateinit var backBtn: ImageView
    private lateinit var changePasswordBtn: ImageView
    private lateinit var setPaymentPreferencesBtn: ImageView

    private lateinit var resetPasswordDialog: ResetPasswordDialog

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_account)

        initializeViews()
        setClicksListeners()
        updateUI()
    }

    private fun initializeViews() {
        screenName = findViewById(R.id.screen_name)
        setPaymentPreferencesBtn = findViewById(R.id.set_payment_preferences_btn)
        changePasswordBtn = findViewById(R.id.change_password_btn)
        changeEmailBtn = findViewById(R.id.change_email_btn)
        backBtn = findViewById(R.id.back_btn)
        logOutBtn = findViewById(R.id.log_out_btn)
        showUserEmailTxt = findViewById(R.id.show_user_email)
        sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        resetPasswordDialog = ResetPasswordDialog(this)
    }

    private fun setClicksListeners() {
        logOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val editor = sharedPrefs.edit()
            editor.remove("FirstName")
            editor.remove("LastName")
            editor.apply()

            val intent = Intent(
                this,
                SignupActivity::class.java
            )
            startActivity(intent)
            finish()
        }

        changeEmailBtn.setOnClickListener {
            val goToChangeEmailActivity = Intent(
                this,
                UpdateAccountEmailActivity::class.java
            )
            startActivity(goToChangeEmailActivity)
            finish()
        }

        backBtn.setOnClickListener {
            val intent = Intent(
                this,
                ConfigurationsActivity::class.java
            )
            startActivity(intent)
            finish()
        }

        changePasswordBtn.setOnClickListener {
            resetPasswordDialog.showResetPassDialog()
        }

        setPaymentPreferencesBtn.setOnClickListener {
            val intent = Intent(
                this,
                SetPaymentMethodActivity::class.java
            )
            startActivity(intent)
        }
    }

    private fun updateUI() {
        textsUI()
        showUserEmailTxt.text = sharedPrefs.getString("email", "")
        val hideEmail= hideEmail(showUserEmailTxt.text.toString())
        showUserEmailTxt.text = hideEmail
    }

    private fun hideEmail(email: String): String {
        val atIndex = email.indexOf('@')
        val username = email.substring(0, Math.min(3, atIndex))
        val domain = email.substring(atIndex)

        return "$username...$domain"
    }

    private fun textsUI() {
        val ts11111: Shader = LinearGradient(
            0f, 12f, 0f, screenName.textSize,
            intArrayOf(
                Color.parseColor("#CED2FD"), // First color
                Color.parseColor("#767B9B") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        screenName.paint.shader = ts11111
    }
}