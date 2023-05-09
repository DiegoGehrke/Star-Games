package com.star.games.android

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val textView : TextView = findViewById(R.id.welcome_txt)
        val signupTxt : TextView = findViewById(R.id.signup_txt)
        val loginTxt : TextView = findViewById(R.id.login_txt)

        val textShader: Shader = LinearGradient(
            0f, 24f, 0f, textView.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#CED2FD"), // First color
                Color.parseColor("#767B9B") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        textView.paint.shader = textShader

        val textShader1: Shader = LinearGradient(
            0f, 24f, 0f, textView.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#66422A"), // First color
                Color.parseColor("#291000") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        loginTxt.paint.shader = textShader1

        val textShader2: Shader = LinearGradient(
            0f, 24f, 0f, textView.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#D7B684"), // First color
                Color.parseColor("#8B7744") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        signupTxt.paint.shader = textShader2
    }
}