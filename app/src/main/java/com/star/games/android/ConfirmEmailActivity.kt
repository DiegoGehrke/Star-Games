package com.star.games.android

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.star.games.android.ErrorToastUtils.showCustomErrorToast

class ConfirmEmailActivity : AppCompatActivity() {

    private lateinit var loadingDialog: LoadingDialog

    private lateinit var cooldownTimer: CountDownTimer
    private val cooldownDuration: Long = 30000

    private lateinit var sharedPrefs: SharedPreferences

    private lateinit var resendOtpTxt: TextView
    private lateinit var message: TextView

    private lateinit var emailVerifiedBtn: ImageView
    private lateinit var useAnotherEmailBtn: ImageView

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_email)

        initializeViews()
        sendOtpCode()
        addViewClickListener()
    }

    private fun addViewClickListener() {
        resendOtpTxt.setOnClickListener {
            resendOtpTxt.isEnabled = false
            startCooldownTimer()
            sendOtpCode()
        }

        emailVerifiedBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        useAnotherEmailBtn.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun sendOtpCode() {
        loadingDialog.show()
        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loadingDialog.hide()
                } else {
                    showCustomErrorToast(
                        this,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_LONG,
                        this.window
                    )
                    loadingDialog.hide()
                }
            }
    }

    private fun initializeViews() {
        loadingDialog = LoadingDialog(this)
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser!!
        resendOtpTxt = findViewById(R.id.resend_otp_txt)

        sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        message = findViewById(R.id.message)
        val getCurrentUserEmail: String = sharedPrefs.getString("email", "")?: ""
        message.text = getString(R.string.we_send_a_link_to_your_email_, getCurrentUserEmail)

        emailVerifiedBtn = findViewById(R.id.email_verified_btn)
        useAnotherEmailBtn = findViewById(R.id.use_another_email_btn)

        val sharedPreferences = getSharedPreferences("CooldownPrefs", Context.MODE_PRIVATE)
        val endTime = sharedPreferences.getLong("cooldownEndTime", 0)
        val currentTime = System.currentTimeMillis()
        val remainingTime = endTime - currentTime

        if (remainingTime > 0) {
            startCooldownTimer(remainingTime = remainingTime)
        }
    }

    private fun startCooldownTimer(remainingTime: Long = cooldownDuration) {
        val currentTime = System.currentTimeMillis()
        val endTime = currentTime + cooldownDuration

        val sharedPreferences = getSharedPreferences("CooldownPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("cooldownEndTime", endTime)
        editor.apply()

        cooldownTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                resendOtpTxt.text = getString(R.string.cooldown_text, secondsRemaining)
            }

            override fun onFinish() {
                resendOtpTxt.isEnabled = true
                resendOtpTxt.text = getString(R.string.resend_e_mail)
                cooldownTimer.cancel()
            }
        }.start()
    }
}