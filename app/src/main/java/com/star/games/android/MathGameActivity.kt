package com.star.games.android

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class MathGameActivity : AppCompatActivity() {

    private lateinit var firstBtn : ImageView
    private lateinit var secondBtn : ImageView
    private lateinit var thirdBtn : ImageView
    private lateinit var fourthBtn : ImageView
    private lateinit var fifthBtn : ImageView
    private lateinit var sixthBtn : ImageView
    private lateinit var firstBtnTxt : TextView
    private lateinit var secondBtnTxt : TextView
    private lateinit var thirdBtnTxt : TextView
    private lateinit var fourthBtnTxt : TextView
    private lateinit var fifthBtnTxt : TextView
    private lateinit var sixthBtnTxt : TextView
    private lateinit var showQuestionTxt : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_math_game)

        firstBtn = findViewById(R.id.first_btn)
        secondBtn = findViewById(R.id.second_btn)
        thirdBtn = findViewById(R.id.third_btn)
        fourthBtn = findViewById(R.id.fourth_btn)
        fifthBtn = findViewById(R.id.fifth_btn)
        sixthBtn = findViewById(R.id.sixth_btn)
        firstBtnTxt = findViewById(R.id.first_btn_txt)
        secondBtnTxt = findViewById(R.id.second_btn_txt)
        thirdBtnTxt = findViewById(R.id.third_btn_txt)
        fourthBtnTxt = findViewById(R.id.fourth_btn_txt)
        fifthBtnTxt = findViewById(R.id.fifth_btn_txt)
        sixthBtnTxt = findViewById(R.id.sixth_btn_txt)
        showQuestionTxt = findViewById(R.id.questTxt)

        val shader21082937: Shader = LinearGradient(
            0f, 12f, 0f, showQuestionTxt.textSize, // Change the ending y-coordinate to be textSize
            intArrayOf(
                Color.parseColor("#FFE17C"), // First color
                Color.parseColor("#F8D661") // Second color
            ),
            null,
            Shader.TileMode.MIRROR
        )
        showQuestionTxt.paint.shader = shader21082937

        val additionMathGame = AdditionMathGame(
            showQuestionTxt,
            firstBtn,
            secondBtn,
            thirdBtn,
            fourthBtn,
            fifthBtn,
            sixthBtn,
            firstBtnTxt,
            secondBtnTxt,
            thirdBtnTxt,
            fourthBtnTxt,
            fifthBtnTxt,
            sixthBtnTxt
        )
        additionMathGame.generateAdditionQuestion()
    }
}