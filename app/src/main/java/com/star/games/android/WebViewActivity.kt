package com.star.games.android

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView

class WebViewActivity : AppCompatActivity() {

    private lateinit var screenNameTxt: TextView

    private lateinit var webView: WebView

    private lateinit var backBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        initViews()
        setViewsOnClickListeners()
        textUI()
    }

    private fun textUI() {
        val setShader: Shader = LinearGradient(
            0f, 0f, 0f, screenNameTxt.textSize,
            intArrayOf(
                Color.parseColor("#CED2FD"),
                Color.parseColor("#767B9B")
            ),
            null,
            Shader.TileMode.MIRROR
        )
        screenNameTxt.paint.shader = setShader
    }

    private fun setViewsOnClickListeners() {
        backBtn.setOnClickListener {
            val backToHomeScreen = Intent(this, HomeActivity::class.java)
            startActivity(backToHomeScreen)
            finish()
        }
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        screenNameTxt = findViewById(R.id.screen_name)
        webView = findViewById(R.id.webView)
        backBtn = findViewById(R.id.back_btn)

        if (intent.extras != null) {
            val getExtras: Bundle = intent.extras!!
            val getIntentExtraUrlValue: String = getExtras.getString("url", "www.google.com")
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(getIntentExtraUrlValue)
        }
    }
}