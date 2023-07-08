package com.star.games.android

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConfigurationsActivity : AppCompatActivity() {

    private lateinit var screenName: TextView

    private lateinit var goToHelpSupportActivity: ImageView
    private lateinit var goToManageAccountActivity: ImageView
    private lateinit var goToReadPolicyPrivacy: ImageView
    private lateinit var goToReadTermsOfUseBtn: ImageView
    private lateinit var backBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configurations)

        initializeViews()
        setViewsClickListeners()
        textUI()
    }

    private fun initializeViews() {
        screenName = findViewById(R.id.screen_name)
        backBtn = findViewById(R.id.back_btn)
        goToManageAccountActivity = findViewById(R.id.manage_account_btn)
        goToHelpSupportActivity = findViewById(R.id.help_support_btn)
        goToReadPolicyPrivacy = findViewById(R.id.policy_privacy_btn)
        goToReadTermsOfUseBtn = findViewById(R.id.terms_of_use_btn)
    }

    private fun setViewsClickListeners() {
        goToHelpSupportActivity.setOnClickListener {
            val intent = Intent(this, SendEmailToSupportActivity::class.java)
            startActivity(intent)
            finish()
        }

        goToManageAccountActivity.setOnClickListener {
            val intent = Intent(this, ManageAccountActivity::class.java)
            startActivity(intent)
            finish()
        }

        goToReadPolicyPrivacy.setOnClickListener {
            val url = "https://stargames88.wordpress.com/"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        goToReadTermsOfUseBtn.setOnClickListener {
            val url = "https://stargames88.wordpress.com/terms-of-use/"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        backBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun textUI() {
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
    }
}